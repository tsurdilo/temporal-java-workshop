/*
 *  Copyright (c) 2020 Temporal Technologies, Inc. All Rights Reserved
 *
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package io.workshop.c4s10;

import com.sun.net.httpserver.HttpServer;
import com.uber.m3.tally.RootScopeBuilder;
import com.uber.m3.tally.Scope;
import com.uber.m3.util.ImmutableMap;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.common.reporter.MicrometerClientStatsReporter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.workshop.c4s10.workflow.FailWorkflow;
import io.workshop.c4s10.workflow.FillWorkflow;
import io.workshop.c4s10.workflow.MetricsWorkflow;

public class MetricsStarter {
  public static void main(String[] args) {
    System.out.println("Starter metrics are available at http://localhost:8078/metrics");

    // Set up prometheus registry and stats reported
    PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    // Set up a new scope, report every 1 second
    Scope scope =
        new RootScopeBuilder()
            // shows how to set custom tags
            .tags(
                ImmutableMap.of(
                    "starterCustomTag1",
                    "starterCustomTag1Value",
                    "starterCustomTag2",
                    "starterCustomTag2Value"))
            .reporter(new MicrometerClientStatsReporter(registry))
            .reportEvery(com.uber.m3.util.Duration.ofSeconds(1));
    // Start the prometheus scrape endpoint for starter metrics
    HttpServer scrapeEndpoint = MetricsUtils.startPrometheusScrapeEndpoint(registry, 8078);
    // Stopping the starter will stop the http server that exposes the
    // scrape endpoint.
    Runtime.getRuntime().addShutdownHook(new Thread(() -> scrapeEndpoint.stop(1)));

    // Add metrics scope to workflow service stub options
    WorkflowServiceStubsOptions stubOptions =
        WorkflowServiceStubsOptions.newBuilder().setMetricsScope(scope).build();

    WorkflowServiceStubs service = WorkflowServiceStubs.newInstance(stubOptions);
    WorkflowClient client = WorkflowClient.newInstance(service);

    WorkflowOptions workflowOptions =
        WorkflowOptions.newBuilder()
            .setWorkflowId("metricsWorkflow")
            .setTaskQueue(MetricsWorker.DEFAULT_TASK_QUEUE_NAME)
            .build();
    MetricsWorkflow workflow = client.newWorkflowStub(MetricsWorkflow.class, workflowOptions);

    String result = workflow.exec("hello metrics");


    // start another exec and cancel it
    MetricsWorkflow workflow2 = client.newWorkflowStub(MetricsWorkflow.class, WorkflowOptions.newBuilder()
            .setWorkflowId("metricsWorkflowFail")
            .setTaskQueue(MetricsWorker.DEFAULT_TASK_QUEUE_NAME)
            .build());

    WorkflowClient.start(workflow2::exec, "hello metrics");
    WorkflowStub untyped = WorkflowStub.fromTyped(workflow2);
    untyped.cancel();


    // start a bunch of "filler" and "fail" workflows
    for(int i=0;i<40;i++) {
      FillWorkflow fillWorkflow = client.newWorkflowStub(FillWorkflow.class, WorkflowOptions.newBuilder()
              .setWorkflowId("Fill-" + i)
              .setTaskQueue(MetricsWorker.DEFAULT_TASK_QUEUE_NAME)
              .build());
      WorkflowClient.start(fillWorkflow::exec, "Filler-" + 1);
      sleep(500);
    }

    // start a bunch of "fail" workflows

    for(int i=0;i<40;i++) {
      FailWorkflow fillWorkflow = client.newWorkflowStub(FailWorkflow.class, WorkflowOptions.newBuilder()
              .setWorkflowId("Fail-" + i)
              .setTaskQueue(MetricsWorker.DEFAULT_TASK_QUEUE_NAME)
              .build());
      WorkflowClient.start(fillWorkflow::exec, "Fail-" + 1);
      sleep(500);
    }

    // We don't shut down the process here so metrics can be viewed.
  }

  private static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
