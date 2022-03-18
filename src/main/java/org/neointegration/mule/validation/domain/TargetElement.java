package org.neointegration.mule.validation.domain;

public class TargetElement {
	private String nodeReference;
	private boolean xPathNodeReference;
	private String xPathReturnType;
	private String attributeName;
	private String attributeValue;
	private NodeAttributeOperationType attributeOperation;


	public boolean isxPathNodeReference() {
		return xPathNodeReference;
	}
	public void setxPathNodeReference(boolean xPathNodeReference) {
		this.xPathNodeReference = xPathNodeReference;
	}

	public String getNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(String nodeReference) {
		this.nodeReference = nodeReference;
	}
	public String getAttributeName() {
		return attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public String getAttributeValue() {
		return attributeValue;
	}
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	public NodeAttributeOperationType getAttributeOperation() {
		return attributeOperation;
	}
	public void setAttributeOperation(NodeAttributeOperationType attributeOperation) {
		this.attributeOperation = attributeOperation;
	}

	public String getxPathReturnType() {
		return xPathReturnType;
	}

	public void setxPathReturnType(String xPathReturnType) {
		this.xPathReturnType = xPathReturnType;
	}

	@Override
	public String toString() {
		return "RuleNode{" +
				", nodeReference='" + nodeReference + '\'' +
				", xPathNodeReference=" + xPathNodeReference +
				", xPathReturnType='" + xPathReturnType + '\'' +
				", attributeName='" + attributeName + '\'' +
				", attributeValue='" + attributeValue + '\'' +
				", attributeOperation=" + attributeOperation +
				'}';
	}
}
