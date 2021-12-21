package io.workshop.c2s2;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class SleepWorkflowImpl implements SleepWorkflow {

    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public void exec(long myTime) {
        // 1. Sleep from when the workflow run starts
        // TODO - start the starter before you start the worker to show this
        //Workflow.sleep(myTime);

        // 2. Sleep from when the workflow run was scheduled
        // TODO - start the started before you start the worker to show this
//        sleepForTimeAfterWfScheduled(myTime);

        // 3. at exact time
        // TODO - change the starter code to do exact time
//        sleepForExactTime(myTime);
    }


    private void sleepForTimeAfterWfScheduled(long myTime) {
        // get the time since start of wf execution
        logger.info("current: " + showInEastern(Workflow.currentTimeMillis()) + " - run started: " + showInEastern(Workflow.getInfo().getRunStartedTimestampMillis()));
        long timeSinceStarted = Workflow.currentTimeMillis() - Workflow.getInfo().getRunStartedTimestampMillis();
        logger.info("****** time since started: " + timeSinceStarted);
        String since = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeSinceStarted),
                TimeUnit.MILLISECONDS.toSeconds(timeSinceStarted) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSinceStarted))
        );

        logger.info("Time since started: " + since);
        long sleepDuration = myTime - timeSinceStarted;
        if(sleepDuration > 0) {
            Workflow.sleep(sleepDuration);
        }
    }

    private void sleepForExactTime(long myTime) {
        // Note currentTimeMillis is in UTC time
        long timeToStart = myTime - Workflow.currentTimeMillis();

        String toStart = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(timeToStart),
                TimeUnit.MILLISECONDS.toSeconds(timeToStart) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeToStart)));
        System.out.println("Time to start: " + toStart);
        Workflow.sleep(timeToStart);
    }

    private String showInEastern(long timeInMillis) {
        Date timeDate = new Date(timeInMillis);
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        return formatter.format(timeDate);
    }
}
