package org.neointegration.mule.validation.domain;

import org.neointegration.mule.validation.PluginUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public enum NodeAttributeOperationType {
	EQUALS(0), MATCHES(1), NOT_MATCHES(2), CONTAINS(3), COUNT(4);


	public boolean eval(Rule rule, Object node) {
		if(rule.getFileType() == Rule.FileType.XML) {
			return xmlFileExpressions[index].eval(rule, node);
		} else {
			return jsonFileExpressions[index].eval(rule, node);
		}
	}

	private static Expression[] xmlFileExpressions = {
			(rule, node) -> node.equals(rule.getNodeSpecification().getAttributeValue()),
			(rule, node) -> ((String) node).matches(rule.getNodeSpecification().getAttributeValue()),
			(rule, node) -> !((String) node).matches(rule.getNodeSpecification().getAttributeValue()),
			(rule, node) -> evalContainsForXMlFile(node, rule.getNodeSpecification().getAttributeValue()),
			(rule, node) -> ((String) node).length() == Integer.parseInt(rule.getNodeSpecification().getAttributeValue())
	};

	private static Expression[] jsonFileExpressions = {
			(rule, node) -> node.equals(rule.getNodeSpecification().getAttributeValue()),
			(rule, node) -> (node instanceof String && ((String) node).matches(rule.getNodeSpecification().getAttributeValue())),
			(rule, node) -> !(node instanceof String && ((String) node).matches(rule.getNodeSpecification().getAttributeValue())),
			(rule, node) -> evalContainsForJSONFile(node, rule.getNodeSpecification().getAttributeValue()),
			(rule, node) -> evalCountForJSONFile(node, rule.getNodeSpecification().getAttributeValue())
	};


	private int index;
	private NodeAttributeOperationType(int index) {
		this.index = index;
	}

	private interface Expression {
		public boolean eval(Rule rule, Object node);
	}

	private static boolean evalContainsForXMlFile(Object node, String value) {
		if(PluginUtil.isNotNullAndEmpty(value) && value.contains("|")) {
			for(String str: value.split("\\|")) {
				if(PluginUtil.isNotNull(str)) str = str.trim();
				if(PluginUtil.isNotNullAndEmpty(str) && str.equals(node)) {
					return true;
				}
			}
		}
		return ((String) node).contains(value);
	}

	private static boolean evalContainsForJSONFile(Object node, String value) {
		if(node instanceof String) {
			return ((String) node).contains(value);
		} else if(node.getClass().isArray()) {
			return Arrays.asList(((Object[])node)).contains(value);
		} else if(node instanceof List) {
			return ((List) node).contains(value);
		} else if(node instanceof Map) {
			return ((Map) node).containsKey(value);
		} else if(node instanceof Set) {
			return ((Set) node).contains(value);
		}
		return false;
	}

	private static boolean evalCountForJSONFile(Object node, String value) {
		int number = Integer.parseInt(value);
		if(node instanceof String) {
			return ((String) node).length() == number;
		} else if(node.getClass().isArray()) {
			return Arrays.asList(((Object[])node)).size() == number;
		} else if(node instanceof List) {
			return ((List) node).size() == number;
		} else if(node instanceof Map) {
			return ((Map) node).size() == number;
		} else if(node instanceof Set) {
			return ((Set) node).size() == number;
		}
		return false;
	}
}
