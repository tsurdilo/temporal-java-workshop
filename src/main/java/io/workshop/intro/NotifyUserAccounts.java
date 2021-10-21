package io.workshop.intro;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface NotifyUserAccounts {
    @WorkflowMethod
    void notify(String[] accountIds);
}
