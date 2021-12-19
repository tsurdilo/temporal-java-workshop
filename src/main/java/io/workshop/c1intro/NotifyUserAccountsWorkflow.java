package io.workshop.c1intro;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class NotifyUserAccountsWorkflow implements NotifyUserAccounts {

    private Logger logger = Workflow.getLogger(this.getClass().getName());
    private int customerCount = 0;

    @Override
    public void notify(String[] accountIds) {

        for(String account : accountIds) {
            customerCount++;
            logger.info("Java: notifying for: " + account);
            Workflow.sleep(Duration.ofSeconds(2));
        }
    }

    @Override
    public int getCount() {
        return customerCount;
    }
}
