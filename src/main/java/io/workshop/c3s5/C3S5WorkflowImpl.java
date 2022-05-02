package io.workshop.c3s5;

import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.time.Duration;

public class C3S5WorkflowImpl implements C3S5Workflow {
    private int count;
    private boolean exit;
    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public int exec(int count) {
        this.count = count;

        Workflow.await(Duration.ofSeconds(2), () -> exit == true);

        if(!exit) {
            logger.info("********* CONTINUING AS NEW AT COUNT: " + this.count);
            // continueAsNew via typed stub
//           C3S5Workflow continueAsNewStub = Workflow.newContinueAsNewStub(C3S5Workflow.class);
//           continueAsNewStub.exec(this.count);
            // you can also use Workflow.continueAsNew
            Workflow.continueAsNew(this.count);
        }

        return this.count;
    }

    @Override
    public void addSignal() {
        this.count = this.count + 1;
    }

    @Override
    public void exit() {
        this.exit = true;
    }

    @Override
    public int getCount() {
        return this.count;
    }
}
