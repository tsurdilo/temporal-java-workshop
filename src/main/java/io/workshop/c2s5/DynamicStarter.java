package io.workshop.c2s5;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class DynamicStarter {
    public static final String TASK_QUEUE = "dynamicWfTaskQueue";
    private static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    private static final WorkflowClient client = WorkflowClient.newInstance(service);
    private static final WorkerFactory factory = WorkerFactory.newInstance(client);

    public static void main(String[] args) {
        Worker worker = factory.newWorker(TASK_QUEUE);
        worker.registerWorkflowImplementationTypes(MyDynamicWorkflow.class);
        worker.registerActivitiesImplementations(new MyDynamicActivity());

        factory.start();

        // Start our dynamic workflow
        WorkflowOptions workflowOptions =
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("MyDynamicWF").build();

        // use untyped workflow stub..."NotExistingWFType" workflow type is not registered with worker!
        // if not specifically registered, it will be routed to our MyDynamicWorkflow
        WorkflowStub workflow = client.newUntypedWorkflowStub("NotExistingWFType", workflowOptions);

        // start wf execution via signalWithStart, not the signal name is not existent
        // pass in "John" as signal param and "Hello" as wf data input
        workflow.signalWithStart("notExistingSignalName", new Object[] {"John"}, new Object[] {"Hello"});

        // wait for the workflow to complete
        String result = workflow.getResult(String.class);

        System.out.println("** WF result: " + result);

        // we can query dynamic workflows after they completed as well:
        String wfNameFroQuery = workflow.query("notExistingQueryName", String.class);
        System.out.println("** WF name from query: " + wfNameFroQuery);

    }
}
