package io.workshop.c5s1;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.ExternalWorkflowStub;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WorkflowsImpls {
    public static class WorkflowAImpl implements Workflows.WorkflowA {
        String bulkResponse = null;
        Logger logger = Workflow.getLogger(this.getClass().getName());
        Activities activities = Workflow.newActivityStub(Activities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

        @Override
        public String exec() {
            // signal bulk requester workflow via activity
            // not we dont use external workflow stub as that would mean
            // we need to have the workflow running
            activities.signalBulkRequester(Workflow.getInfo().getWorkflowId());

            // wait for bulk requester complete signal
            Workflow.await(() -> bulkResponse != null);
            logger.info("** " + Workflow.getInfo().getWorkflowId() + " receiveved build request completion event");
            return bulkResponse;
        }

        @Override
        public void requestCompleted(String response) {
            this.bulkResponse = response;
        }
    }

    public static class WorkflowBImpl implements Workflows.WorkflowB {
        String bulkResponse = null;
        Logger logger = Workflow.getLogger(this.getClass().getName());
        Activities activities = Workflow.newActivityStub(Activities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

        @Override
        public String exec() {
            // signal bulk requester workflow via activity
            // not we dont use external workflow stub as that would mean
            // we need to have the workflow running
            activities.signalBulkRequester(Workflow.getInfo().getWorkflowId());

            // wait for bulk requester complete signal
            Workflow.await(() -> bulkResponse != null);
            logger.info("** " + Workflow.getInfo().getWorkflowId() + " receiveved build request completion event");
            return bulkResponse;
        }

        @Override
        public void requestCompleted(String response) {
            this.bulkResponse = response;
        }
    }

    public static class WorkflowCImpl implements Workflows.WorkflowC {
        String bulkResponse = null;
        Logger logger = Workflow.getLogger(this.getClass().getName());
        Activities activities = Workflow.newActivityStub(Activities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

        @Override
        public String exec() {
            // signal bulk requester workflow via activity
            // not we dont use external workflow stub as that would mean
            // we need to have the workflow running
            activities.signalBulkRequester(Workflow.getInfo().getWorkflowId());

            // wait for bulk requester complete signal
            Workflow.await(() -> bulkResponse != null);
            logger.info("** " + Workflow.getInfo().getWorkflowId() + " receiveved build request completion event");
            return bulkResponse;
        }

        @Override
        public void requestCompleted(String response) {
            this.bulkResponse = response;
        }
    }

    public static class BulkRequesterWorkflowImpl implements Workflows.BulkRequesterWorkflow {
        List<String> bulkInput = new ArrayList<>();
        Activities activities = Workflow.newActivityStub(Activities.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());
        Logger logger = Workflow.getLogger(this.getClass().getName());
        @Override
        public void exec() {
            // wait for enough bulk request signals
            Workflow.await(() -> bulkInput.size() == 3);

            // make the bulk request
            activities.callBulkService(bulkInput);

            // signal each workflow back that bulk request was completed
            bulkInput.forEach(b -> {
                logger.info("* request complted for: " + b);
                ExternalWorkflowStub stub = Workflow.newUntypedExternalWorkflowStub(b);
                stub.signal("requestCompleted", "request completed for " + b);
            });

            // irl this would be a long running workflow that would make bulk request and then look for more signals
            // for this sample we just complete here. note this workflow would need to call
            // continueasnew after x number of bulk requests
        }

        @Override
        public void requestService(String requestorId) {
            bulkInput.add(requestorId);
        }
    }
}
