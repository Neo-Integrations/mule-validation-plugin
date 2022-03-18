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
public enum ReportBuilder {

    INSTANCE;

    public static final String GREEN_BOLD = "\033[1;92m";  // BOLD GREEN
    public static final String GREEN = "\033[0;92m";   // GREEN
    public static final String RED = "\033[0;91m";     // RED
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String YELLOW_BOLD = "\033[0;93m"; // YELLOW
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE

    private static final String REPORT_TEMPLATE = "report.ftl";

    public void printToConsole(List<RuleResult> ruleResultList) {
        System.out.println(GREEN_BOLD + "************************ Static Code Validation Results Start **********************************************" + ANSI_RESET);

        System.out.println(PURPLE_BRIGHT + "------------------------------------------------------------------------------------------------------------" + ANSI_RESET);
        System.out.printf(YELLOW_BOLD + "%-16s %-8s %-70s", "Rule ID", "Status", "Rule Description" + ANSI_RESET);
        System.out.println();
        System.out.println(PURPLE_BRIGHT + "------------------------------------------------------------------------------------------------------------" + ANSI_RESET);


        int passed = 0 ;
        int failed = 0;
        int skipped = 0;
        for (RuleResult ruleResult : ruleResultList) {
            if(ruleResult.getStatus() == Status.FAILED) {
                System.out.printf(RED+ "%-16s %-8s %-70s", ruleResult.getRule().getRuleId(), ruleResult.getStatus(), ruleResult.getRule().getDescription() + ANSI_RESET);
            } else {
                System.out.printf(GREEN+ "%-16s %-8s %-70s", ruleResult.getRule().getRuleId(), ruleResult.getStatus(), ruleResult.getRule().getDescription() + ANSI_RESET);
            }

            System.out.println();

            if(ruleResult.getStatus() == Status.PASSED) {
                System.out.println("");
                passed = passed + 1;
                continue;
            }

            if(ruleResult.getStatus() == Status.SKIP) {
                System.out.println("");
                skipped = skipped + 1;
                continue;
            }

            failed = failed + 1;

            for (Result result : ruleResult.getResults()) {
                if(PluginUtil.isNullOrEmpty(result.getSummary())) continue;
                if(ruleResult.getRule().getSeverity() == Severity.CRITICAL ||
                        ruleResult.getRule().getSeverity() == Severity.MAJOR) {
                    System.out.printf(RED + "%-16s %-8s %-70s", "","",  "Error: " + result.getSummary() + ANSI_RESET);
                    System.out.println("");
                } else {
                    System.out.printf(PURPLE_BRIGHT + "%-16s %-8s %-70s", "", "", "Warning: " + result.getSummary() + ANSI_RESET);
                    System.out.println("");
                }
            }
            System.out.println("");
        }

        System.out.println(PURPLE_BRIGHT + "------------------------------------------------------------------------------------------------------------" + ANSI_RESET);
        System.out.println("");
        System.out.println(PURPLE_BRIGHT + "--------------------" + ANSI_RESET);
        System.out.printf(GREEN + "%-2s %-8s %-2s %-3d %-2s", "|", "Passed", "=", passed, "|" + ANSI_RESET);
        System.out.println();
        System.out.printf(RED + "%-2s %-8s %-2s %-3d %-2s", "|", "Failed", "=",  failed, "|" + ANSI_RESET);
        System.out.println();
        System.out.printf(GREEN + "%-2s %-8s %-2s %-3d %-2s", "|", "Skipped", "=",  skipped, "|" + ANSI_RESET);
        System.out.println();
        System.out.println(PURPLE_BRIGHT+ "--------------------" + ANSI_RESET);
        System.out.println("");


        System.out.println(GREEN_BOLD + "************************ Static Code Validation Results End ************************************************" + ANSI_RESET);
    }
    public void createHtmlReport(String reportPath,
                                String reportFileName,
                                List<RuleResult> ruleResultList) throws MojoExecutionException {

        File directory = new File(reportPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(reportPath + File.separator + reportFileName);

        try (Writer fileWriter = new FileWriter(file)) {
            Configuration cfg = new Configuration(new Version(2, 3, 28));
            cfg.setClassForTemplateLoading(ReportBuilder.class, "/");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

            int passed =0 ;
            int failed = 0;
            int skipped = 0;
            for(RuleResult eachResult: ruleResultList) {
               if(eachResult.getStatus() == Status.FAILED) {
                   failed = failed + 1;
               }
               else if(eachResult.getStatus() == Status.SKIP) {
                    skipped = skipped + 1;
                } else {
                   passed = passed + 1;
               }
            }

            Map<String, Object> input = new HashMap<>();
            input.put("failedSummary", failed);
            input.put("passedSummary", passed);
            input.put("skippedSummary", skipped);
            input.put("failedRules", ruleResultList);

            Template template = cfg.getTemplate(REPORT_TEMPLATE);
            template.process(input, fileWriter);

        } catch (IOException | TemplateException ex) {
            throw new MojoExecutionException("Can't create report " + ex.getMessage());
        }
    }
}
