# Java SDK Workshop - Chapter 4 - How-tos, Patterns, SDK Metrics

* [Section 1 - More "how tos" and "gotchas"](#Section-1)
* [Section 2 - Patterns](#Section-2)
* [Section 3 - SDK Metrics](#Section-3)


# Section 1

## Parallel activity invocation - error handling
Shows how to handle TimeoutFailure when running multiple child workflows in parallel

* Code in package [c4s1](c4s1)

## Manipulate threads inside workflow code (big "no-no") - PotentialDeadlockException
Shows how Thread.slee in workflow code can lead to PotentialDeadlockException
How to handle it?

* Code in package [c4s2](c4s2)

## Possible "issues" with signals and workflow history

* Code in package [c4s3](c4s3)

## Cron - setting timezone

* Code in package [c4s4](c4s4)

## More error handling fun

* Code in package [c4s5](c4s5)

# Section 2

## Patterns

1. Polling 
* Code in package [c4s5](c4s6)

2. Recovery / Fallback
SAGA can be used to undo/recover **Successfull** previous actions.
   But how to recover / fallback from failed activities?
   Answer use error handling and do what you need
   For sample we use a generic method that could be useful

* Code in package [c4s7](c4s7)

3. Executing one workflow at a time - pipeline / rate limit workflow executions
   "I want to start X number of workflows but run them one at a time."
   
Rate limiting:

* SDK WorkerOptions

   * maxWorkerActivitiesPerSecond (worker specific)
       * Maximum number of activities started per second by this worker. Default is 0 which means unlimited.
   * maxConcurrentActivityExecutionSize
       * Number of simultaneous poll requests on activity task queue. Worker-specific limit on parallel activities.
   * maxTaskQueueActivitiesPerSecond 
       * This is managed by the server and controls activities per second for the entire task queue across all the workers (global across all workers)
   * maxConcurrentWorkflowTaskExecutionSize 
    * Max concurrent workflow tasks (not workflows!) - helps to limit cpu/memory utilization by a worker
    
* We cannot rate limit concurrent workflow executions
Pattern:
  Have one running workflow, send signals to workflow for all workflows you want to create
  Have workflow handle signals one by one, run needed logic, or have workflow start up a chil workflow for each signal handled. 
  If child workflow then parent can be reusable workflow that can start childs for different types
  This workflow will have to call continueAsNew after X number of child workflow executions

* Code in package [c4s8](c4s8)

3. Busy loop with wait - anti-pattern
   * Can grow workflow history
   * Use workflow.await to wait for a condition inside a workflow (does not grow history)

* Code in package [c4s9](c4s9)

# Section 3

## SDK Metrics

* Code in package [c4s10](c4s10)

