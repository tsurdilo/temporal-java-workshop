package io.workshop.c1intro;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class NotifyUserAccountsWorkflow implements NotifyUserAccounts {

    private Logger logger = Workflow.getLogger(this.getClass().getName());
    private int customerCount = 0;

    private AccountActivities accountActivities =
            Workflow.newActivityStub(AccountActivities.class, ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(3))
                    .build());

    @Override
    public void notify(String[] accountIds) {

        for(String account : accountIds) {
            customerCount++;
            logger.info("Notifying Account Id: " + account);
            accountActivities.notify(account);
            Workflow.sleep(Duration.ofSeconds(2));
        }
    }

    @Override
    public int getCount() {
        return customerCount;
    }
}
