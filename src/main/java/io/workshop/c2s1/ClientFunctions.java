package io.workshop.c2s1;

import com.google.protobuf.ByteString;
import com.google.protobuf.Duration;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.TaskQueueKind;
import io.temporal.api.enums.v1.TaskQueueType;
import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.api.query.v1.WorkflowQuery;
import io.temporal.api.taskqueue.v1.PollerInfo;
import io.temporal.api.taskqueue.v1.TaskQueue;
import io.temporal.api.workflow.v1.PendingActivityInfo;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.api.workflowservice.v1.*;
import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.List;

public class ClientFunctions {

    // Initializes all gRPC stubs (connection, blocking, future)
    // Note: by default target set to 127.0.0.1:7233, can change via workflowServiceStubsOptions
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();

    // Client to the Temporal service used to start and query workflows by external processes
    // Note: by default set to "default" namespace, can change via WorkfowClientOptions
    public static final WorkflowClient client = WorkflowClient.newInstance(service);

    public static final String taskQueue = "c2s3TaskQueue";

    public static void main(String[] args) {
        // listOpenWorkflows(null); // Does not need ES

        // listClosedWorkflows(null); // Does not need ES

        // TODO - set workflow id and run id to actually open wf
        WorkflowExecution execution = WorkflowExecution.newBuilder()
                .setWorkflowId("childWorkflow-0")
                .setRunId("2d08ea81-f628-4ab1-afb3-96d8fa571e7a")
                .build();
//        describeWorkflowExecution(execution);

        //printFailedWorkflowsWithReason(null);

        // printWorkflowStacktrace(execution);

        // TODO - set workflow id and run id to an actual cron wf
        WorkflowExecution cronExecution = WorkflowExecution.newBuilder()
                .setWorkflowId("CronWorkflow")
                .setRunId("48d77756-9255-4a79-85ea-6c2d87dd6fab")
                .build();
        //getCronScheduleFor(cronExecution);

        // TODO - set workflow id and run id to an actual cron wf
        WorkflowExecution activityRetriesExec = WorkflowExecution.newBuilder()
                .setWorkflowId("WfWithActivityRetries")
                .setRunId("578966bd-e202-4c8b-8f1b-99b492c374fb")
                .build();
        //getActivitiesWithRetriesOver(5, null);

        //listNamespaces();

        // createNamespace("workshopnamespace");

        //getClusterInfo();

        describeTaskQueue();
    }

    private static void listOpenWorkflows(ByteString token) {
        ListOpenWorkflowExecutionsRequest req;
        if (token == null) {
            System.out.println("******** Open workflow executions: ");
            req = ListOpenWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .build();
        } else {
            req = ListOpenWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .setNextPageToken(token)
                    .build();
        }
        ListOpenWorkflowExecutionsResponse res =
                service.blockingStub().listOpenWorkflowExecutions(req);
        for (WorkflowExecutionInfo info : res.getExecutionsList()) {
            System.out.println("* id: " + info.getExecution().getWorkflowId() + " rid: " +
                    info.getExecution().getRunId() + " status: " + info.getStatus().name());
        }

        if (res.getNextPageToken() != null && res.getNextPageToken().size() > 0) {
            listOpenWorkflows(res.getNextPageToken());
        }
    }

    private static void listClosedWorkflows(ByteString token) {
        ListClosedWorkflowExecutionsRequest req;
        if (token == null) {
            System.out.println("******** Closed workflow executions: ");
            req = ListClosedWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .build();
        } else {
            req = ListClosedWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .setNextPageToken(token)
                    .build();
        }
        ListClosedWorkflowExecutionsResponse res =
                service.blockingStub().listClosedWorkflowExecutions(req);
        for (WorkflowExecutionInfo info : res.getExecutionsList()) {
            System.out.println("* id: " + info.getExecution().getWorkflowId() + " rid: " +
                    info.getExecution().getRunId() + " status: " + info.getStatus().name());
        }

        if (res.getNextPageToken() != null && res.getNextPageToken().size() > 0) {
            listOpenWorkflows(res.getNextPageToken());
        }
    }

    private static void describeWorkflowExecution(WorkflowExecution execution) {
        DescribeWorkflowExecutionRequest req =
                DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setExecution(execution)
                        .build();

        DescribeWorkflowExecutionResponse res =
                service.blockingStub().describeWorkflowExecution(req);

        System.out.println("Execution Config: \nTask queue name:" + res.getExecutionConfig().getTaskQueue().getName()
                + "\n Task queue kind: " + res.getExecutionConfig().getTaskQueue().getKind()
                + "\nWorkflowExecutionTimeout: " + res.getExecutionConfig().getWorkflowExecutionTimeout());

        System.out.println("Workflow execution: \nWorkflow id: " + res.getWorkflowExecutionInfo().getExecution().getWorkflowId()
                + "\nRun id: " + res.getWorkflowExecutionInfo().getExecution().getRunId()
                + "\nType: " + res.getWorkflowExecutionInfo().getType().getName()
                + "\nStart time: " + res.getWorkflowExecutionInfo().getStartTime()
                + "\nClose time: " + res.getWorkflowExecutionInfo().getCloseTime()
                + "\nStatus: " + res.getWorkflowExecutionInfo().getStatus().name());
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
            System.out.println("**** wfid: " + info.getExecution().getWorkflowId());
            System.out.println("**** failure: " + lastHistoryEvent.getWorkflowExecutionFailedEventAttributesOrBuilder().getFailure());
            System.out.println("**** history: " + lastHistoryEvent);
        }

