package io.workshop.c4s8.oneattimeworkflow;

import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.ChildWorkflowStub;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RatedWorkflowImpl implements RatedWorkflow {
    private List<ToInvoke> toInvokeList;
    private boolean exit = false;
    private  int maxCalls = 8; // set to 8 to show we can continueAsNew while doing this...
    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public void oneAtTime(List<ToInvoke> invokeQueue) {
        if(invokeQueue == null) {
            this.toInvokeList = new ArrayList<>(10);
        } else {
            this.toInvokeList = invokeQueue;
        }
        int count = 0;
        while (true) {
            // Block until we get signal
            Workflow.await(() -> !toInvokeList.isEmpty() || exit);
            if (toInvokeList.isEmpty() && exit) {
                // no messages in queue and exit signal was sent just end
                return;
            }
            ToInvoke toInvoke = toInvokeList.remove(0);
            // execute child workflow sync
            ChildWorkflowStub childWorkflowStub =
                    Workflow.newUntypedChildWorkflowStub(toInvoke.getWorkflowType(),
                            ChildWorkflowOptions.newBuilder()
                                    .setWorkflowId(toInvoke.getWorkflowId())
                                    .build());

            // note omitted error handling...
            childWorkflowStub.execute(Void.class, toInvoke.getInput());
            count++;
            // we have to call continueAsNew after X number of child workflow execs
            if(count == maxCalls) {
                // irl it would be more than after 8..just for demo
                logger.info("Workflow calling ContinueAsNew");
                Workflow.continueAsNew(this.toInvokeList);
            }
        }
    }

    @Override
    public void toInvoke(ToInvoke toInvoke) {
        this.toInvokeList.add(toInvoke);
    }

    @Override
    public void exit() {
        this.exit = true;
    }
}
