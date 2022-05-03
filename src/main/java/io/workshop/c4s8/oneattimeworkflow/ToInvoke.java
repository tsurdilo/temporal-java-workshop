package io.workshop.c4s8.oneattimeworkflow;

public class ToInvoke {
    private String workflowType;
    private String workflowId;
    private String input;

    public ToInvoke() {
    }

    public ToInvoke(String workflowType, String workflowId, String input) {
        this.workflowType = workflowType;
        this.workflowId = workflowId;
        this.input = input;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