        if (response.getNextPageToken() != null && response.getNextPageToken().size() > 0) {
            printFailedWorkflowsWithReason(response.getNextPageToken());
        }
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

    public static void printWorkflowStacktrace(WorkflowExecution workflowExecution) {
        QueryWorkflowRequest req =
                QueryWorkflowRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setQuery(WorkflowQuery.newBuilder()
                                .setQueryType("__stack_trace").build())
                        .setExecution(workflowExecution)
                        .build();
        QueryWorkflowResponse res = service.blockingStub().queryWorkflow(req);
        System.out.println("** Stack Trace: \n" + res.getQueryResult());
    }

    public static void getCronScheduleFor(WorkflowExecution execution) {
        GetWorkflowExecutionHistoryRequest request =
                GetWorkflowExecutionHistoryRequest.newBuilder()
                        .setNamespace(client.getOptions().getNamespace())
                        .setExecution(execution)
                        .setMaximumPageSize(1)
                        .build();
        GetWorkflowExecutionHistoryResponse result =
                service.blockingStub().getWorkflowExecutionHistory(request);
        List<HistoryEvent> events = result.getHistory().getEventsList();
        String cronSchedule =
                events.get(0).getWorkflowExecutionStartedEventAttributes().getCronSchedule();
        System.out.println("Cron: " + cronSchedule);
    }

    private static void getActivitiesWithRetriesOver(int retryCount, ByteString token) {
        ListOpenWorkflowExecutionsRequest req;
        if (token == null) {
            req = ListOpenWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .build();
        } else {
            req = ListOpenWorkflowExecutionsRequest.newBuilder()
                    .setNamespace(client.getOptions().getNamespace())
                    .setNextPageToken(token)
                    .build();
        }

        ListOpenWorkflowExecutionsResponse res =
                service.blockingStub().listOpenWorkflowExecutions(req);
        for (WorkflowExecutionInfo info : res.getExecutionsList()) {
            DescribeWorkflowExecutionRequest describeWorkflowExecutionRequest =
                    DescribeWorkflowExecutionRequest.newBuilder()
                            .setNamespace(client.getOptions().getNamespace())
                            .setExecution(info.getExecution()).build();
            DescribeWorkflowExecutionResponse describeWorkflowExecutionResponse =
                    service.blockingStub().describeWorkflowExecution(describeWorkflowExecutionRequest);
            for (PendingActivityInfo activityInfo : describeWorkflowExecutionResponse.getPendingActivitiesList()) {
                if (activityInfo.getAttempt() > retryCount) {
                    System.out.println("Activity Type: " + activityInfo.getActivityType());
                    System.out.println("Activity attempt: " + activityInfo.getAttempt());
                    System.out.println("Last failure message : " + activityInfo.getLastFailure().getMessage());
                    // ...
                }
            }
        }
        if (res.getNextPageToken() != null && res.getNextPageToken().size() > 0) {
            getActivitiesWithRetriesOver(retryCount, res.getNextPageToken());
        }

    }

    public static void listNamespaces() {
        // Note listNamespaceRequest is paginated!
        ListNamespacesRequest req =
                ListNamespacesRequest.newBuilder()
                        .setPageSize(1)
                        .build();
        ListNamespacesResponse res = service.blockingStub().listNamespaces(req);
        for (DescribeNamespaceResponse response : res.getNamespacesList()) {
            System.out.println("Namespace: " + response.getNamespaceInfo().getName() + " - id: " +
                    response.getNamespaceInfo().getId());
        }
    }

    public static void createNamespace(String name) {
        RegisterNamespaceRequest req = RegisterNamespaceRequest.newBuilder()
                .setNamespace(name)
                .setWorkflowExecutionRetentionPeriod(Duration.newBuilder().setSeconds(48 * 60 * 60).build())
                .build();
        service.blockingStub().registerNamespace(req);
    }

    public static void getClusterInfo() {
        GetClusterInfoResponse res = client
                .getWorkflowServiceStubs()
                .blockingStub()
                .getClusterInfo(GetClusterInfoRequest.newBuilder().build());

        System.out.println("*** cluster id: " + res.getClusterId());
        System.out.println("*** server version: " + res.getServerVersion());
        System.out.println("*** persistence store: " + res.getPersistenceStore());

    }

    public static void describeTaskQueue() {
        TaskQueue tq = TaskQueue.newBuilder().setKind(TaskQueueKind.TASK_QUEUE_KIND_NORMAL).setName(taskQueue).build();

        DescribeTaskQueueRequest req = DescribeTaskQueueRequest.newBuilder()
                .setNamespace(client.getOptions().getNamespace())
                .setTaskQueue(tq)
                .setTaskQueueType(TaskQueueType.TASK_QUEUE_TYPE_WORKFLOW)
                .build();
        DescribeTaskQueueResponse res = service.blockingStub().describeTaskQueue(req);
        for (PollerInfo pollerInfo : res.getPollersList()) {
            System.out.println("poller id: " + pollerInfo.getIdentity());
            System.out.println("last access: " + pollerInfo.getLastAccessTime());
        }
    }

}
