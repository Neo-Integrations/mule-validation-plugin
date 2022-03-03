package org.neointegration.mule.validation;

import org.neointegration.mule.validation.domain.Rule;

import java.io.File;

public abstract class Validator {
    public abstract void validate(final File file, final Rule rule) throws Exception;
    public static Validator getInstance(final File file, final Rule rule) throws Exception {
        if(PluginUtil.isNull(file) ||
                PluginUtil.isNullOrEmpty(file.getName()))  return null;
        if(PluginUtil.isNotNull(rule.getFileType()))
            return rule.getFileType().getValidator();
        String str = file.getName().toLowerCase();
        if(PluginUtil.isNullOrEmpty(str)) return null;
        for(Rule.FileType fileType: Rule.FileType.values()){
            if(str.endsWith(fileType.getExtension())) {
                rule.setFileType(fileType);
                return fileType.getValidator();
            }
        }
        return null;
    }
}
