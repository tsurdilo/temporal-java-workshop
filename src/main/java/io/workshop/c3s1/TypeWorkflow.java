package io.workshop.c3s1;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TypeWorkflow {
    @WorkflowMethod
//    @WorkflowMethod(name = "ABC")
    String execute(String input);

    @SignalMethod
    void inputSignal(String input);

    @SignalMethod
//    @SignalMethod(name = "input2")
    void inputSignal(String input, String input2);

    @QueryMethod
    String getInput();

    @QueryMethod
//    @QueryMethod(name = "getinput2")
    String getInput(String abc);
}
