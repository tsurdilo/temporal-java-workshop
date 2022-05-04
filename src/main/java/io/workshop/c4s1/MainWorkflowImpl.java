package io.workshop.c4s1;

import io.temporal.failure.ChildWorkflowFailure;
import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MainWorkflowImpl implements MainWorkflow {

    private String result = "";

    @Override
    public String execute(String input) {

        ChildWorkflowOne childOneStub = Workflow.newChildWorkflowStub(ChildWorkflowOne.class,
                ChildWorkflowOptions.newBuilder()
                .setWorkflowId("childOne")
                .setWorkflowRunTimeout(Duration.ofSeconds(10))
                .build());

        ChildWorkflowTwo childTwoStub = Workflow.newChildWorkflowStub(ChildWorkflowTwo.class,
                ChildWorkflowOptions.newBuilder()
                .setWorkflowId("childTwo")
                .setWorkflowRunTimeout(Duration.ofSeconds(2))
                .build());

        List<Promise<Void>> childPromiseList = new ArrayList<>();
        childPromiseList.add(Async.procedure(childOneStub::execChildOne, input));
        childPromiseList.add(Async.procedure(childTwoStub::execChildTwo, input));

        try {
            // Wait for all promises to complete
            Promise.allOf(childPromiseList).get();
        } catch (ChildWorkflowFailure e) {
            // cause is TimeoutFailure
            for (Promise<Void> promise : childPromiseList) {
                if(promise.getFailure() != null) {
                    System.out.println("**** Promise failure: " + promise.getFailure().getCause().getClass().getName());
                } else {
                    // isCompleted returns true if completed. not needed really in this case
                    // because we are waiting on all promised with allOf, just for example
                    if(promise.isCompleted()) {
                        result += promise.get();
                    }
                }
            }
        }
        return result;
    }
}
