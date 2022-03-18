package org.neointegration.mule.validation.domain;

import org.w3c.dom.NodeList;

import java.io.File;
import java.util.List;

public class Result {
	private Status status;
	private String fileName;
	
	private String nodeName;
	private String attributeValue;
	private int numberOfNode = 0;
	private String summary;

	private Result() {

	}

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(boolean status) {
		if(status) this.status = Status.PASSED;
		else this.status = Status.FAILED;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getNumberOfNode() {
		return numberOfNode;
	}

	public void setNumberOfNode(int numberOfNode) {
		this.numberOfNode = numberOfNode;
	}

	@Override
	public String toString() {
		return "Result [status=" + status + ", fileName=" + fileName + ", nodeName=" + nodeName + ", attributeValue="
				+ attributeValue + ", numberOfNode=" + numberOfNode + ", summary=" + summary + "]";
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private Status status;
		private String fileName;

		private String nodeName;
		private String attributeValue;
		private int numberOfNode = 0;
		private String summary;

		public Builder withStatus(Status status) {
			this.status = status;
			return this;
		}
		public Builder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}
		public Builder withNodeName(String nodeName) {
			this.nodeName = nodeName;
			return this;
		}
		public Builder withAttributeValue(String attributeValue) {
			this.attributeValue = attributeValue;
			return this;
		}
		public Builder withNumberOfNode(int numberOfNode) {
			this.numberOfNode = numberOfNode;
			return this;
		}
		public Builder withSummary(String summary) {
			this.summary = summary;
			return this;
		}

		public Result build() {
			Result result = new Result();
			if(this.status != null) result.setStatus(this.status);
			if(this.fileName != null) result.setFileName(this.fileName);
			if(this.nodeName != null) result.setNodeName(this.nodeName);
			if(this.attributeValue != null) result.setAttributeValue(this.attributeValue);
			if(this.numberOfNode > 0) result.setNumberOfNode(this.numberOfNode);
			if(this.summary != null) result.setSummary(this.summary);

			return result;
		}
	}

	public static Result instance(File xmlFile,
									 NodeList nodes,
									 boolean passed) {

		return Result.builder()
				.withFileName(xmlFile.getName())
				.withNumberOfNode((nodes == null)? 1 : nodes.getLength())
				.withStatus( passed? Status.PASSED : Status.FAILED)
				.build();
	}

}
