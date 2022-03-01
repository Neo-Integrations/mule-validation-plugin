package org.neointegration.mule.validation.domain;

public class Result {
	private Status status;
	private String fileName;
	
	private String nodeName;
	private String attributeValue;
	private int numberOfNode = 0;
	
	private String summary;
	
	


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


	
}
