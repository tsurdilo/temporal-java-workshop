# Java SDK Workshop - Chapter 2 - Let's keep going...

* [Section 1 - Client APIs continued](#Section-1)
* [Section 2 - Versioning](#Section-2)
* [Section 3 - Error Handling](#Section-3)
* [Section 4 - Cancellation](#Section-4)
* [Section 4 - Compensation](#Section-5)

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

## Versioning 

# Section 3

## Error Handling

# Section 4

## Cancellation

# Section 5

## Compensation 