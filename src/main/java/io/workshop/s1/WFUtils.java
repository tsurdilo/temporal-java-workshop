package io.workshop.s1;

import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.util.Optional;

public class WFUtils {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // tast queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c1TaskQueue";

    /**
     * Prints workflow executions, given a provided query. Note this requires ES.
     * For non-es use ListOpenWorkflowExecutionsRequest and ListClosedWorkflowExecutionRequest
     * @param query
     */
    private static void printWorkflowExecutions(String query) {
        ListWorkflowExecutionsRequest listWorkflowExecutionRequest =
                ListWorkflowExecutionsRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setQuery(query)
                        .build();
        ListWorkflowExecutionsResponse listWorkflowExecutionsResponse =
                service.blockingStub().listWorkflowExecutions(listWorkflowExecutionRequest);
        for(WorkflowExecutionInfo workflowExecutionInfo : listWorkflowExecutionsResponse.getExecutionsList()) {
            System.out.println("Workflow ID: " + workflowExecutionInfo.getExecution().getWorkflowId() + " Run ID: " +
                    workflowExecutionInfo.getExecution().getRunId() + " Status: " + workflowExecutionInfo.getStatus());
        }
    }

    /**
     * Lists Workflows for given query, then queries workflow to get customer info and prints it
     */
    private static void printWorkflowExecutionsAndShowCustomerInfo(String query) {
        ListWorkflowExecutionsRequest listWorkflowExecutionRequest =
                ListWorkflowExecutionsRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setQuery(query)
                        .build();
        ListWorkflowExecutionsResponse listWorkflowExecutionsResponse =
                service.blockingStub().listWorkflowExecutions(listWorkflowExecutionRequest);
        for(WorkflowExecutionInfo workflowExecutionInfo : listWorkflowExecutionsResponse.getExecutionsList()) {

            WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowExecutionInfo.getExecution().getWorkflowId(),
                    Optional.empty(), Optional.empty());
            Customer customer = existingUntyped.query("getCustomer", Customer.class);
            System.out.println("Workflow ID: " + workflowExecutionInfo.getExecution().getWorkflowId()
                    + " Status: " + workflowExecutionInfo.getStatus()
                    + " Customer: " + customer);
        }
    }

    /**
     * This method uses DescribeWorkflowExecutionRequest to get the status of a workflow given a
     * WorkflowClient and the workflow id.
     *
     * @return Workflow status as string
     */
    public static String getWorkflowStatus(WorkflowClient client, String workflowId) {
        WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowId, Optional.empty(), Optional.empty());
        DescribeWorkflowExecutionRequest describeWorkflowExecutionRequest =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setExecution(existingUntyped.getExecution())
                        .build();

        DescribeWorkflowExecutionResponse resp =
                client.getWorkflowServiceStubs().blockingStub().describeWorkflowExecution(describeWorkflowExecutionRequest);

        WorkflowExecutionInfo workflowExecutionInfo = resp.getWorkflowExecutionInfo();
        return workflowExecutionInfo.getStatus().toString();
    }

    /**
     * Print execution history for a workflow given its workflow id and run id
     */
    public static void printWorkflowExecutionHistory(WorkflowClient client, String workflowId, String runId) {
        WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty());

        GetWorkflowExecutionHistoryRequest getWorkflowExecutionHistoryRequest =
                GetWorkflowExecutionHistoryRequest.newBuilder()
                        .setNamespace("default")
                        .setExecution(existingUntyped.getExecution())
                        .build();
        GetWorkflowExecutionHistoryResponse getWorkflowExecutionHistoryResponse =
                service.blockingStub().getWorkflowExecutionHistory(getWorkflowExecutionHistoryRequest);
        for(HistoryEvent historyEvent : getWorkflowExecutionHistoryResponse.getHistory().getEventsList()) {
            System.out.println("Type: " + historyEvent.getEventType() + " id: " + historyEvent.getEventId());
        }
    }

    /**
     * Reset a workflow to a specific taskFinishEventId given its workfow id and run id
     */
    public static void resetWorkflow(WorkflowClient client, String workflowId, String runId, long taskFinishEventId) {
        WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty());
        ResetWorkflowExecutionRequest resetWorkflowExecutionRequest =
                ResetWorkflowExecutionRequest.newBuilder()
                        .setNamespace("default")
                        .setWorkflowExecution(existingUntyped.getExecution())
                        .setWorkflowTaskFinishEventId(taskFinishEventId)
                        .build();

        ResetWorkflowExecutionResponse resetWorkflowExecutionResponse =
                client.getWorkflowServiceStubs().blockingStub().resetWorkflowExecution(resetWorkflowExecutionRequest);

        System.out.println("Reset execution id: " + resetWorkflowExecutionResponse.getRunId());
    }

    public static void main(String[] args) {
        //printWorkflowExecutions("ExecutionStatus='Completed'");

        // for search attributes
        // Customer workflows where customer is over 20
        printWorkflowExecutionsAndShowCustomerInfo("CustomerAge > 20");
        // Customer workflows where customer title is Ms and age is over 30
        //printWorkflowExecutionsAndShowCustomerInfo("CustomerAge > 20 AND CustomerTitle='Ms'");
        // Customer workflows where customer speaks Spanish
        //printWorkflowExecutionsAndShowCustomerInfo("CustomerLanguages LIKE \"%Spanish\"");

        //printWorkflowExecutionHistory(client, "c1GreetingWorkflow", "ca6d5cee-cefa-41d6-bade-fdca490d90f4");
        //resetWorkflow(client, "c1GreetingWorkflow", "ca6d5cee-cefa-41d6-bade-fdca490d90f4", 9);
    }
}
