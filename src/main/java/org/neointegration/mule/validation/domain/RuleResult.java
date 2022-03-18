package org.neointegration.mule.validation.domain;

import java.util.List;

public class RuleResult {

	private final Rule rule;
	private List<Result> results;
	private Status status = Status.PASSED;

	private RuleResult(Rule rule) {
		this.rule = rule;
	}
	
	public Rule getRule() {
		return rule;
	}
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setStatus(boolean status) {
		if(status) {
			this.status = Status.PASSED;
		} else {
			this.status = Status.FAILED;
		}
	}
	@Override
	public String toString() {
		return "RuleResult [rule=" + rule + ", results=" + results + ", status=" + status + "]";
	}

	public static Builder builder(Rule rule) {
		return new Builder(rule);
	}

	public static class Builder {
		private Rule rule;
		private List<Result> results;
		private Status status = Status.PASSED;

		private Builder(Rule rule) {
			this.rule = rule;
		}

		public Builder withResults(List<Result> results) {
			this.results = results;
			return this;
		}
		public Builder withStatus(Status status) {
			this.status = status;
			return this;
		}
		public RuleResult build() {
			RuleResult ruleResult = new RuleResult(this.rule);
			if(this.status != null) ruleResult.setStatus(this.status);
			if(this.results != null) ruleResult.setResults(this.results);

			return ruleResult;
		}
	}
}
