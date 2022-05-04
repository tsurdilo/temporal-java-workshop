package io.workshop.c4s2;

import io.temporal.client.WorkflowFailedException;
import io.temporal.failure.ApplicationFailure;
import io.temporal.failure.CanceledFailure;
import io.temporal.internal.sync.PotentialDeadlockException;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class C4S2WorkflowImpl implements C4S2Workflow {

    @Override
    public void exec() {
        try {
            // io.temporal.internal.sync.PotentialDeadlockException: Potential deadlock detected.
            // note this exception is going to be thrown in a different thread - "control thread"
            // so we cannot catch it!
            // Catching PotentialDeadlockException brings workflow
            // into a branch that will not and shouldn’t exist during replay.
            Thread.sleep(3 * 1000); // BAD (do not manipulate threads in workflow code!!)
//            Workflow.sleep(Duration.ofSeconds(3));

            // Show this next (remove thread.sleep)
            // This will cause UnableToAcquireLockException
            // our workflow task will time out (task timeout 10s) and server will place it on task queue again
            // worker picks it up and says "im already working on this and its not releasing control" and raise UnableToAcquireLockException
//            while(true);

        } catch (PotentialDeadlockException e) { // cannot catch - workflow task is replaying waiting for fix (don't do this)
            System.out.println("********* PotentialDeadlockException : " + e.getClass().getName());
        } catch (Exception e) {
            System.out.println("********* Exception : " + e.getClass().getName());
        }
    }
}
