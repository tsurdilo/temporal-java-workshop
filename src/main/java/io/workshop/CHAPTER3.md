# Java SDK Workshop - Chapter 3 - TBD

* [Section 1 - Types](#Section-1)
* [Section 2 - Typed vs untyped stubs](#Section-2)
* [Section 3 - Client error handling](#Section-3)
* [Section 4 - Hidden signals and queries](#Section-4)


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

## Client error handling 

Catching client errors 

* Code in package [c3s3](c3s3)

# Section 4

## Hidden signals and queries

Shows how to dynamically add signals and queries not exposed via workflow interface

* Code in package [c3s4](c3s4)