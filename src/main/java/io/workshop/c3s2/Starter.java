package io.workshop.c3s2;

import io.temporal.client.BatchRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c3S2TaskQueue";

    private static final String workflowId = "c3s2Workflow";


    public static void main(String[] args) {
        createWorker();

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setWorkflowId(workflowId)
                .setTaskQueue(taskQueue)
                .build();

        // 1. Typed workflow stub
        C3S2Workflow workflow = client.newWorkflowStub(C3S2Workflow.class, workflowOptions);

        // execute sync
        String result = workflow.execute("Hello");

        // async
        WorkflowClient.start(workflow::execute, "Hello");
        // signal
        workflow.addInput("Hello2");
        // query
        String input = workflow.getInput();
        // get for results (potentially wait)
        // untyped!!
        WorkflowStub untyped1 = WorkflowStub.fromTyped(workflow);
        String r = untyped1.getResult(String.class);

        // signalWithStart typed
        BatchRequest request = client.newSignalWithStartRequest();
        // workflow method
        request.add(workflow::execute, "Hello");
        // signal method
        request.add(workflow::addInput, "Hello2");
        client.signalWithStart(request);

        // cancel, terminate - have to use untyped stub

        // ****************************************************

        // 2. Untyped stub
        WorkflowStub untyped2 = client.newUntypedWorkflowStub("C3S2Workflow", workflowOptions);
        // Start
        untyped2.start("Hello");
        // get results
        String r2 = untyped2.getResult(String.class);
        // signal
        untyped2.signal("addInput", "Hello2");
        // query
        String q2 = untyped2.query("getInput", String.class);
        // cancel
        untyped2.cancel();
        // terminate
        untyped2.terminate("my reason");
        // signalWithSTart
        // signal name, signal args, workflow args
        untyped2.signalWithStart("addInput", new Object[] {"Hello2"}, new Object[] {"Hello"});
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(C3S2WorkflowImpl.class,
                C3S2ChildWorkflowImpl.class);
        worker.registerActivitiesImplementations(new C3S2ActivitiesImpl());

        workerFactory.start();
    }
}
