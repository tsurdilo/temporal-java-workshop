package io.workshop.c4s2;

import io.temporal.activity.ActivityOptions;
import io.temporal.internal.sync.PotentialDeadlockException;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class C4S2WorkflowImpl implements C4S2Workflow {

    private C4S2Activities activities = Workflow.newActivityStub(C4S2Activities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(2))
                    .build());

    @Override
    public void exec() {
        try {
            Thread.sleep(3 * 1000); // BAD (do not manipulate threads in workflow code!!)

            Workflow.sleep(Duration.ofSeconds(2));
            while (!runTillFalse()) ;
        } catch (IllegalStateException e) {
            System.out.println("********* IllegalStateException : " + e.getClass().getName());
        } catch (Exception e) {
            System.out.println("********* Exception : " + e.getClass().getName());
            e.printStackTrace();
        }
    }

    private boolean runTillFalse() {
        activities.first();
        return activities.second();
    }


}
