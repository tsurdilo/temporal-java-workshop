package io.workshop.c5s6;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.converter.JacksonJsonPayloadConverter;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

public class Starter {

    // TODO dont forget to show pom
//    private static final WorkflowClientOptions clientOptions = WorkflowClientOptions.newBuilder()
//            .setDataConverter(
//                    DefaultDataConverter.newDefaultInstance()
//                            .withPayloadConverterOverrides(getParameterNameModuleJacksonJsonPayloadConverter()))
//            .build();
//
    public static final WorkflowServiceStubs service = WorkflowServiceStubs.newInstance();
    public static final WorkflowClient client = WorkflowClient.newInstance(service);
//    public static final WorkflowClient client = WorkflowClient.newInstance(service, clientOptions);
    public static final String taskQueue = "c5s6TaskQueue";

    public static void main(String[] args) {
        createWorker();

        MyModel model = new MyModel("myName", 40);

        ModelWorkflow workflow = client.newWorkflowStub(ModelWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setWorkflowId("modelWF")
                        .setTaskQueue(taskQueue)
                        .build());

        workflow.exec(model);
    }

    private static void createWorker() {
        WorkerFactory workerFactory = WorkerFactory.newInstance(client);
        Worker worker = workerFactory.newWorker(taskQueue);

        worker.registerWorkflowImplementationTypes(ModelWorkflowImpl.class);
        workerFactory.start();
    }

    private static JacksonJsonPayloadConverter getParameterNameModuleJacksonJsonPayloadConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));

        return new JacksonJsonPayloadConverter(objectMapper);
    }
}
