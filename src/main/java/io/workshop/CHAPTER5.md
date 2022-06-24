# Java SDK Workshop - Chapter 5 - More Patterns, Cancellation, Interceptors, Workflow Execution+History and more

* [Section 1 - More Patterns / Cancellation/ Signals / Interceptors](#Section-1)
* [Section 2 - Workflow Execution + History](#Section-2)

# Section 1

## Bulk Request

In this scenario we have several workflows that want to execute the same activity (request to a 3rd party service).
This service allows bulk requests. 

For the sample we have 3 workflows, each send a signal to the bulk requester workflow.
The bulk requester workflow waits for 3 signals and then calls the bulk request activity.
After this the bulk requester workflow notifies all senders that the bulk request was completed 
via signal and sends the bulk request activity result (as signal payload).

* Code in package [c5s1](c5s1)

## Cascade wait for cancellation

For this sample we have a workflow that starts child (async) and receives a signal to cancel the child.
The child executes an activity which also needs to be cancelled when child is cancelled.
Show how child waits for activity to cancel and parent waits for child to cancel.

* Code in package [c5s1](c5s2)

## Handle multiple distinct signals in order

* Code in package [c5s1](c5s3)

## Break workflow determinism + reset

* Code in package [c5s1](c5s4)

# Section 2

## Workflow execution + history
