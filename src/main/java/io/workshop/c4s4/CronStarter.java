package io.workshop.c4s4;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.workshop.c4s3.WorkflowOneImpl;
import io.workshop.c4s3.WorkflowTwoImpl;

public class CronStarter {
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
    public static final String taskQueue = "c4s4TaskQueue";
    public static final String workflowId = "TimezoneWorkflow";

    public static void main(String[] args) {
        createWorker();

        CronWorkflow workflow = client.newWorkflowStub(CronWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)

                // cron supports
                // 1. crontab:
//                .setCronSchedule("5 10 * * *")

                // Intervals
//                .setCronSchedule("@every 1m")

                // Time Zones
                // by default timezone is server timezon UTC
                // set timezone (https://en.wikipedia.org/wiki/List_of_tz_database_time_zones)
                // can set TZ flag too (not "official")
//                .setCronSchedule("TZ=America/New_York 43 1 * * *")
                // use CRON-TZ as "official"
                .setCronSchedule("CRON_TZ=America/New_York 43 1 * * *")

                // Predefined Schedules
                .setCronSchedule("@monthly") // "Run once a month, midnight, first of month"
                .build());

        workflow.runit();

    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);
        worker.registerWorkflowImplementationTypes(CronWorkflowImpl.class);
        workerFactory.start();
    }

}
