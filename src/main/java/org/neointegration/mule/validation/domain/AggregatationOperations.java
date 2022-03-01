package org.neointegration.mule.validation.domain;

public class AggregatationOperations {
	private NodeCardinality nodeCardinality;
	private int limit = 0;

	public NodeCardinality getNodeCardinality() {
		return nodeCardinality;
	}

	public void setNodeCardinality(NodeCardinality nodeCardinality) {
		this.nodeCardinality = nodeCardinality;
	}
	
	

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "AggregatationOperations [nodeCardinality=" + nodeCardinality + ", limit=" + limit + "]";
	}


	
	

}
