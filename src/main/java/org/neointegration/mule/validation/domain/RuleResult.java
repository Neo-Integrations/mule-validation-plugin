package org.neointegration.mule.validation.domain;

import java.util.List;

public class RuleResult {

	private Rule rule;
	private List<Result> results;
	private Status status = Status.PASSED;
	
	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
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

	

}
