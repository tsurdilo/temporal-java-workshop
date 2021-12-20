package io.workshop.c2s2;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.time.Duration;
import java.util.Calendar;


public class SleepDurationsStarter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    public static void main(String[] args) {
        WorkflowOptions options =
                WorkflowOptions.newBuilder()
                        .setWorkflowId("sleepWorkflow")
                        .setTaskQueue(SleepDurationsWorker.TASK_QUEUE)
                        .build();
        SleepWorkflow workflow = client.newWorkflowStub(SleepWorkflow.class, options);

        // For 1. and 2. - static time 30 seconds
        workflow.exec(Duration.ofSeconds(30).toMillis());

        // For 3. Start at exact time
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, 39);
//        workflow.exec(calendar.getTimeInMillis());

    }
}
