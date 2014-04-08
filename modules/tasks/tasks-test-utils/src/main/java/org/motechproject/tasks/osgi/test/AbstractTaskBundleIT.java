package org.motechproject.tasks.osgi.test;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import static org.junit.Assert.assertTrue;

public abstract class AbstractTaskBundleIT extends BasePaxIT {

    @Inject
    private ChannelService channelService;

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return Arrays.asList("org.motechproject:motech-tasks-test-utils",
                "org.motechproject:motech-tasks",
                "org.apache.commons:com.springsource.org.apache.commons.fileupload",
                "org.apache.commons:com.springsource.org.apache.commons.beanutils");
    }

    protected Channel findChannel(String channelName) throws IOException {
        getLogger().info(String.format("Looking for %s", channelName));

        getLogger().info(String.format("There are %d channels in total", channelService.getAllChannels().size()));

        return channelService.getChannel(channelName);
    }

    protected TaskEvent findTaskEventBySubject(List<? extends TaskEvent> taskEvents, String subject) {
        TaskEvent taskEvent = null;
        for (TaskEvent event : taskEvents) {
            if (subject.equals(event.getSubject())) {
                taskEvent = event;
                break;
            }
        }
        return taskEvent;
    }

    protected TriggerEvent findTriggerEventBySubject(List<TriggerEvent> triggerEvents, String subject) {
        TaskEvent taskEvent = findTaskEventBySubject(triggerEvents, subject);
        assertTrue(taskEvent instanceof TriggerEvent);
        return (TriggerEvent) taskEvent;
    }

    protected ActionEvent findActionEventBySubject(List<ActionEvent> actionEvents, String subject) {
        TaskEvent taskEvent = findTaskEventBySubject(actionEvents, subject);
        assertTrue(taskEvent instanceof ActionEvent);
        return (ActionEvent) taskEvent;
    }

    protected boolean hasEventParameterKey(String externalIdKey, List<EventParameter> eventParameters) {
        boolean found = false;
        for (EventParameter param : eventParameters) {
            if (externalIdKey.equals(param.getEventKey())) {
                found = true;
                break;
            }
        }
        return found;
    }

    protected boolean hasActionParameterKey(String externalIdKey, SortedSet<ActionParameter> actionParameters) {
        boolean found = false;
        for (ActionParameter param : actionParameters) {
            if (externalIdKey.equals(param.getKey())) {
                found = true;
                break;
            }
        }
        return found;
    }

    protected void waitForChannel(final String channelName) throws InterruptedException {
        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                try {
                    return findChannel(channelName) == null;
                } catch (IOException e) {
                    return false;
                }
            }
        }, 20000).start();
    }
}
