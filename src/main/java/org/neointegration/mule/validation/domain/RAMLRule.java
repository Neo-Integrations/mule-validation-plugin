package org.neointegration.mule.validation.domain;

import java.util.List;

public class RAMLRule {

    private boolean validateRaml;
    private boolean validateTitle;
    private boolean validateDescription;
    private boolean validateDocumentation;
    private boolean validateSecuritySchemes;
    private boolean validateOAuth2Scheme;
    private boolean validateClientIdEnforcementScheme;
    private boolean validateResponseBody;
    private boolean validateRequestBody;
    private boolean validateTraceability;
    private boolean validateErrorResponse;

    private String jexlExpression;
    private String jexlExpressionExpectedValue;
    private String jexlExpressionMatchingOperation;

    public boolean isValidateTitle() {
        return validateTitle;
    }

    public void setValidateTitle(boolean validateTitle) {
        this.validateTitle = validateTitle;
    }

    public boolean isValidateDescription() {
        return validateDescription;
    }

    public void setValidateDescription(boolean validateDescription) {
        this.validateDescription = validateDescription;
    }

    public boolean isValidateDocumentation() {
        return validateDocumentation;
    }

    public void setValidateDocumentation(boolean validateDocumentation) {
        this.validateDocumentation = validateDocumentation;
    }

    public boolean isValidateSecuritySchemes() {
        return validateSecuritySchemes;
    }

    public void setValidateSecuritySchemes(boolean validateSecuritySchemes) {
        this.validateSecuritySchemes = validateSecuritySchemes;
    }

    public boolean isValidateOAuth2Scheme() {
        return validateOAuth2Scheme;
    }

    public void setValidateOAuth2Scheme(boolean validateOAuth2Scheme) {
        this.validateOAuth2Scheme = validateOAuth2Scheme;
    }

    public boolean isValidateClientIdEnforcementScheme() {
        return validateClientIdEnforcementScheme;
    }

    public void setValidateClientIdEnforcementScheme(boolean validateClientIdEnforcementScheme) {
        this.validateClientIdEnforcementScheme = validateClientIdEnforcementScheme;
    }

    public boolean isValidateResponseBody() {
        return validateResponseBody;
    }

    public void setValidateResponseBody(boolean validateResponseBody) {
        this.validateResponseBody = validateResponseBody;
    }

    public boolean isValidateRequestBody() {
        return validateRequestBody;
    }

    public void setValidateRequestBody(boolean validateRequestBody) {
        this.validateRequestBody = validateRequestBody;
    }

    public boolean isValidateTraceability() {
        return validateTraceability;
    }

    public void setValidateTraceability(boolean validateTraceability) {
        this.validateTraceability = validateTraceability;
    }

    public boolean isValidateErrorResponse() {
        return validateErrorResponse;
    }

    public void setValidateErrorResponse(boolean validateErrorResponse) {
        this.validateErrorResponse = validateErrorResponse;
    }

    public boolean isValidateRaml() {
        return validateRaml;
    }

    public void setValidateRaml(boolean validateRaml) {
        this.validateRaml = validateRaml;
    }

    public String getJexlExpression() {
        return jexlExpression;
    }

    public void setJexlExpression(String jexlExpression) {
        this.jexlExpression = jexlExpression;
    }

    public String getJexlExpressionExpectedValue() {
        return jexlExpressionExpectedValue;
    }

    public void setJexlExpressionExpectedValue(String jexlExpressionExpectedValue) {
        this.jexlExpressionExpectedValue = jexlExpressionExpectedValue;
    }

    public String getJexlExpressionMatchingOperation() {
        return jexlExpressionMatchingOperation;
    }

    public void setJexlExpressionMatchingOperation(String jexlExpressionMatchingOperation) {
        this.jexlExpressionMatchingOperation = jexlExpressionMatchingOperation;
    }

    public static enum JEXLExpression {
        VALIDATE_RAML(""),
        TITLE("obj.name().value()"),
        DESCRIPTION("obj.description().value()"),
        DOCUMENTATION("obj.documentations().get(0).description().value()"),
        SECURITY_SCHEMA("obj.security().get(0).schemes().get(0).scheme().name().value()"),
        OAUTH2_SECURITY_SCHEMA(""),
        CLIENT_ID_ENFORCEMENT(""),
        RESPONSE_BODY(""),
        REQUEST_BODY(""),
        X_CORRELATION_ID("");

        private String expression;
        private JEXLExpression(String expression) {
            this.expression = expression;
        }
        public String get() {
            return this.expression;
        }
    }

    @Override
    public String toString() {
        return "RAMLRule{" +
                "validateRaml=" + validateRaml +
                ", validateTitle=" + validateTitle +
                ", validateDescription=" + validateDescription +
                ", validateDocumentation=" + validateDocumentation +
                ", validateSecuritySchemes=" + validateSecuritySchemes +
                ", validateOAuth2Scheme=" + validateOAuth2Scheme +
                ", validateClientIdEnforcementScheme=" + validateClientIdEnforcementScheme +
                ", validateResponseBody=" + validateResponseBody +
                ", validateRequestBody=" + validateRequestBody +
                ", validateTraceability=" + validateTraceability +
                ", validateErrorResponse=" + validateErrorResponse +
                ", jexlExpression='" + jexlExpression + '\'' +
                ", jexlExpressionExpectedValue='" + jexlExpressionExpectedValue + '\'' +
                ", jexlExpressionMatchingOperation='" + jexlExpressionMatchingOperation + '\'' +
                '}';
    }
}
