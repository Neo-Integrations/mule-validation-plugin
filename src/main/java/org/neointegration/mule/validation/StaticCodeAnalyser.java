package org.neointegration.mule.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.RuleResult;
import org.neointegration.mule.validation.domain.Status;

public class StaticCodeAnalyser {

	public void validateEachRule(Rule rule, File dir, RuleResult ruleResult, String projectDir) {

		List<File> files = this.files(dir, rule.getLocation().getInlcudeFileNamePattern(),
				rule.getLocation().getExcludeFileNamePattern());

		rule.setProjectDir(projectDir);

		for (File file : files) {
			try {
				Validator.validateRule(file, rule);

				// If return false, do not repeat the rule for any other file
				// Useful for RAML rules as it only requires to process one time for every rule.
				if(rule.isFinished()) break;

			} catch (Exception ignored) {}
		}
		ruleResult.setResults(rule.getMapList());
		
		int howManySuccess = 0;
		int howManyFailed = 0;
		int maxNodeInAFile = 0;
		for(Result result: rule.getMapList()) {
			if(result.getStatus() == Status.PASSED) {
				if(result.getNumberOfNode() > 0) {
					howManySuccess = howManySuccess + result.getNumberOfNode();
					if(result.getNumberOfNode() > maxNodeInAFile) {
						maxNodeInAFile = result.getNumberOfNode();
					}
				} else {
					howManySuccess = howManySuccess + 1;
				}
			} if(result.getStatus() == Status.FAILED) {
				howManyFailed = howManyFailed + 1;
			}	
		}
		
		if(rule.getAggregation() != null &&
				PluginUtil.isNotNull(rule.getAggregation().getNodeCardinality())) {
			ruleResult.setStatus(rule.getAggregation().getNodeCardinality().eval(howManySuccess,
					rule.getAggregation().getLimit(),
					rule.getMapList().size(), maxNodeInAFile));
		} else {
			if(howManyFailed > 0) {
				ruleResult.setStatus(Status.FAILED);
			}
		}

	}

	private List<File> files(File dir, String includeFileNamePattern, String excludeFileNamePattern) {
		List<File> listFiles = new ArrayList<File>();

		if(PluginUtil.isNullOrEmpty(includeFileNamePattern)) throw new IllegalArgumentException("includeFileNamePattern must be set");

		Collection<File> files = FileUtils.listFiles(dir, new RegexFileFilter(includeFileNamePattern),
				DirectoryFileFilter.DIRECTORY);

		String excludDir = dir.getAbsolutePath() + File.separator + "target";
		for (File f : files) {
			if (f.getAbsolutePath().startsWith(excludDir) == false) {
				if (PluginUtil.isNullOrEmpty(excludeFileNamePattern)) {
					listFiles.add(f);
				} else if(f.getName().matches(excludeFileNamePattern) == false) {
					listFiles.add(f);
				}
			}
		}

		return listFiles;
	}

}
