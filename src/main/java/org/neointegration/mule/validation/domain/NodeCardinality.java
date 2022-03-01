package org.neointegration.mule.validation.domain;

public enum NodeCardinality {
	ONLY_ONE((howManySuccess, limit, total, maxNodeInAFile) -> howManySuccess == 1),
	AT_LEAST_ONE((howManySuccess, limit, total, maxNodeInAFile) -> howManySuccess > 0),
	MORE_THAN_ONE((howManySuccess, limit, total, maxNodeInAFile) -> howManySuccess > 1),
	NONE((howManySuccess, limit, total, maxNodeInAFile) -> howManySuccess == 0),
	LESS_THAN((howManySuccess, limit, total, maxNodeInAFile) -> maxNodeInAFile < limit),
	MORE_THAN((howManySuccess, limit, total, maxNodeInAFile) -> maxNodeInAFile > limit),
	ALL((howManySuccess, limit, total, maxNodeInAFile) -> total == howManySuccess);


	private CardinalityExpression expression;
	private NodeCardinality(CardinalityExpression expression) {
		this.expression = expression;
	}

	public boolean eval(int howManySuccess, int limit, int total, int maxNodeInAFile) {
		return expression.eval(howManySuccess, limit, total, maxNodeInAFile);
	}

	public interface CardinalityExpression {
		public boolean eval(int howManySuccess, int limit, int total, int maxNodeInAFile);
	}
}
