package org.neointegration.mule.validation;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.neointegration.mule.validation.domain.NodeAttributeOperationType;
import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public enum XMLValidator implements Validator {
	INSTANCE;

	@Override
	public void validate(final File xmlFile, final Rule rule) throws Exception{

		if(false == this.doesNodeSpecificationExists(xmlFile, rule)) {
			// If node specification does not exists then nothing else to do
			return;
		}

		final NodeList nodes = findMatchingNodes(xmlFile, rule);
		if(PluginUtil.isNullOrEmpty(nodes)) {
			// Return empty, if no node found
			return;
		}

		// If there were node selected based on criteria but the attribute name is null
		// then consider it as success and return. In this case attributes matching is not required
		if(PluginUtil.isNullOrEmpty(rule.getNodeSpecification().getAttributeName()) &&
				nodes.getLength() > 0 ) {
			final Result result = Result.builder()
							.withFileName(xmlFile.getName())
							.withNumberOfNode(nodes.getLength())
							.withStatus(Status.PASSED)
							.withNodeName(rule.getNodeSpecification().getNodeReference())
							.withSummary("File " + xmlFile.getName() +
										 " has " + nodes.getLength() +
									     " " + rule.getNodeSpecification().getNodeReference() +
									     " nodes")
							.build();

			rule.addToMapList(result);
			return;
		}

		// Match the attributes
		for(int i = 0; i < nodes.getLength(); i++) {
			Node eachNode = nodes.item(i);
			if(PluginUtil.isNull(eachNode)) continue;

			Result result = Result.instance(xmlFile, null, true);
			rule.addToMapList(result);

			NamedNodeMap attributes = eachNode.getAttributes();
			if(PluginUtil.isNull(attributes)) {
				result.setStatus(Status.FAILED);
				String failedSummary = "The rule failed in the file [" + xmlFile.getName() +
						"] as no attributes found in the selected node ["+ eachNode.getNodeName() + "]";
				continue;
			}

			final Node node = attributes.getNamedItem(rule.getNodeSpecification().getAttributeName());

			if(PluginUtil.isNull(node) || PluginUtil.isNullOrEmpty(node.getNodeValue())) {
				if(rule.getNodeSpecification().getAttributeOperation() == NodeAttributeOperationType.NOT_MATCHES) {
					continue; // In case of NOT_MATCHES, not finding a node with the attribute name will be considered as success.
				} else {
					String failedSummary = "The rule failed in the file [" + xmlFile.getName() +
							"] as it could not find any attribute with the name [" + rule.getNodeSpecification().getAttributeName() + "]";
					result.setStatus(Status.FAILED);
					result.setSummary(failedSummary);
				}
				continue;
			}

			if(PluginUtil.isNotNull(rule.getNodeSpecification().getAttributeOperation())) {
				result.setStatus(rule.getNodeSpecification().getAttributeOperation().eval(rule, node.getNodeValue()));
			}

			if(result.getStatus() == Status.FAILED) {
				String failedSummary = "The rule failed in the file [" + xmlFile.getName() +
						"] on the XML tag ["+node.getNodeName() + "] and with attribute value ["+node.getNodeValue() + "]";
				result.setSummary(failedSummary);
			}
		}

		return;

	}

	private NodeList findMatchingNodes(File xmlFile,
									   Rule rule) throws Exception {
		// Create DOM parser
		final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document doc = builder.parse(xmlFile);

		// Find the nodes from XML document matching 'nodeReference'
		NodeList nodes = null;
		if(rule.getNodeSpecification().isxPathNodeReference() == true) {
			nodes = processXPathExpression(xmlFile, rule, doc);
		} else {
			Element element = doc.getDocumentElement();
			nodes = element.getElementsByTagName(rule.getNodeSpecification().getNodeReference());
		}

		return nodes;
	}

	private NodeList processXPathExpression(File xmlFile,
										Rule rule,
										Document doc
										) throws XPathExpressionException {
		NodeList nodes = null;
		final XPath xPath = XPathFactory.newInstance().newXPath();
		final XPathExpression expression = xPath.compile(rule.getNodeSpecification().getNodeReference());
		if(PluginUtil.isNotNullAndEmpty(rule.getNodeSpecification().getxPathReturnType()) &&
				rule.getNodeSpecification().getxPathReturnType().equals("BOOLEAN")) {
			boolean matched = (boolean) expression.evaluate(doc, XPathConstants.BOOLEAN);
			Result result = Result.instance(xmlFile, null, matched);
			if(matched) result.setSummary("XPath matched");
			else result.setSummary("XPath did not matched");
			rule.addToMapList(result);
			return null;
		}
		nodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
		return nodes;
	}

	private boolean doesNodeSpecificationExists(File xmlFile, Rule rule) {
		if(PluginUtil.isNotNull(rule.getNodeSpecification())) {
			return true;
		}

		// If the 'NodeSpecification' does not exists, then consider it as success and return
		final Result result = Result.instance(xmlFile, null, true);
		result.setNodeName(xmlFile.getName());
		rule.addToMapList(result);

		return false;
	}
}
