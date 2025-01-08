package io.jenkins.plugins.folderagent;

import hudson.model.queue.CauseOfBlockage;

public class BecauseAccessIsForbidden extends CauseOfBlockage {

    public String getShortDescription() {
        return "Access to agent is forbidden for this project";
    }

}
