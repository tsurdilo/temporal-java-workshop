# Java SDK Workshop - Chapter 3 - Workflow, Activity Types, ContinueAsNew, Client error handling and more

* [Section 1 - Types](#Section-1)
* [Section 2 - Typed vs untyped stubs](#Section-2)
* [Section 3 - Dynamic signals and queries with typed stubs](#Section-3)
* [Section 3 - Client errors and setting up SSL/mTLS](#Section-4)
* [Section 5 -ContinueAsNew and signals](#Section-5)

# Section 1

## Workflow, activity types, signal and query names

* Types represent a unique identification of workflow and activity definitions with a single worker. 
* Signal and Query names tie signals/queries with signal/query handlers.
* Unique workflow/activity types can be registered with single worker.
* Unique signal/query names are allowed in signal workflow definition.
* Workflow and activity types play important role in invoking them. 

* Code in package [c3s1](c3s1)

# Section 2

## Typed and Untyped stubs

* Workflow stubs
* Activity stubs
* Child workflow stubs
  
* Code in package [c3s2](c3s2)

# Section 3

## Dynamic signals and queries with typed stubs

Shows how to dynamically add signals and queries not exposed via workflow interface

* Code in package [c3s3](c3s3)

# Section 4

## Client errors and setting up SSL/mTLS

* Catching client errors 
* Setting up SSL (mTLS)

* For adding mTLS support on server see samples repo [here](https://github.com/temporalio/samples-server).

* Code in package [c3s4](c3s4)

# Section 5

## ContinueAsNew and signals

Demo on sending signals while workflow is doing continueAsNew

* Code in package [c3s5](c3s5)
