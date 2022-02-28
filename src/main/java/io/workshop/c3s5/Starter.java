package io.workshop.c3s5;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.util.Optional;

public class Starter {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c3s5TaskQueue";

    private static final String workflowId = "c3s5Workflow";

    public static void main(String[] args) {
        createWorker();

        C3S5Workflow workflow = client.newWorkflowStub(C3S5Workflow.class, WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .build());

        // start wf exec async via untyped stub
        WorkflowExecution exec = WorkflowClient.start(workflow::exec, 0);
        // send 50 signals to workflow
        for(int i = 0; i < 50; i++) {
            // sleep for 100 ms just for demo // TODO show when removed
            sleep(500);
            workflow.addSignal();
        }
        // send exit signal
        workflow.exit();

        // Attach to the latest run and query
        sleep(3000);
        WorkflowStub untyped = client.newUntypedWorkflowStub(exec.getWorkflowId(),
                Optional.of(exec.getRunId()), Optional.of("C3S5Workflow"));
        int total = untyped.getResult(Integer.class);

        System.out.println("*** Total Processed: " +  total);


        System.exit(0);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(C3S5WorkflowImpl.class);

        workerFactory.start();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
