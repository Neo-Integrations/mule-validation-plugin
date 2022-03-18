package org.neointegration.mule.validation;

import amf.client.model.domain.*;
import amf.client.validate.ValidationReport;
import org.neointegration.mule.validation.domain.RAMLRule;
import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.Status;
import webapi.Raml10;
import webapi.WebApiBaseUnit;
import webapi.WebApiDocument;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;

public enum RAMLValidator implements Validator{
    INSTANCE;

    private static WebApiDocument model = null;

    @Override
    public void validate(final File ramlFile, final Rule rule) throws ExecutionException, InterruptedException {

        rule.setFinished(true);

        if(PluginUtil.isNull(rule.getRamlRule())) {
            Result result = Result.instance(ramlFile, null, true);
            result.setNodeName(ramlFile.getName());
            rule.addToMapList(result);
            return;
        }

        final File file = PluginUtil.getRAMLFile(rule.getProjectDir());
        if(PluginUtil.isNull(file)) {
            System.out.println("****Warning: ["+rule.getRuleId() +"] No RAML found ****");
            return;
        }

        final WebApi api = this.parseRAML( file.getAbsolutePath());

        // If JEXL expression language used then only evaluate that and
        // ignore all other standard RAML validation
        if(rule.getRamlRule().getJexlExpression() != null) {
            dynamicRAMLValidationRule(ramlFile, rule, api);
            return;
        }

        boolean validateRAML = validateRAML(ramlFile, rule, api);
        boolean validateTitle = validateTitle(ramlFile, rule, api);
        boolean validateDescription = validateDescription(ramlFile, rule, api);
        boolean validateDocumentationTag = validateDocumentationTag(ramlFile, rule, api);
        boolean validateSecuritySchemes = validateSecuritySchemes(ramlFile, rule, api);
        boolean validateOAuth2SecuritySchemes = validateOAuth2SecuritySchemes(ramlFile, rule, api);

        boolean validateClientIdSecuritySchemes = validateClientIdSecuritySchemes(ramlFile, rule, api);
        //if(validateClientIdSecuritySchemes == false) {
        //    validateTraitsClientIdEnforcement(ramlFile, rule, api);
        //}
        boolean validateResponseBody = validateResponseBody(ramlFile, rule, api);
        boolean validateRequestBody = validateRequestBody(ramlFile, rule, api);
        boolean validateCorrelationId = validateCorrelationId(ramlFile, rule, api);



    }

    private void dynamicRAMLValidationRule(final File ramlFile,
                                           final Rule rule,
                                           final WebApi api) {
        if(PluginUtil.isNull(rule.getRamlRule().getJexlExpressionExpectedValue())) {
            throw new IllegalArgumentException("jexlExpressionExpectedValue must be provided when jexlExpression is provided");
        }

        if(PluginUtil.isNull(rule.getRamlRule().getJexlExpressionMatchingOperation())) {
            throw new IllegalArgumentException("jexlExpressionMatchingOperation must be provided when jexlExpression is provided");
        }
        Result result = Result.instance(ramlFile, null, true);
        rule.addToMapList(result);
        result.setNodeName(rule.getRamlRule().getJexlExpression());

        Object object = PluginUtil.eval(api, rule.getRamlRule().getJexlExpression());
        String expression = "obj " +
                rule.getRamlRule().getJexlExpressionMatchingOperation() + " " +
                rule.getRamlRule().getJexlExpressionExpectedValue();

        object = PluginUtil.eval(object, expression);
        if(PluginUtil.isNull(object) || object.equals(false)) {
            result.setSummary("Unable to match the expression: [" + rule.getRamlRule().getJexlExpression() + "]");
            result.setStatus(Status.FAILED);
        }
    }

