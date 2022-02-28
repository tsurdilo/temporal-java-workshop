package io.workshop.c3s4;

import io.grpc.StatusRuntimeException;
import io.temporal.client.*;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.util.Optional;

public class ClientErrorHandling {

    public static void main(String[] args) {
        try {
            // Set invalid service stub options (target)
            WorkflowServiceStubsOptions options =
                    WorkflowServiceStubsOptions.newBuilder()
                            .setTarget("127.0.0.2:9000")
                            .build();
            WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

            WorkflowClient client = WorkflowClient.newInstance(service);

            // 1. Show WorkflowServiceException
            WorkflowStub untyped = client.newUntypedWorkflowStub("MyWorkflowId",
                    WorkflowOptions.newBuilder()
                            // TODO SHOW THAT WE FORGOT TO SET TASK QUEUE
//                            .setTaskQueue("abc")
                            .setWorkflowId("myWorkflowID")
                            .build());

            // 2. Show invalid grpc connection
            // TODO PUT OPTIONS IN WorkflowServiceStubs


            untyped.start("Hello");

            // 3. Show WorkflowNotFoundException on signal and IllegalStateException on query
            // create untyped stub for existing execution
            // TODO remove the previous start ...
            // this workflow stub is for existing workflow exec
            WorkflowStub untyped2 = client.newUntypedWorkflowStub("nonExisting", Optional.empty(), Optional.empty());
            untyped2.signal("abc");
            untyped.query("xyz", String.class);

        } catch(WorkflowServiceException e) {
            e.printStackTrace();
            StatusRuntimeException se = (StatusRuntimeException) e.getCause();
            System.out.println("****** 1. Status: " + se.getStatus());
        } catch (StatusRuntimeException ee) {
            System.out.println("****** 2. Status: " + ee.getStatus());
        } catch(WorkflowNotFoundException eee) {
            System.out.println("**** 3. Workflow Not Found: " + eee.getMessage());
        } catch (IllegalStateException eeee) {
            System.out.println("**** 4. Query Error: " + eeee.getMessage());
        }
    }
}
