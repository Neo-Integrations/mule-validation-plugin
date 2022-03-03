package org.neointegration.mule.validation;

import org.neointegration.mule.validation.domain.Rule;
import org.neointegration.mule.validation.domain.RuleResult;

import java.io.File;

public interface CodeAnalyser {
    public RuleResult analyse(final Rule rule);
}
