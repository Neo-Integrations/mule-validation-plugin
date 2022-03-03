package org.neointegration.mule.validation;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;

import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.RuleResult;
import org.neointegration.mule.validation.domain.Status;

public class MuleCodeAnalyser implements CodeAnalyser {

	@Override
	public RuleResult analyse(final Rule rule) {
		final RuleResult ruleResult = new RuleResult(rule);

		for(File file: this.listFiles(rule)) {
			try {
				Validator.getInstance(file, rule).validate(file, rule);
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
			ruleResult.setStatus(
					rule.getAggregation().getNodeCardinality().eval(
							howManySuccess,
							rule.getAggregation().getLimit(),
							rule.getMapList().size(), maxNodeInAFile));
		} else {
			if(howManyFailed > 0) {
				ruleResult.setStatus(Status.FAILED);
			}
		}
		return ruleResult;
	}

	private List<File> listFiles(Rule rule) {
		final List<File> listFiles = new ArrayList<File>();
		final File dir = new File(new StringBuilder()
				.append(rule.getProjectDir())
				.append(File.separator)
				.append(rule.getLocation().getPath()).toString());

		if(PluginUtil.isNullOrEmpty(rule.getLocation().getInlcudeFileNamePattern()))
			throw new IllegalArgumentException("includeFileNamePattern must be set");
		final Collection<File> files = FileUtils.listFiles(dir,
				new RegexFileFilter(rule.getLocation().getInlcudeFileNamePattern()),
				DirectoryFileFilter.DIRECTORY);

		final String excludeDir = new StringBuilder()
				.append(dir.getAbsolutePath())
				.append(File.separator)
				.append("target").toString();

		for (File file : files) {
			if (false == file.getAbsolutePath().startsWith(excludeDir)) {
				if (PluginUtil.isNullOrEmpty(rule.getLocation().getExcludeFileNamePattern()) ||
						false == file.getName().matches(rule.getLocation().getExcludeFileNamePattern())) {
					listFiles.add(file);
				}
			}
		}
		return listFiles;
	}

}
