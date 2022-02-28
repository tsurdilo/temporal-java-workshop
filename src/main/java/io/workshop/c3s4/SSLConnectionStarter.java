package io.workshop.c3s4;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.SimpleSslContextBuilder;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SSLConnectionStarter {

    public static final String TARGET_ENDPOINT = "my.cloud:7233";
    public static final String NAMESPACE = "mynamespace";
    public static final String DEFAULT_TASK_QUEUE = "myTaskQueue";

    public static void main(String[] args) {
        InputStream clientCert = SSLConnectionStarter.getFileInputStream("/mycert.pem");
        InputStream clientKey = SSLConnectionStarter.getFileInputStream("/mykey.key");
        // TODO Show newWorkflowService method!
        WorkflowClient client =
                WorkflowClient.newInstance(
                        newWorkflowService(clientCert, clientKey), WorkflowClientOptions.newBuilder()
                                .setNamespace(NAMESPACE).build());

        createWorker(client);

        // Create the workflow stub
        C3S4Workflow workflow =
                client.newWorkflowStub(
                        C3S4Workflow.class,
                        WorkflowOptions.newBuilder()
                                .setWorkflowId("MyDemoId")
                                .setTaskQueue(DEFAULT_TASK_QUEUE)
                                .build());

        workflow.exec();
    }

    private static void createWorker(WorkflowClient client) {
        WorkerFactory factory = WorkerFactory.newInstance(client);
        Worker worker = factory.newWorker(DEFAULT_TASK_QUEUE);

        worker.registerWorkflowImplementationTypes(C3S4WorkflowImpl.class);

        factory.start();
    }

    public static WorkflowServiceStubs newWorkflowService(InputStream clientCert, InputStream clientKey) {
        try {
            return WorkflowServiceStubs.newInstance(
                    WorkflowServiceStubsOptions.newBuilder()
                            .setSslContext(SimpleSslContextBuilder.forPKCS8(clientCert, clientKey).build())
                            .setTarget(TARGET_ENDPOINT)
                            .build());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    public static InputStream getFileInputStream(String fileName) {
        try {
            return new FileInputStream(getFile(fileName));
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return null;
        }
    }

    private static File getFile(String fileName) {
        return new File(SSLConnectionStarter.class.getResource(fileName).getFile());
    }
}
