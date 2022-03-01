package org.neointegration.mule.validation;

import org.neointegration.mule.validation.domain.Rule;

import java.io.File;

public abstract class Validator {
    public abstract void validate(File file, Rule rule) throws Exception;

    public static void validateRule(File file, Rule rule) throws Exception {

        if(PluginUtil.isNull(file) ||
                PluginUtil.isNullOrEmpty(file.getName()))  return;

        Rule.FileType
            .match(file, rule)
            .validate(file, rule);
    }
}
