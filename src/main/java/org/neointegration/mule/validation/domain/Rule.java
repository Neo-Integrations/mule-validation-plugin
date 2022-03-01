package org.neointegration.mule.validation.domain;


import org.neointegration.mule.validation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Rule {
	private String ruleId;
	private String description;
	private Severity severity;
	private TargetElement nodeSpecification;
	private AggregatationOperations  aggregation;
	private RAMLRule ramlRule;
	private boolean active = true;
	private Locations location;

	// Below attributes are for transactional convenience
	private FileType fileType;
	private boolean finished;
	private final List<Result> mapList = new ArrayList<>();
	private String projectDir;



	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}


	public TargetElement getNodeSpecification() {
		return nodeSpecification;
	}

	public void setNodeSpecification(TargetElement nodeSpecification) {
		this.nodeSpecification = nodeSpecification;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public RAMLRule getRamlRule() {
		return ramlRule;
	}

	public void setRamlRule(RAMLRule ramlRule) {
		this.ramlRule = ramlRule;
	}


	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public AggregatationOperations getAggregation() {
		return aggregation;
	}

	public void setAggregation(AggregatationOperations aggregation) {
		this.aggregation = aggregation;
	}

	public Locations getLocation() {
		return location;
	}

	public void setLocation(Locations location) {
		this.location = location;
	}


	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public List<Result> getMapList() {
		return mapList;
	}

	public void addToMapList(Result result) {
		this.mapList.add(result);
	}

	public String getProjectDir() {
		return projectDir;
	}

	public void setProjectDir(String projectDir) {
		this.projectDir = projectDir;
	}

	@Override
	public String toString() {
		return "Rule{" +
				"ruleId='" + ruleId + '\'' +
				", description='" + description + '\'' +
				", severity=" + severity +
				", nodeSpecification=" + nodeSpecification +
				", aggregation=" + aggregation +
				", ramlRule=" + ramlRule +
				", active=" + active +
				", location=" + location +
				", fileType=" + fileType +
				", finished=" + finished +
				'}';
	}

	public static enum FileType {
		XML(".xml", new XMLValidator()),
		JSON(".json", new JSONValidator()),
		RAML(".raml", new RAMLValidator());

		private final String extension;
		private final Validator validator;
		private FileType(String extension,  Validator validator) {
			this.extension = extension;
			this.validator = validator;
		}

		public static Validator match(File file, Rule rule) {

			if(PluginUtil.isNotNull(rule.getFileType()))
				return rule.getFileType().validator;

			String str = file.getName().toLowerCase();
			if(PluginUtil.isNullOrEmpty(str)) return null;
			for(FileType fileType: FileType.values()){
				if(str.endsWith(fileType.extension)) {
					rule.setFileType(fileType);
					return fileType.validator;
				}
			}
			return null;
		}
	}
}
