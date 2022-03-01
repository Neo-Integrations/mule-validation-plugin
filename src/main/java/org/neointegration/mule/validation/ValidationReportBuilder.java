package org.neointegration.mule.validation;

import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.RuleResult;
import org.neointegration.mule.validation.domain.Severity;
import org.neointegration.mule.validation.domain.Status;
import freemarker.template.*;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


/**
 * Utility class for creating reports
 */
public class ValidationReportBuilder {

    private static final String REPORT_TEMPLATE = "report.ftl";

    public void printToConsole(List<RuleResult> ruleResultList) {
        System.out.println("************************ Static Code Validation Results Start **********************************************");

        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-16s %-8s %-70s", "Rule ID", "Status", "Rule Description");
        System.out.println();
        System.out.println("------------------------------------------------------------------------------------------------------------");


        int passed = 0 ;
        int failed = 0;
        for (RuleResult ruleResult : ruleResultList) {
            System.out.printf("%-16s %-8s %-70s", ruleResult.getRule().getRuleId(), ruleResult.getStatus(), ruleResult.getRule().getDescription());
            System.out.println();

            if(ruleResult.getStatus() != Status.FAILED) {
                System.out.println("");
                passed = passed + 1;
                continue;
            }

            failed = failed + 1;

            for (Result result : ruleResult.getResults()) {
                if(PluginUtil.isNullOrEmpty(result.getSummary())) continue;
                if(ruleResult.getRule().getSeverity() == Severity.CRITICAL ||
                        ruleResult.getRule().getSeverity() == Severity.MAJOR) {
                    System.out.printf("%-16s %-8s %-70s", "","",  "Error: " + result.getSummary());
                    System.out.println("");
                } else {
                    System.out.printf("%-16s %-8s %-70s", "", "", "Warning: " + result.getSummary());
                    System.out.println("");
                }
            }
            System.out.println("");
        }

        System.out.println("------------------------------------------------------------------------------------------------------------");
        System.out.println("");
        System.out.println("---------------------");
        System.out.printf("%-2s %6s %-2s %5d %-2s", "|", "Passed", "|", passed, "|");
        System.out.println();
        System.out.printf("%-2s %6s %-2s %5d %-2s", "|", "Failed", "|",  failed, "|");
        System.out.println();
        System.out.println("---------------------");
        System.out.println("");


        System.out.println("************************ Static Code Validation Results End ************************************************");
    }
    public void createXMLReport(String reportPath,
                                String reportFileName,
                                List<RuleResult> ruleResultList) throws MojoExecutionException {
        File directory = new File(reportPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(reportPath + File.separator + reportFileName);

        try (Writer fileWriter = new FileWriter(file)) {
            Configuration cfg = new Configuration(new Version(2, 3, 28));
            cfg.setClassForTemplateLoading(ValidationReportBuilder.class, "/");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            int passed =0 ;
            int failed = 0;
            for(RuleResult eachResult: ruleResultList) {
               if(eachResult.getStatus() == Status.FAILED) {
                   failed = failed + 1;
                   continue;
               }
               passed = passed + 1;
            }

            Map<String, Object> input = new HashMap<>();
            input.put("failedSummary", failed);
            input.put("passedSummary", passed);
            input.put("failedRules", ruleResultList);

            Template template = cfg.getTemplate(REPORT_TEMPLATE);
            template.process(input, fileWriter);

        } catch (IOException | TemplateException ex) {
            throw new MojoExecutionException("Can't create report " + ex.getMessage());
        }
    }
}
