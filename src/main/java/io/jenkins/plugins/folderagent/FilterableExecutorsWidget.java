package io.jenkins.plugins.folderagent;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jenkins.widgets.ExecutorsWidget;
import jenkins.widgets.WidgetFactory;
import org.jenkinsci.Symbol;

public class FilterableExecutorsWidget {
    private final static Logger logger = Logger.getLogger(FilterableExecutorsWidget.class.getName());

    @SuppressWarnings("unused")
    @Initializer(after = InitMilestone.PLUGINS_PREPARED)
    public void postInitialize() {
        ExtensionList<WidgetFactory> wf = ExtensionList.lookup(WidgetFactory.class);
        for (WidgetFactory i : new ArrayList<>(wf)) {
            if (i instanceof ExecutorsWidget.ViewFactoryImpl) {
                wf.remove(i);
                logger.info("FilterableExecutorsWidget has removed default widget");
            }
        }
    }

    @Extension(ordinal = 100)
    @Symbol("executors") // historically this was above normal widgets and below BuildQueueWidget
    public static final class FilterableViewFactoryImpl extends WidgetFactory<View, ExecutorsWidget> {
        @Override
        public Class<View> type() {
            return View.class;
        }

        @Override
        public Class<ExecutorsWidget> widgetType() {
            return ExecutorsWidget.class;
        }

        @NonNull
        @Override
        public Collection<ExecutorsWidget> createFor(@NonNull View target) {
            List<Computer> computers = target.getComputers()
                    .stream()
                    .filter(computer -> filterComputer(target.getUrl(), computer))
                    .collect(Collectors.toUnmodifiableList());
            if (computers.isEmpty()) {
                return List.of();
            }
            return List.of(new ExecutorsWidget(target.getUrl(), computers));
        }

        private boolean filterComputer(String url, Computer computer) {
            String viewUrl = url.replace("job/", "");
            Node node = computer.getNode();
            if (node == null) {
                logger.warning("node is null");
                return false;
            }
            PatternMatchingNodeProperty nodeProperty = node.getNodeProperty(PatternMatchingNodeProperty.class);
            if (nodeProperty == null) {
                logger.warning("nodeProperty is null");
                return true;
            }

            return nodeProperty.getPatterns()
                    .stream()
                    .map(StartsWithPattern::getPattern)
                    .filter(p -> !p.isBlank())
                    .anyMatch(viewUrl::startsWith);
        }
    }
}