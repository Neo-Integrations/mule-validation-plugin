package org.neointegration.mule.validation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.jayway.jsonpath.JsonPath;
import org.neointegration.mule.validation.domain.Result;
import org.neointegration.mule.validation.domain.Rule;

public class JSONValidator extends Validator {

	@Override
	public void validate(File jsonFile, Rule rule) throws IOException {
		if(PluginUtil.isNull(rule.getNodeSpecification()) ||
				PluginUtil.isNullOrEmpty(rule.getNodeSpecification().getNodeReference())) {
			Result result = PluginUtil.createResultObject(jsonFile, rule, null, true);
			result.setNodeName(jsonFile.getName());
			rule.addToMapList(result);
			return;
		}

		String data = new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath())));
		Object node = JsonPath.read(data, rule.getNodeSpecification().getNodeReference());
		Result result = PluginUtil.createResultObject(jsonFile, rule, null, true);
		result.setNodeName(jsonFile.getName());
		rule.addToMapList(result);

		if(PluginUtil.isNotNull(rule.getNodeSpecification().getAttributeOperation())) {
			result.setStatus(rule.getNodeSpecification().getAttributeOperation().eval(rule, node));
		}

	}
}
