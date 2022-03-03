package org.neointegration.mule.validation;


import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.RuleResult;
import org.neointegration.mule.validation.domain.Severity;
import org.neointegration.mule.validation.domain.Status;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "validate")
public class ValidationMojo extends AbstractMojo {

    @Parameter(property = "project.dir")
    private String projectDir;

    @Parameter(property = "rules.path", defaultValue = "src/test/resources")
    private String ruleFileLoc;

    @Parameter(property = "rules.file.name", defaultValue = "app-rule.json")
    private String ruleFileName;

    @Parameter(property = "report.path", defaultValue = "target/validationReport")
    private String reportPath;

    @Parameter(property = "report.file.name", required = false ,defaultValue = "report.html")
    private String reportFileName;

   @Override
    public void execute() throws MojoExecutionException {
        try {
            final List<RuleResult> resultList = new ArrayList<>();
            projectDir = this.trimPath(projectDir);
            final String ruleFileDir = new StringBuilder()
                    .append(projectDir)
                    .append(File.separator)
                    .append(ruleFileLoc).toString();

            final List<Rule> rules = PluginUtil.loadRuleFile(ruleFileDir, ruleFileName);

            for (Rule rule : rules) {
                if(!rule.isActive()) continue; // Skip the rule
                rule.setProjectDir(projectDir);
                resultList.add(rule.analyse());
            }

            final ValidationReportBuilder report = new ValidationReportBuilder();
            report.printToConsole(resultList);
            final String reportFilePath = new StringBuilder()
                    .append(projectDir)
                    .append(File.separator)
                    .append(reportPath).toString();
            report.createXMLReport(reportFilePath, reportFileName, resultList);

            for(RuleResult result: resultList) {
                if(result.getStatus() == Status.FAILED &&
                        (result.getRule().getSeverity() == Severity.MAJOR ||
                                result.getRule().getSeverity() == Severity.CRITICAL)) {
                    throw new Exception(result.getRule().getDescription());
                }
            }

        } catch (Exception ex) {
            throw new MojoExecutionException(ex.getMessage(), ex);
        }

    }

    private String trimPath(final String projectDir) {
        String dir = projectDir;

        if(projectDir == null) {
            dir = this.curDir();
        } else { // Check if the provided path is relative or absolute
            if(dir.matches("^[/\\\\](.*)$") == false &&
                    dir.matches("^[a-zA-Z]{1}:(.*)$") == false) {
                dir = this.curDir() + File.separator + dir;
            }
        }
        if (dir.endsWith("/") || dir.endsWith("\\")) { // Trim the ending slashes
            dir = dir.substring(0, dir.length() - 1);
        }
        return dir;
    }

    private  String curDir() {
        return Paths.get("").toAbsolutePath().toString();
    }
}
