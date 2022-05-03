# Java SDK Workshop - Chapter 4 - How-tos, Patterns, SDK Metrics

* [Section 1 - More "how tos" and "gotchas"](#Section-1)
* [Section 2 - Patterns](#Section-2)


# Section 1

## Parallel activity invocation - error handling
Shows how to handle TimeoutFailure when running multiple activities in parallel

* Code in package [c4s1](c4s1)

## Manipulate threads inside workflow code (big "no-no") - PotentialDeadlockException
Shows how to handle TimeoutFailure when running multiple activities in parallel

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

* Code in package [c4s7](c4s7)
   

2. Scale out - "Should I create workflow per user account. We have half a million accounts currently"
Yes it's ok to create workflow per X. Temporal was tested 
   to hundreds of millions open workfows. 

3. Updating initial workflow data input
Depends on use case. In some cases you can use signal to update workflow state.
   You can also use reset feature to roll back workflow to some previous point of execution
    
4. Executing one workflow at a time - pipeline / rate limit workflow executions
"I want to start X number of workflows but run them one at a time."