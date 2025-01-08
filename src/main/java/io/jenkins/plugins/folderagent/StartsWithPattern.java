package io.jenkins.plugins.folderagent;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;

public class StartsWithPattern extends AbstractDescribableImpl<StartsWithPattern> {

    private final String pattern;

    @DataBoundConstructor
    public StartsWithPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<StartsWithPattern> {
        public String getDisplayName() { return "StartsWithPattern"; }
    }

}
