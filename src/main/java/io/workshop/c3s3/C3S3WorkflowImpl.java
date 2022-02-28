package io.workshop.c3s3;

import io.temporal.workflow.DynamicQueryHandler;
import io.temporal.workflow.DynamicSignalHandler;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class C3S3WorkflowImpl implements C3S3Workflow {

    private String input1;
    private String input2;

    private Logger logger = Workflow.getLogger(this.getClass().getName());

    @Override
    public void exec() {

        // Register dynamic signal handler
        // can check signal name to do different logic
        Workflow.registerListener(
                (DynamicSignalHandler)
                        (signalName, encodedArgs) -> {
                            if(signalName.equals("setInput2")) {
                               logger.info("******* setting input2 via dynamic signal handler");
                               input2 = encodedArgs.get(0, String.class);
                            }
                        } );

        // Register dynamic query handler
        // can check query type to do different logic
        Workflow.registerListener(
                (DynamicQueryHandler)
                        (queryType, encodedArgs) -> {
                            if(queryType.equals("getInput2")) {
                                return input2;
                            }
                            return null;
                        });

        Workflow.await(() -> input1 != null && input2 != null);

    }

    @Override
    public void setInput(String input) {
        logger.info("******* setting input1 via defined signal handler");
        this.input1 = input;
    }

    @Override
    public String getInput() {
        return input1;
    }
}
