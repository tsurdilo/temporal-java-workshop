package io.workshop.intro;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class NotifyUserAccountsWorkflow implements NotifyUserAccounts {

    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public void notify(String[] accountIds) {

        for(String account : accountIds) {
            logger.info("Java: notifying for: " + account);
            Workflow.sleep(Duration.ofSeconds(2));
        }
    }
}