    private boolean validateCorrelationId(File ramlFile,
                                        Rule rule,
                                        WebApi api) {

        if(rule.getRamlRule().isValidateTraceability()) {
            Result result = Result.instance(ramlFile, null, false);
            result.setSummary("Traceability traits does not exists");
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.X_CORRELATION_ID.toString());

            for(EndPoint ep : api.endPoints()) {
                if(PluginUtil.isNull(ep)) continue;
                if(findCorrelationTrait(ep.extendsNode())) {
                    result.setStatus(Status.PASSED);
                    result.setSummary(null);
                    return true;
                }

                for(Operation ops: ep.operations()) {
                    if(PluginUtil.isNull(ops)) continue;
                    if(findCorrelationTrait(ops.extendsNode())) {
                        result.setStatus(Status.PASSED);
                        result.setSummary(null);
                        return true;
                    }
                }
            }

        }
        return false;
    }

    private boolean findCorrelationTrait(List<DomainElement> items) {
        for(DomainElement element: items) {
            if(PluginUtil.isNull(element) ||
                    (element instanceof ParametrizedTrait) == false) continue;

            ParametrizedTrait trait = (ParametrizedTrait) element;
            if(PluginUtil.isNull(trait.name()) ||
                    PluginUtil.isNullOrEmpty(trait.name().value())) continue;

            if(PluginUtil.isNull( trait.target()) ||
                    PluginUtil.isNull(trait.target().linkTarget()) ||
                    PluginUtil.isNull(trait.target().linkTarget().get())) continue;

            ObjectNode refTrait = (ObjectNode) ((Trait) trait.target().linkTarget().get()).dataNode();

            if(PluginUtil.isNull(refTrait) ||
                    PluginUtil.isNull(refTrait.properties())) continue;

            ObjectNode trHeaders = (ObjectNode) refTrait.properties().get("headers");

            if(PluginUtil.isNull(trHeaders) ||
                    PluginUtil.isNull(trHeaders.properties())) continue;

            for(String key : trHeaders.properties().keySet()) {
                if(PluginUtil.isNotNullAndEmpty(key)) {
                    if(key.toLowerCase().matches("x-correlation-id[\\?]{0,1}")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateRequestBody(File ramlFile,
                                         Rule rule,
                                         WebApi api) {

        if(rule.getRamlRule().isValidateRequestBody()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.REQUEST_BODY.toString());

            for(EndPoint ep : api.endPoints()) {
                if(PluginUtil.isNull(ep)) continue;
                String path = ep.path().value();

                for(DomainElement rt : ep.extendsNode()) {
                    if(rt instanceof ParametrizedResourceType) {
                        // If there is a common parametrized ResourceType under an endpoint
                        // it will fulfill requirement of having a request body
                       return true;
                    }
                }

                for(Operation ops: ep.operations()) {
                    String method = ops.method().value().toLowerCase();
                    if(PluginUtil.isNull(ops) ||
                            method.equals("get") ||
                            method.equals("head") ||
                            method.equals("delete"))
                        continue;


                    if(PluginUtil.isNull(ops.request())) {
                        result.setStatus(Status.FAILED);
                        result.setNodeName(method.toUpperCase() + " " + path);
                        result.setSummary("validateRequestBody: Request has not been defined for the method "+method);
                        return false;
                    }

                    if(PluginUtil.isNullOrEmpty(ops.request().payloads())) {
                        result.setStatus(Status.FAILED);
                        result.setNodeName(method.toUpperCase() + " " + path);
                        result.setSummary("validateRequestBody: Request body has not been defined for the method "+method);
                        return false;
                    }
                    for(Payload payload : ops.request().payloads()) {

                        if(PluginUtil.isNull(payload.mediaType()) ||
                                PluginUtil.isNullOrEmpty(payload.mediaType().value())) {
                            result.setStatus(Status.FAILED);
                            result.setNodeName(method.toUpperCase() + " " + path);
                            result.setSummary("validateRequestBody: Request body payload media type not defined");
                            return false;
                        }

                        if(PluginUtil.isNull(payload.schema()) &&
                                PluginUtil.isNullOrEmpty(payload.schema().inherits()) &&
                                PluginUtil.isNullOrEmpty(payload.examples())) {
                            result.setStatus(Status.FAILED);
                            result.setNodeName(method.toUpperCase() + " " + path);
                            result.setSummary("validateRequestBody: No payload schema type or example has been defined");
                            return false;
                        }
                    }
                }
            }
        }



        return true;
    }

    private boolean validateResponseBody(File ramlFile,
                                                      Rule rule,
                                                      WebApi api) {

        if(rule.getRamlRule().isValidateResponseBody()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.RESPONSE_BODY.toString());

            for(EndPoint ep : api.endPoints()) {
                if(PluginUtil.isNull(ep)) continue;

                for(Operation ops: ep.operations()) {
                    if(PluginUtil.isNull(ops)) continue;
                    for(Response response: ops.responses()) {
                        if(PluginUtil.isNull(response.statusCode()) ||
                                PluginUtil.isNullOrEmpty(response.statusCode().value()) ) {
                            result.setStatus(Status.FAILED);
                            result.setNodeName(ops.method().value() + " " + ep.path().value());
                            result.setSummary("validateResponseBody: Response Status Code not defined");
                            return false;
                        }

                        if(PluginUtil.isNullOrEmpty(response.payloads())) {
                            result.setStatus(Status.FAILED);
                            result.setNodeName(ops.method().value() + " " + ep.path().value());
                            result.setSummary("validateResponseBody: No response Payload defined");
                            return false;
                        }
                        for(Payload payload : response.payloads()) {
                            if(PluginUtil.isNull(payload.mediaType()) ||
                                    PluginUtil.isNullOrEmpty(payload.mediaType().value())) {
                                result.setStatus(Status.FAILED);
                                result.setNodeName(ops.method().value() + " " + ep.path().value());
                                result.setSummary("validateResponseBody: No payload media type defined");
                                return false;
                            }

                            if(PluginUtil.isNull(payload.schema()) &&
                                    PluginUtil.isNullOrEmpty(payload.schema().inherits()) &&
                                    PluginUtil.isNullOrEmpty(payload.examples())) {
                                result.setStatus(Status.FAILED);
                                result.setNodeName(ops.method().value() + " " + ep.path().value());
                                result.setSummary("validateResponseBody: No payload schema type or example has been defined");
                                return false;
                            }
                        }

                    }
                }
            }
        }
        return true;
    }

    private boolean validateTraitsClientIdEnforcement(File ramlFile,
                                                  Rule rule,
                                                  WebApi api) {

        if(rule.getRamlRule().isValidateClientIdEnforcementScheme()) {
            Result result = Result.instance(ramlFile, null, false);
            result.setSummary("ClientId Enforcement traits does not exists");
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.CLIENT_ID_ENFORCEMENT.toString());

            for(EndPoint ep : api.endPoints()) {
                if(PluginUtil.isNull(ep)) continue;
                for(Operation ops: ep.operations()) {
                    if(PluginUtil.isNull(ops)) continue;
                    for(DomainElement element: ops.extendsNode()) {
                        if(PluginUtil.isNull(element)) continue;
                        ParametrizedTrait trait = (ParametrizedTrait) element;
                        if(PluginUtil.isNull(trait.name()) ||
                                PluginUtil.isNullOrEmpty(trait.name().value())) continue;
                        if(trait.name().value().toLowerCase().matches("client[-_]{0,1}id[-_]{0,1}(.*)")) {
                            result.setStatus(Status.PASSED);
                            result.setSummary(null);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean validateOAuth2SecuritySchemes(File ramlFile,
                                                  Rule rule,
                                                  WebApi api) {
        if(rule.getRamlRule().isValidateOAuth2Scheme()) {
            return validateSecuritySchemes(ramlFile, rule, api, "(.*)oauth(.*)");
        }

        return false;
    }

    private boolean validateClientIdSecuritySchemes(File ramlFile,
                                                  Rule rule,
                                                  WebApi api) {
        if(rule.getRamlRule().isValidateClientIdEnforcementScheme()) {
            return validateSecuritySchemes(ramlFile, rule, api, "(.*)client[-_]{0,1}id(.*)");
        }
        return false;
    }

    private boolean validateSecuritySchemes(File ramlFile,
                                            Rule rule,
                                            WebApi api, String schemaName) {

        Result result = Result.instance(ramlFile, null, false);
        result.setSummary(schemaName + " Schema has not been specified");
        rule.addToMapList(result);
        result.setNodeName(RAMLRule.JEXLExpression.OAUTH2_SECURITY_SCHEMA.toString());

        if(PluginUtil.isNullOrEmpty(api.security())) return false;
        for(SecurityRequirement sr : api.security()) {
            for(ParametrizedSecurityScheme pSR : sr.schemes()) {

                if(PluginUtil.isNull(pSR.scheme()) ||
                        PluginUtil.isNull(pSR.scheme().name()) ||
                        PluginUtil.isNullOrEmpty(pSR.scheme().name().value())) {
                    continue;
                }

                if(pSR.scheme().name().value().toLowerCase().matches(schemaName)) {
                    result.setStatus(Status.PASSED);
                    result.setSummary(null);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean validateSecuritySchemes(File ramlFile,
                                             Rule rule,
                                             WebApi api) {
        if(rule.getRamlRule().isValidateSecuritySchemes()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.SECURITY_SCHEMA.toString());
            Object object = PluginUtil.eval(api, RAMLRule.JEXLExpression.SECURITY_SCHEMA.get());
            if(PluginUtil.isNullOrEmpty(object)) {
                result.setStatus(Status.FAILED);
                result.setSummary("API Specification does not contain a security schema");
                return false;
            }
        }
        return true;
    }

    private boolean validateDocumentationTag(File ramlFile,
                                        Rule rule,
                                        WebApi api) {
        if(rule.getRamlRule().isValidateDocumentation()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.DOCUMENTATION.toString());
            Object object = PluginUtil.eval(api, RAMLRule.JEXLExpression.DOCUMENTATION.get());
            if(PluginUtil.isNullOrEmpty(object)) {
                result.setStatus(Status.FAILED);
                result.setSummary("API Specification does not contain a documentation content tag");
                return false;
            }
        }
        return true;
    }

    private boolean validateDescription(File ramlFile,
                                  Rule rule,
                                  WebApi api) {
        if(rule.getRamlRule().isValidateDescription()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.DESCRIPTION.toString());
            if(PluginUtil.isNull(api.description()) ||
                    PluginUtil.isNullOrEmpty(api.description().value())) {
                result.setStatus(Status.FAILED);
                result.setSummary("API Specification description has not been defined");
                return false;
            }
        }
        return true;
    }

    private boolean validateTitle(File ramlFile,
                                  Rule rule,
                                  WebApi api) {
        if(rule.getRamlRule().isValidateTitle()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.TITLE.toString());
            if(PluginUtil.isNull(api.name()) || PluginUtil.isNullOrEmpty(api.name().value())) {
                result.setStatus(Status.FAILED);
                result.setSummary("API Specification title has not been defined");
                return false;
            }
        }
        return true;
    }

    private boolean validateRAML(File ramlFile,
                                  Rule rule,
                                  WebApi api) {
        if(rule.getRamlRule().isValidateRaml()) {
            Result result = Result.instance(ramlFile, null, true);
            rule.addToMapList(result);
            result.setNodeName(RAMLRule.JEXLExpression.VALIDATE_RAML.toString());
            try {
                final ValidationReport report = Raml10.validate((WebApiBaseUnit) model).get();

                if (!report.conforms()) {
                    result.setStatus(Status.FAILED);
                    result.setSummary("Error: API Specification could not be validated.");
                    return false;
                }
            } catch(Exception exp) {
                result.setStatus(Status.FAILED);
                result.setSummary("Error: Error while validating API Specification. [" + exp.getMessage() + "]");
                return false;
            }
        }
        return true;
    }

    // This method assume a serial execution of the rules using a single thread.
    // In a multithreaded environment, use synchronisation pattern
    private WebApi parseRAML(String fileLocation) throws ExecutionException, InterruptedException {
        String projectDir = "file://" + fileLocation;
        if(model == null) {
            model = (WebApiDocument) Raml10.parse(projectDir).get();
        }
        return (WebApi) model.encodes();
    }
}
