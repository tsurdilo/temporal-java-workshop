package io.workshop.c5s1;

import io.temporal.activity.ActivityInterface;

import java.util.List;

@ActivityInterface
public interface Activities {
    void signalBulkRequester(String requesterId);

    String callBulkService(List<String> input);

}
