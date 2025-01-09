package io.jenkins.plugins.folderagent;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Node;
import hudson.model.Queue;
import hudson.model.queue.CauseOfBlockage;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatternMatchingNodeProperty extends NodeProperty<Node> {
    private final List<StartsWithPattern> patterns;

    @DataBoundConstructor
    public PatternMatchingNodeProperty(List<StartsWithPattern> patterns) {
        this.patterns = patterns == null ? new ArrayList<>() : patterns;
    }

    public PatternMatchingNodeProperty(StartsWithPattern... patterns) {
        this.patterns = Arrays.asList(patterns);
    }

    public List<StartsWithPattern> getPatterns() {
        return this.patterns;
    }

    @Override
    public CauseOfBlockage canTake(Queue.BuildableItem item) {
        String jobUrl = item.getTask().getUrl().replace("job/", "");
        if (getPatterns()
                .stream()
                .map(StartsWithPattern::getPattern)
                .filter(p -> !p.isBlank())
                .anyMatch(jobUrl::startsWith)) {
            return null;
        }
        return new BecauseAccessIsForbidden();
    }

    @Extension
    @Symbol({"patternMatching"})
    public static class DescriptorImpl extends NodePropertyDescriptor {
        public DescriptorImpl() {
        }

        @NonNull
        public String getDisplayName() {
            return "Allowed Projects";
        }
    }
}
