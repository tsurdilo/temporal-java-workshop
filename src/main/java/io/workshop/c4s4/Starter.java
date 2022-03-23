package io.workshop.c4s4;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import org.slf4j.MDC;

import java.util.Collections;

public class Starter {

    public static final MyMDCContextPropagator propagator = new MyMDCContextPropagator();

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    private static final WorkflowClientOptions options = WorkflowClientOptions.newBuilder()
            .setContextPropagators(Collections.singletonList(propagator))
            .build();

    public static final WorkflowClient client = WorkflowClient.newInstance(service, options);

    public static final String taskQueue = "c4s4TaskQueue";
    private static final String workflowId = "c4s4Workflow";

    public static void main(String[] args) {
        createWorker();

        MDC.put("test", "client123");

        MdcWorkflow workflow = client.newWorkflowStub(MdcWorkflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId("MdcWorkflow")
                .setTaskQueue(taskQueue)
                // set here if you want to overwrite one set in WorkflowClientOptions
//                .setContextPropagators(Collections.singletonList(propagator))
                .build());

        String result = workflow.exec();
        System.out.println("************* RES: " + result);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(MdcWorkflowImpl.class);
        worker.registerActivitiesImplementations(new MdcActivityImpl());

        workerFactory.start();
    }
}
