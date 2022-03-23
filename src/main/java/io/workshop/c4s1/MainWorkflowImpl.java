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
    private ChildWorkflowOptions optionsOne = ChildWorkflowOptions.newBuilder()
            .setWorkflowId("childOne")
            .setWorkflowRunTimeout(Duration.ofSeconds(10))
            .build();

    private ChildWorkflowOptions optionsTwo = ChildWorkflowOptions.newBuilder()
            .setWorkflowId("childTwo")
            .setWorkflowRunTimeout(Duration.ofSeconds(2))
            .build();

    private String result = "";

    @Override
    public String execute(String input) {

        ChildWorkflowOne childOneStub = Workflow.newChildWorkflowStub(ChildWorkflowOne.class, optionsOne);
        ChildWorkflowTwo childTwoStub = Workflow.newChildWorkflowStub(ChildWorkflowTwo.class, optionsTwo);


        Promise<Void> childOnePromise = Async.procedure(childOneStub::execChildOne, input)
//                .exceptionally(e -> {
//                    // exception here is going to be ChildWorkflowFailure
//                    System.out.println("*********** CHILD1: " + e.getClass().getName());
//                    ChildWorkflowFailure failure = (ChildWorkflowFailure) e;
//                    System.out.println("ORIGINAL MESSAGE: " + failure.getCause().getClass().getName());
////                    return null;
////                    *********** CHILD2: io.temporal.failure.ChildWorkflowFailure
////                    ORIGINAL MESSAGE: io.temporal.failure.TimeoutFailure
//
//                    throw Workflow.wrap((ChildWorkflowFailure) e);
//                })
                ;


        Promise<Void> childTwoPromise = Async.procedure(childTwoStub::execChildTwo, input)
//                .exceptionally(e -> {
//                    // exception here is going to be ChildWorkflowFailure
//                    System.out.println("*********** CHILD2: " + e.getClass().getName());
//                    ChildWorkflowFailure failure = (ChildWorkflowFailure) e;
//                    System.out.println("ORIGINAL MESSAGE: " + failure.getCause().getClass().getName());
////                    return null;
//                    throw Workflow.wrap((ChildWorkflowFailure) e);
//                })
                ;

        List<Promise<Void>> childPromiseList = new ArrayList<>();
        childPromiseList.add(childOnePromise);
        childPromiseList.add(childTwoPromise);

        try {
            Promise.allOf(childPromiseList).get();
            for (Promise<Void> promise : childPromiseList) {
                if(promise.getFailure() != null) {
                    System.out.println("***** here!!");
                }
            }

        } catch (ChildWorkflowFailure e) {
            // cause is TimeoutFailure
            for (Promise<Void> promise : childPromiseList) {
                if(promise.getFailure() != null) {
                    // ...
                } else {
                    if(promise.isCompleted()) {
                        result += promise.get();
                    }
                }
            }
        }

        return result;
    }
}
