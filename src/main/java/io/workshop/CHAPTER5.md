# Java SDK Workshop - Chapter 5 - More Patterns, TBD, ...

* [Section 1 - More Patterns](#Section-1)

# Section 1

## Bulk Request

In this scenario we have several workflows that want to execute the same activity (request to a 3rd party service).
This service allows bulk requests. 

For the sample we have 3 workflows, each send a signal to the bulk requester workflow.
The bulk requester workflow waits for 3 signals and then calls the bulk request activity.
After this the bulk requester workflow notifies all senders that the bulk request was completed 
via signal and sends the bulk request activity result (as signal payload).

* Code in package [c5s1](c5s1)