package io.workshop.c1s1;

import com.google.protobuf.ByteString;
import com.google.protobuf.Timestamp;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.netty.shaded.io.netty.channel.ChannelInitializer;
import io.grpc.netty.shaded.io.netty.channel.local.LocalChannel;
import io.temporal.api.common.v1.Payload;
import io.temporal.api.common.v1.Payloads;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.ArchivalState;
import io.temporal.api.enums.v1.EventType;
import io.temporal.api.enums.v1.ResetReapplyType;
import io.temporal.api.filter.v1.StartTimeFilter;
import io.temporal.api.filter.v1.WorkflowTypeFilter;
import io.temporal.api.history.v1.History;
import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.api.namespace.v1.NamespaceConfig;
import io.temporal.api.workflow.v1.PendingActivityInfo;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.*;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowStub;
import io.temporal.common.RetryOptions;
import io.temporal.common.converter.DataConverter;
import io.temporal.common.converter.DefaultDataConverter;
import io.temporal.internal.common.WorkflowExecutionHistory;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.workflow.Workflow;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class S1WFUtils {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    private static final int MAX_INBOUND_MESSAGE_SIZE = 52_428_800;

    private static final WorkflowServiceStubsOptions.ChannelInitializer channelInitializer = new WorkflowServiceStubsOptions.ChannelInitializer() {
        public void initChannel(ManagedChannelBuilder builder) {
            ((NettyChannelBuilder) builder).maxInboundMessageSize(MAX_INBOUND_MESSAGE_SIZE);
        }
    };

    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(
//            WorkflowServiceStubsOptions.newBuilder()
//                    .setChannelInitializer(channelInitializer)
//                    .build()
    );

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    // task queue (server "end-point") that worker(s) listen to
    public static final String taskQueue = "c1TaskQueue";

    public static final RetryOptions NO_RETRY = RetryOptions.newBuilder().setMaximumAttempts(1).build();


    /**
     * Prints workflow executions, given a provided query. Note this requires ES.
     * For non-es use ListOpenWorkflowExecutionsRequest and ListClosedWorkflowExecutionRequest
     *
     * @param query
     */
    private static void listWorkflowExecutionsWithQuery(String query, ByteString token) {

        ListWorkflowExecutionsRequest request;

        if (token == null) {
            request =
                    ListWorkflowExecutionsRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setQuery(query)
                            .build();
        } else {
            request =
                    ListWorkflowExecutionsRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setQuery(query)
                            .setNextPageToken(token)
                            .build();
        }
        ListWorkflowExecutionsResponse response =
                service.blockingStub().listWorkflowExecutions(request);
        for (WorkflowExecutionInfo workflowExecutionInfo : response.getExecutionsList()) {
            System.out.println("Workflow ID: " + workflowExecutionInfo.getExecution().getWorkflowId() + " Run ID: " +
                    workflowExecutionInfo.getExecution().getRunId() + " Status: " + workflowExecutionInfo.getStatus());
            if(workflowExecutionInfo.getParentExecution() != null) {
                System.out.println("****** PARENT: " + workflowExecutionInfo.getParentExecution().getWorkflowId() + " - " +
                        workflowExecutionInfo.getParentExecution().getRunId());
            }
        }

        if (response.getNextPageToken() != null && response.getNextPageToken().size() > 0) {
            listWorkflowExecutionsWithQuery(query, response.getNextPageToken());
        }
    }

    private static void printCronSchedulesFor(String wfid) {
        System.out.println("****** WFID: " + wfid);
        ListWorkflowExecutionsRequest request =  ListWorkflowExecutionsRequest.newBuilder()
                .setNamespace(client.getOptions().getNamespace())
                .setQuery("WorkflowId=\"" + wfid + "\"")
                .setPageSize(1000)
                .build();

        ListWorkflowExecutionsResponse response =
                service.blockingStub().listWorkflowExecutions(request);
        for (WorkflowExecutionInfo info : response.getExecutionsList()) {
           System.out.println("CRON: " + getCronSchedule(info.getExecution()));
        }
    }

    private static void printFailedWorkflowsWithReason(ByteString token) {
        ListWorkflowExecutionsRequest request;

        if (token == null) {
            request = ListWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .setQuery("ExecutionStatus='Failed'")
                    .setPageSize(1000)
                    .build();
        } else {
            request = ListWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .setQuery("ExecutionStatus='Failed'")
                    .setNextPageToken(token)
                    .setPageSize(1000)
                    .build();
        }
        ListWorkflowExecutionsResponse response =
                service.blockingStub().listWorkflowExecutions(request);
        for (WorkflowExecutionInfo info : response.getExecutionsList()) {
            HistoryEvent lastHistoryEvent = getLastHistoryEvent(info.getExecution(), null);
            // here you can get the failure and compare/check against what you want/need
            System.out.println("wfid: " + info.getExecution().getWorkflowId());
            System.out.println("failure: " + lastHistoryEvent.getWorkflowExecutionFailedEventAttributesOrBuilder().getFailure());
            System.out.println("history: " + lastHistoryEvent);
        }

        if (response.getNextPageToken() != null && response.getNextPageToken().size() > 0) {
            printFailedWorkflowsWithReason(response.getNextPageToken());
        }
    }


    public static void terminateWorkflows(ByteString token) {
        ListOpenWorkflowExecutionsRequest req;
        if(token == null) {
            req = ListOpenWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    // note you can set filters here too
//                .setTypeFilter(WorkflowTypeFilter.newBuilder()
//                        .setName(workflowType)
//                        .build())
//                .setStartTimeFilter(StartTimeFilter.newBuilder()
//                        .setEarliestTime(time)
//                        .build())
                    .build();
        } else {
            req = ListOpenWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .setNextPageToken(token)
                    .build();
        }

        ListOpenWorkflowExecutionsResponse res = service.blockingStub().listOpenWorkflowExecutions(req);
        List<WorkflowExecutionInfo> infoList = res.getExecutionsList();
        for(WorkflowExecutionInfo info : infoList) {
            WorkflowStub untyped = client.newUntypedWorkflowStub(info.getExecution().getWorkflowId(),
                    Optional.of(info.getExecution().getRunId()), Optional.empty());
            untyped.terminate("my reason"); //
        }

        if (res.getNextPageToken() != null && res.getNextPageToken().size() > 0) {
            terminateWorkflows(res.getNextPageToken());
        }
    }

    public static String getCronSchedule(WorkflowExecution wfExec) {
        GetWorkflowExecutionHistoryRequest req = GetWorkflowExecutionHistoryRequest.newBuilder()
                .setNamespace(client.getOptions().getNamespace())
                .setExecution(wfExec)
                .build();
        GetWorkflowExecutionHistoryResponse res =
                service.blockingStub().getWorkflowExecutionHistory(req);

        HistoryEvent firstEvent = res.getHistory().getEvents(0);

        return firstEvent.getWorkflowExecutionStartedEventAttributes().getCronSchedule();
    }

    public static HistoryEvent getLastHistoryEvent(WorkflowExecution wfExec, ByteString token) {
        GetWorkflowExecutionHistoryRequest request;
        HistoryEvent lastHistoryEvent;

        if (token == null) {
            request =
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setExecution(wfExec)
                            .build();
        } else {
            request =
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setExecution(wfExec)
                            .setNextPageToken(token)
                            .build();
        }
        GetWorkflowExecutionHistoryResponse response =
                service.blockingStub().getWorkflowExecutionHistory(request);
        lastHistoryEvent = response.getHistory().getEventsList().get(response.getHistory().getEventsList().size() - 1);
        if (response.getNextPageToken() != null && response.getNextPageToken().size() > 0) {
            return getLastHistoryEvent(wfExec, response.getNextPageToken());
        } else {
            return lastHistoryEvent;
        }
    }

    private static void listOpenWorkflowsByStartTime(long seconds) {
        ListOpenWorkflowExecutionsRequest req = ListOpenWorkflowExecutionsRequest
                .newBuilder()
                .setTypeFilter(WorkflowTypeFilter.newBuilder().build())
                .setStartTimeFilter(StartTimeFilter.newBuilder()
                        .setEarliestTime(Timestamp.newBuilder()
                                .setSeconds(seconds)
                                .build())
                        .build())
                .setNamespace(client.getOptions().getNamespace())
                .build();

        ListOpenWorkflowExecutionsResponse res =
                service.blockingStub().listOpenWorkflowExecutions(req);
        for(WorkflowExecutionInfo info : res.getExecutionsList()) {
            System.out.println("******* INFO: " + info.getExecution().getWorkflowId() + " - " +
                    info.getStartTime().getSeconds());
        }
    }


    private static void getActivitiesWithRetriesOver(int retryCount) {
        ListOpenWorkflowExecutionsRequest listOpenWorkflowExecutionsRequest =
                ListOpenWorkflowExecutionsRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .build();

        ListOpenWorkflowExecutionsResponse listOpenWorkflowExecutionsResponse =
                service.blockingStub().listOpenWorkflowExecutions(listOpenWorkflowExecutionsRequest);
        for (WorkflowExecutionInfo info : listOpenWorkflowExecutionsResponse.getExecutionsList()) {
            DescribeWorkflowExecutionRequest describeWorkflowExecutionRequest =
                    DescribeWorkflowExecutionRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setExecution(info.getExecution()).build();
            DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse =
                    service.blockingStub().describeWorkflowExecution(describeWorkflowExecutionRequest);
            for (PendingActivityInfo activityInfo : describeWorkflowExecutionResponse.getPendingActivitiesList()) {
//                if (activityInfo.getAttempt() > retryCount) {
                    System.out.println("Activity Type: " + activityInfo.getActivityType());
                    System.out.println("Activity attempt: " + activityInfo.getAttempt());
                    System.out.println("Last failure message : " + activityInfo.getLastFailure().getMessage());
                    Payloads details = activityInfo.getLastFailure().getApplicationFailureInfo().getDetails();
                    for(Payload detailsPayload : details.getPayloadsList()) {
                        System.out.println("Detail: " + DataConverter.getDefaultInstance().fromPayload(detailsPayload, String.class, String.class));
                    }
                    // ...
//                }
            }
        }
    }

    private static void getPendingActivitiesLastFailureMessageDetails(WorkflowExecution exec) {
        DescribeWorkflowExecutionRequest req =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setExecution(exec).build();
        DescribeWorkflowExecutionResponse res =
                service.blockingStub().describeWorkflowExecution(req);
        for (PendingActivityInfo activityInfo : res.getPendingActivitiesList()) {
            System.out.println("Activity Type: " + activityInfo.getActivityType());
            System.out.println("Activity attempt: " + activityInfo.getAttempt());
            System.out.println("Last failure message : " + activityInfo.getLastFailure().getMessage());
            Payloads details = activityInfo.getLastFailure().getApplicationFailureInfo().getDetails();
            for(Payload detailsPayload : details.getPayloadsList()) {
                // you will need to know the details type here
                System.out.println("Detail: " + DataConverter.getDefaultInstance().fromPayload(detailsPayload,
                        String.class, String.class));
            }
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
        for (WorkflowExecutionInfo workflowExecutionInfo : listWorkflowExecutionsResponse.getExecutionsList()) {

            WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowExecutionInfo.getExecution().getWorkflowId(),
                    Optional.empty(), Optional.empty());
            Customer customer = existingUntyped.query("getCustomer", Customer.class);
            System.out.println("Workflow ID: " + workflowExecutionInfo.getExecution().getWorkflowId()
                    + " Status: " + workflowExecutionInfo.getStatus()
                    + " Customer: " + customer);
        }
    }

    private static void printArchivedWorkflowExecutions(String query) {
        ListWorkflowExecutionsRequest listWorkflowExecutionRequest =
                ListWorkflowExecutionsRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setQuery(query)
                        .build();
        ListWorkflowExecutionsResponse listWorkflowExecutionsResponse =
                service.blockingStub().listWorkflowExecutions(listWorkflowExecutionRequest);
        for (WorkflowExecutionInfo workflowExecutionInfo : listWorkflowExecutionsResponse.getExecutionsList()) {
            GetWorkflowExecutionHistoryRequest getWorkflowExecutionHistoryRequest =
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setNamespace("default")
                            .setExecution(workflowExecutionInfo.getExecution())
                            .build();
            GetWorkflowExecutionHistoryResponse getWorkflowExecutionHistoryResponse =
                    service.blockingStub().getWorkflowExecutionHistory(getWorkflowExecutionHistoryRequest);
            for (HistoryEvent historyEvent : getWorkflowExecutionHistoryResponse.getHistory().getEventsList()) {
                historyEvent.getWorkflowExecutionStartedEventAttributes().getInput();
                // ...
            }
        }
    }

    public static String getWorkflowExecutionHistoryAsJson(String wfId, String wfRunId) {
        GetWorkflowExecutionHistoryRequest request =
                GetWorkflowExecutionHistoryRequest.newBuilder()
                        .setNamespace("default")
                        .setExecution(WorkflowExecution.newBuilder()
                                .setWorkflowId(wfId)
                                .setRunId(wfRunId)
                                .build())
                        .build();
        return new WorkflowExecutionHistory(
                service.blockingStub().getWorkflowExecutionHistory(request).getHistory()).toJson(true);
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
        System.out.println("**** PARENT: " + resp.getWorkflowExecutionInfo().getParentExecution().getWorkflowId());

        WorkflowExecutionInfo workflowExecutionInfo = resp.getWorkflowExecutionInfo();
        return workflowExecutionInfo.getStatus().toString();
    }

    public static long getFirstWorkflowTaskStartedEventId(String wfId, String wfRunId, String namespace) {
        GetWorkflowExecutionHistoryRequest request =
                GetWorkflowExecutionHistoryRequest.newBuilder()
                        .setNamespace(namespace)
                        .setExecution(WorkflowExecution.newBuilder()
                                .setWorkflowId(wfId)
                                .setRunId(wfRunId)
                                .build()).build();
        History history = service.blockingStub().getWorkflowExecutionHistory(request).getHistory();
        for(HistoryEvent he : history.getEventsList()) {
            if(he.getEventType().equals(EventType.EVENT_TYPE_ACTIVITY_TASK_SCHEDULED)) {
                return he.getEventId();
            }
        }
        // not found
        return -1;
    }

    public static void printDescribeWorkflowExecution(WorkflowClient client, String workflowId, String runId) {
        WorkflowStub wfStub = client.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty());
        DescribeWorkflowExecutionRequest request = DescribeWorkflowExecutionRequest.newBuilder()
                .setNamespace(client.getOptions().getNamespace())
                .setExecution(wfStub.getExecution()).build();
        DescribeWorkflowExecutionResponse response = service.blockingStub().describeWorkflowExecution(request);
        // response includes info like workflow exec info, pending activities (and their count),
        // pending child wfs, etc
    }

    /**
     * Print execution history for a workflow given its workflow id and run id
     */
    public static void printWorkflowExecutionHistory(WorkflowClient client, String workflowId, String runId, WorkflowStub wfStub, ByteString token) {
        if (wfStub == null) {
            wfStub = client.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty());
        }

        GetWorkflowExecutionHistoryRequest workflowExecutionHistoryRequest;
        if (token == null) {
            workflowExecutionHistoryRequest =
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setExecution(wfStub.getExecution())
                            .build();
        } else {
            workflowExecutionHistoryRequest =
                    GetWorkflowExecutionHistoryRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setExecution(wfStub.getExecution())
                            .setNextPageToken(token)
                            .build();
        }

        GetWorkflowExecutionHistoryResponse workflowExecutionHistoryResponse =
                service.blockingStub().getWorkflowExecutionHistory(workflowExecutionHistoryRequest);
        for (HistoryEvent historyEvent : workflowExecutionHistoryResponse.getHistory().getEventsList()) {
            System.out.println(historyEvent.getEventId() + " - " + historyEvent.getEventType());
        }
        if (workflowExecutionHistoryResponse.getNextPageToken() != null && workflowExecutionHistoryResponse.getNextPageToken().size() > 0) {
            printWorkflowExecutionHistory(client, workflowId, runId, wfStub, workflowExecutionHistoryResponse.getNextPageToken());
        }
    }

    /**
     * Reset a workflow to a specific taskFinishEventId given its workfow id and run id
     */
    public static void resetWorkflow(WorkflowClient client, String workflowId, String runId, long taskFinishEventId,
                                     String reason) {
        WorkflowStub existingUntyped = client.newUntypedWorkflowStub(workflowId, Optional.of(runId), Optional.empty());
        ResetWorkflowExecutionRequest resetWorkflowExecutionRequest =
                ResetWorkflowExecutionRequest.newBuilder()
                        .setRequestId(UUID.randomUUID().toString())
                        .setNamespace(client.getOptions().getNamespace())
                        .setWorkflowExecution(existingUntyped.getExecution())
                        .setWorkflowTaskFinishEventId(taskFinishEventId)
                        .setReason(reason)
                        .build();

        ResetWorkflowExecutionResponse resetWorkflowExecutionResponse =
                client.getWorkflowServiceStubs().blockingStub().resetWorkflowExecution(resetWorkflowExecutionRequest);

        System.out.println("Reset execution id: " + resetWorkflowExecutionResponse.getRunId());
    }

    public static void enableNamespaceArchival(String namespace, String historyArchivalURI, String visibilityArchivalURI) {
        UpdateNamespaceResponse res = client.getWorkflowServiceStubs().blockingStub().updateNamespace(
                UpdateNamespaceRequest.newBuilder()
                        .setNamespace(namespace)
                        .setConfig(NamespaceConfig.newBuilder()
                                .setVisibilityArchivalState(ArchivalState.ARCHIVAL_STATE_ENABLED)
                                .setHistoryArchivalState(ArchivalState.ARCHIVAL_STATE_ENABLED)
                                .setHistoryArchivalUri(historyArchivalURI)
                                .setVisibilityArchivalUri(visibilityArchivalURI)
                                .build())
                        .build()
        );
        System.out.println(res.getNamespaceInfo());
    }

    public static void disableNamespaceArchival(String namespace) {
        UpdateNamespaceResponse res = client.getWorkflowServiceStubs().blockingStub().updateNamespace(
                UpdateNamespaceRequest.newBuilder()
                        .setNamespace(namespace)
                        .setConfig(NamespaceConfig.newBuilder()
                                .setVisibilityArchivalState(ArchivalState.ARCHIVAL_STATE_DISABLED)
                                .setHistoryArchivalState(ArchivalState.ARCHIVAL_STATE_DISABLED)
                                .build())
                        .build()
        );
        System.out.println(res.getNamespaceInfo());
    }

    public static void main(String[] args) {
//        terminateWorkflows(null);
//
//        listOpenWorkflowsWithFilters("CronWorkflow", Timestamp.newBuilder()
//                .setSeconds(1650313912)
//                .build());

//        System.out.println("Cron: " +
//        getCronSchedule(WorkflowExecution.newBuilder()
//                .setWorkflowId("HelloCronWorkflow")
//                .setRunId("ae1f0112-4aab-4d43-a4d8-c6e455caa6d4")
//                .build())
//        );

//        listOpenWorkflowsByStartTime(1649807246);
//        printCronSchedulesFor("NEWc3s5Workflow2");
//        getWorkflowExecutionHistoryAsJson("c1GreetingWorkflow", "87411ad0-5247-454a-91f5-ac182e037f19");
        //printArchivedWorkflowExecutions("ExecutionStatus=2");

        //printWorkflowExecutionHistory(client, "c1GreetingWorkflow", "ca6d5cee-cefa-41d6-bade-fdca490d90f4");
        resetWorkflow(client, "myworkflow", "8b1ace32-3934-49de-b12f-a2cda2e03916", 3, "test reasons");
//        getActivitiesWithRetriesOver(2);
//        getPendingActivitiesLastFailureMessageDetails(WorkflowExecution.newBuilder()
//                .setWorkflowId("HelloActivityWorkflow")
//                .build());
//        try {
//            getWorkflowStatus(client, "055db52b-b7b6-3108-8c04-d5a36f668df6");
//        } catch (Exception e) {
//            System.out.println("****** E: " + e.getClass().getName());
//            System.out.println(((StatusRuntimeException) e).getStatus().getDescription()
//            );
//        }
        //printWorkflowExecutionHistory(client, "HelloPeriodicWorkflow", "6c989861-deca-47e7-bfdf-05970b810355", null, null);

//        printFailedWorkflowsWithReason(null);

        //printDescribeWorkflowExecution(client, "HelloPeriodicWorkflow", "6c989861-deca-47e7-bfdf-05970b810355");
//        ListNamespacesResponse response = client.getWorkflowServiceStubs().blockingStub().listNamespaces(
//                ListNamespacesRequest.newBuilder().build()
//        );
//        List<DescribeNamespaceResponse> namespaces = response.getNamespacesList();
//        for(DescribeNamespaceResponse nr : namespaces) {
//            System.out.println("*********** " + nr.getNamespaceInfo().getName());
//        }
//
//        try {
//            DescribeNamespaceResponse resp = client.getWorkflowServiceStubs().blockingStub().describeNamespace(
//                    DescribeNamespaceRequest.newBuilder()
//                            .setNamespace("abc")
//                            .build()
//            );
//
//            response.getNamespaces(0).getNamespaceInfo().getName();
//        } catch(StatusRuntimeException e) {
//            // ....
//        }


        // 1) for signalWithStart and NPE  -- dont forget to fix issue and restart worker before restarting!!
        //printWorkflowExecutionHistory(client, "c1GreetingWorkflow", "06c1c822-2e3e-40ba-94f2-95564c3f7d0d");
        //resetWorkflow(client, "c1GreetingWorkflow", "06c1c822-2e3e-40ba-94f2-95564c3f7d0d", 9);

//        // 2) for searchAttributes
//        // Using built-in search attributes
//        listWorkflowExecutionsWithQuery("ExecutionStatus='Completed'", null);
//        // Customer workflows where customer is over 20
//        printWorkflowExecutionsAndShowCustomerInfo("CustomerAge > 20");
//        // Customer workflows where customer title is Ms and age is over 30
//        printWorkflowExecutionsAndShowCustomerInfo("CustomerAge > 20 AND CustomerTitle='Ms'");
//        // Customer workflows where customer speaks Spanish
//        printWorkflowExecutionsAndShowCustomerInfo("CustomerLanguages LIKE \"%Spanish\"");

//        System.out.println(
//        getFirstWorkflowTaskStartedEventId("hello_world_workflowID", "37c70ec6-24e8-48cf-ad8e-d034e1f4b754", "default")
//        );

    }
}
