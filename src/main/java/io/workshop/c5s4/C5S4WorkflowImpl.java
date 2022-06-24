package io.workshop.c5s4;

import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.Random;

public class C5S4WorkflowImpl implements C5S4Workflow {

    @Override
    public void exec() {

        C5S4Activity activity = Workflow.newActivityStub(C5S4Activity.class,
                ActivityOptions.newBuilder()
                        .setStartToCloseTimeout(Duration.ofSeconds(2))
                        .build());

        // show why not to use system out :)
        // use workflow logger instead
        System.out.println("**** HERE!");

        Random random = new Random();
        int rand = random.nextInt(10 - 1 + 1) + 1;

        for(int i =0;i<rand;i++) {
            activity.runActivity(i);
        }

        // first show that this code actually runs cause of
        // worker caching (sticky queue)
        // then break determinism by restarting worker with sleep

//        Workflow.sleep(Duration.ofSeconds(15));
    }
}
