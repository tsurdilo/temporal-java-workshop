# Workshop 1 - Getting Started

* [Section 1](#Section-1)
* [Section 2](#Section-2)
* Section 3 TBDs

# Section 1

## In this chapter we will learn about:

* [Temporal Java SDK](#Temporal-Java-SDK)
* [Using SDK in your applications](#Using-SDK)
* [Temporal Server](#Temporal-Server)
* [tctl (CLI)](#tctl)
* [Writing a simple Workflows](#Simple-Workflows)  
* [Invoking workflows - Client API](#Client-API)
* [Workers](#Workers)
* [Workflows, deeper look](#Workflows-Revisited)
* [Activities](#Activities)
* [Child Workflows](#Child-Workflows)
* [Testing Workflows and Activities](#Testing)

### Prerequisites
* Java Installed (version 11 or higher)
* [Apache Maven](https://maven.apache.org/download.cgi) (latest)
* [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) (latest)
* [Docker Compose](https://docs.docker.com/compose/install/) (latest)
    * [Video](https://www.youtube.com/watch?v=f6N3ZcWHygU)
* IDE (IntelliJ, VSCode, Eclipse, ...)

### Temporal Java SDK

#### Overview

* APIs (Client / Workflow / Testing)

1. [GitHub Repo](https://github.com/temporalio/sdk-java)
2. [Maven Central](https://search.maven.org/search?q=io.temporal)
3. [Javadoc.io](https://www.javadoc.io/doc/io.temporal/temporal-sdk/latest/index.html)
4. [Java Samples](https://github.com/temporalio/samples-java)
5. APIs
    * Client
    * Workflow
    * Testing   
  
### Using SDK

* Main Dependency

Maven:   
```
<dependency>
    <groupId>io.temporal</groupId>
    <artifactId>temporal-sdk</artifactId>
    <version>${version.temporal}</version>
</dependency>
```

Gradle:
```
implementation group: 'io.temporal', name: 'temporal-sdk', version: '1.4.0'
```

* Test dependencies

Maven:
```
<dependency>
    <groupId>io.temporal</groupId>
    <artifactId>temporal-testing</artifactId>
    <scope>test</scope>
    <version>${version.temporal}</version>
</dependency>

<dependency>
    <groupId>io.temporal</groupId>
    <artifactId>temporal-testing-junit4</artifactId>
    <scope>test</scope>
    <version>${version.temporal}</version>
</dependency>

<dependency>
    <groupId>io.temporal</groupId>
    <artifactId>temporal-testing-junit5</artifactId>
    <scope>test</scope>
    <version>${version.temporal}</version>
</dependency>
```

Gradle:
```
 testImplementation group: 'io.temporal', name: 'temporal-testing', version: '1.4.0'
 testImplementation group: 'io.temporal', name: 'temporal-testing-junit4', version: '1.4.0'
 testImplementation group: 'io.temporal', name: 'temporal-testing-junit5', version: '1.4.0'
```

* [Simple Maven Archetype](https://github.com/tsurdilo/temporal-simple-archetype)

### Temporal Server

* Run on Docker Compose

```
git clone git@github.com:temporalio/docker-compose.git
cd docker-compose
docker-compose -f docker-compose-cas-es.yml up
```   

* [Temporal Docker Compose GitHub Repo](https://github.com/temporalio/docker-compose)
* [Temporal Helm Charts Repo](https://github.com/temporalio/helm-charts)
* [Video](https://www.youtube.com/watch?v=f6N3ZcWHygU)

* Look at [Web UI](http://localhost:808/)
* Useful Docker commands for cleaning up **DEV** environment:

```
docker system prune -a
docker volume prune
```

<p align="center">
<img src="../../../../../../media/c1/c1-server.png" width="400"/>
</p>

### tctl

* Install CLI via HomeBrew:

```
brew install tctl
``` 

* [Other ways to install](https://docs.temporal.io/docs/system-tools/tctl/#run-the-cli)
* Check tctl version:

```
tctl -version
``` 

* Check cluster health:

```
tctl cluster health
```

### Simple Workflows

#### Overview

* Workflow Interface - @WorkflowInterface
* Workflow Type
* Workflow method -  @WorkflowMethod
* Signal methods - @SignalMethod
* Query methods - @QueryMethod
* Workflow Implementation
* Logger - Workflow.getLogger
* WorkflowInfo - Workflow.getInfo

### Client API

#### Overview

* gRPC communication to Temporal server
* Workflow client stub
* WorkflowStub - client side stub to a single workflow instance
* Different ways of starting workflow executions
* Core concepts: namespace, task queue, poller/worker
* Polyglot

* WorkflowServiceStubs
* WorkflowService
* Workflow stubs
* Workflow Options
  * Workflow Id
  * Task Queue
* Starting workflows
    * typed
        * sync / async
    * cron
    * untyped
    * async
        * Workflow.execute
        * Workflow.start
        * Async start then call blocking stub
        * From a different process
    * signalwithstart
* Dealing with exceptions (not extending TemporalFailure)
    * Workflows do not fail on unknown exceptions (by default)
    * Unknown exceptions are treated as a bug (can be fixed), not failing workflow
    * WorkflowImplementationOptions - if need to fail on unknown exceptions 
    * WorkflowFailedException indicates workflow failed
* Exceptions in Workflows:
    * If exception extends TemporalFailure
        * Workflow fails
        * Workflow is retried (from beginning) only if Workflow retry options are specified
    * Exception does not extend TemporalFailure
        * Is specified in WorkflowImplementationOptions
            * Workflow is retried (from beginning) only if Workflow retry options are specified
        * Is not specified in WorkflowImplementationOptions
            * Workflow replays (from history) up until workflow run timeout (waits for fix)
* Workflow retries
    * Each retry is a new workflow run (new runid)
* Workflow reset
    *  Currently supports only resetting to WorkflowTaskStarted
    

<p align="center">
<img src="../../../../../../media/c1/c1-clientapi.png" width="450"/>
</p>

* In Web UI
    * Discuss namespace
    * Discuss task queue / pollers
    * Discuss Workflow execution identity
    * Discuss long-running workflow
* Discuss polyglot aspect of Temporal (in terms of client)

### Workers

#### Overview

* WorkerFactory
* WorkerOptions
* Worker
    * TaskQueue
    * Register Workflow impls
    
### Workflows Revisited

#### Overview

* Workflow Statuses
    * Running
    * Completed
    * Failed
    * Cancelled
    * Terminated
    * ContinuedAsNew
    * TimedOut

* Retention Period
  
* Determinism

    
### Activities

#### Overview
* Invoking sync/async

### Child Workflows

#### Overview
* Invoking sync/async

### Testing

#### Overview
* Invoking sync/async