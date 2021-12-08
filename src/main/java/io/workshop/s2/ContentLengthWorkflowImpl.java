package io.workshop.s2;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.*;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ContentLengthWorkflowImpl implements ContentLengthWorkflow {

    private Logger logger = Workflow.getLogger(this.getClass().getName());
    private ContentLengthInfo defaultInfo;

    private final ContentLengthActivity activities =
            Workflow.newActivityStub(
                    ContentLengthActivity.class,
                    ActivityOptions.newBuilder().setStartToCloseTimeout(Duration.ofSeconds(2)).build());


    @Override
    public ContentLengthInfo execute() {

        return invokeAndWaitForResult();

        //return invokeAsyncAndWaitForResult();

        //return invokeParallelWaitForAll();

        //return invokeParallelWaitForFirst();

        //return invokeParallelBranches();

        //return invokeWithRetries();

        //return invokeNoRetriesHandleError();

        //return invokeNoRetryHandleErrorWithSaga();
    }

    private ContentLengthInfo invokeAndWaitForResult() {
        return activities.count("https://temporal.io/");
    }

    private ContentLengthInfo invokeAsyncAndWaitForResult() {
        Promise<ContentLengthInfo> activityPromise = Async.function(activities::count, "https://temporal.io/");

        return activityPromise.get();
    }

    private ContentLengthInfo invokeParallelWaitForAll() {
        ContentLengthInfo info = new ContentLengthInfo();

        List<Promise<ContentLengthInfo>> promiseList = new ArrayList<>();
        // Async.function takes lambda that returns a result
        // Async.procedure takes lambda, does not return result
        promiseList.add(Async.function(activities::count, "https://temporal.io/"));
        promiseList.add(Async.function(activities::count, "https://www.google.com/"));

        // Invoke all activities in parallel. Wait for all to complete
        Promise.allOf(promiseList).get();

        for (Promise<ContentLengthInfo> promise : promiseList) {
            if (promise.getFailure() == null) {
                info.add(promise.get());
            } else {
                throw Activity.wrap(promise.getFailure());
            }
        }

        return info;
    }

    private ContentLengthInfo invokeParallelWaitForFirst() {
        List<Promise<ContentLengthInfo>> promiseList = new ArrayList<>();
        promiseList.add(Async.function(activities::count, "https://temporal.io/"));
        promiseList.add(Async.function(activities::count, "https://www.google.com/"));

        // Invoke all activities in parallel. Wait for first to complete
        return Promise.anyOf(promiseList).get();
    }

    private ContentLengthInfo invokeParallelBranches() {
        ContentLengthInfo info = new ContentLengthInfo();

        List<Promise<ContentLengthInfo>> promiseList = new ArrayList<>();
        promiseList.add(Async.function(this::branch, 3, "https://temporal.io/"));
        promiseList.add(Async.function(this::branch, 1, "https://www.google.com/"));
        promiseList.add(Async.function(this::branch, 2, "https://www.espn.com/"));

        // Invoke all branches in parallel. Wait for all to complete
        Promise.allOf(promiseList).get();

        for (Promise<ContentLengthInfo> promise : promiseList) {
            if (promise.getFailure() == null) {
                info.add(promise.get());
            } else {
                throw Activity.wrap(promise.getFailure());
            }
        }

        return info;
    }

    // Branches
    private ContentLengthInfo branch(int seconds, String url) {
        Workflow.sleep(Duration.ofSeconds(seconds));
        return activities.count(url);
    }

    private ContentLengthInfo invokeWithRetries() {
        return activities.count("DOESNOTEXIST");
    }

    private ContentLengthInfo invokeNoRetriesHandleError() {
        // NOTE TO SELF - set max attempts to 1 :)
        try {
            return activities.count("DOESNOTEXIST");
        } catch (ActivityFailure failure) {
            logger.warn("Error: " + failure.getCause().getMessage());
            return new ContentLengthInfo();
        }
    }

    private ContentLengthInfo invokeNoRetryHandleErrorWithSaga() {
        // NOTE TO SELF - set max attempts to 1 :)

        // define a Saga object. supports parallel! if not parallel then its reverse order
        Saga saga = new Saga(new Saga.Options.Builder().setParallelCompensation(false).build());
        // add compensation steps to saga -- note you can have multiple
        // compensation can include activity executions
        saga.addCompensation(this::getDefaultCount);
        // but it can also include any arbitrary logic
        saga.addCompensation(
                () -> logger.info("Performing first step of compensation!"));

        try {
            return activities.count("DOESNOTEXIST");
        } catch (ActivityFailure failure) {
            logger.warn("Error: " + failure.getCause().getMessage());

            // Note - compensation is manually triggered, not automatic
            saga.compensate();
            return defaultInfo;
        }
    }

    private void getDefaultCount() {
        defaultInfo =  activities.count("https://temporal.io/");
    }
}
