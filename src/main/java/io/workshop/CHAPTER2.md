# Java SDK Workshop - Chapter 2 - Let's keep going...

* [Section 1 - Client APIs continued](#Section-1)
* [Section 1 - Sleep Duration](#Section-2)
* [Section 2 - Versioning](#Section-3)
* [Section 3 - Error Handling](#Section-4)
* [Section 4 - Cancellation](#Section-5)
* [Section 4 - Compensation](#Section-6)

# Section 1

## Client APIs Continued

* List Opened Workflows - does NOT require Elasticsearch
    * tctl wf list --open
* List Closed Workflows
    * tctl wf list --status completed
* Describe wf execution
    * tctl wf desc -w <wfid> -r <wfrunid>
* Print wf stack trace
    * tctl wf stack -w <wfid> -r <wfrunid>
* Get workflow cron schedule
* Get Activities with retry count over X
* List Namespaces
  * tctl namespace list
* Create Namespace
  * tctl --ns mynamespace n re
* Check cluster (FrontEnd Service health)
  * tctl cluster health
* Describe task queue
  * tctl tq desc -tq <task_queue_name>

# Section 2

## Sleep Duration

<p align="center">
<img src="../../../../../media/c2/workflow-sleep-website.png" width="450"/>
</p>

In this section we are going to take a look at "simple" Workflow.sleep
and will learn how to get the most out of it in cases of failures, or workers being down

# Section 3

## Versioning 

1. Show how we can fix activity code to unblock workflow execution
2. Show version sample

** Current limitations for Java SDK:
  * https://github.com/temporalio/sdk-java/issues/587 - TemporalChangeVersion search attribute not updated in Java SDK
  * To Show search attributes: tctl cluster get-search-attributes
  * tctl workflow count --query='TemporalChangeVersion="<change_id>-<version>" AND ExecutionStatus=1'
  * tctl workflow list --query='TemporalChangeVersion="<change_id>-<version>" AND ExecutionStatus=1'

# Section 4

## Error Handling

* Handling workflow timeout on client
  *


By default:
* Workflows don't have retry options - not retried when failed or timed out
* Workflow is retried only if you specify its retry options


* Workflow code throws exception that extends TemporalFailure


* Any exception that doesn’t extend TemporalFailure is converted to ApplicationFailure when thrown from a workflow or an activity.
* Calls to activities always throw ActivityFailure with an exception that caused the failure as a cause. 
So a NullPointerException thrown from an activity is going to be delivered to the workflow 
(after exhausting all retries according to the retry options) as an 
ActivityFailure that has an ApplicationFailure with a type equal to `java.lang.NullPointerException" as a cause.


An activity invocation always throws ActivityFailure with an original failure as a cause.
A child workfow invocation always throws ChildWorkflowFailure with an original failure as a cause.
A synchronous workflow invocation always returns WorkflowException which will contain the workflow failure as a cause.


Workflow error handling
By default, workflows by default don’t have retry options and are not retried when failed or timed out.

Case 1 : thrown Exception extends Temporal Failure
Expected Result : Workflow fails and closes with no retries.

Workflow fails and is retried (by executing from the beginning) only if retry options are specified.

Case 2 : thrown Exception does not extend TemporalFailure and is specified in WorkflowImplementationOptions.setFailWorkflowExceptionTypes
Expected Result : Workflow retries from scratch, ignoring EventHistory and re-executing any activities as well as the special Workflow.random*() methods.

The same as Case 1. Workflow fails and is retried (by executing from the beginning) only if retry options are specified.

Q : In the event of a full retry, is the pervious EventHistory retained anywhere?

The workflow retry is modeled as a new workflow run. So it gets a new runId. The previous run data including its event history is still available up to the retention period.

Case 3 : thrown Exception does not extend TemporalFailure and is not specified in WorkflowImplementationOptions.setFailWorkflowExceptionTypes (like an NPE)
Expected Result : Workflow replays indefinitely or until hitting a timeout, using the Event History, waiting for a fix.



# Section 5

## Cancellation

# Section 6

## Compensation 