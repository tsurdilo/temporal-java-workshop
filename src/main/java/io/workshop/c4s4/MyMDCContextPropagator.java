package io.workshop.c4s4;

import io.temporal.api.common.v1.Payload;
import io.temporal.common.context.ContextPropagator;
import io.temporal.common.converter.DataConverter;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;

public class MyMDCContextPropagator implements ContextPropagator {
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public Map<String, Payload> serializeContext(Object context) {
        String testKey = (String) context;
        if (testKey != null) {
            return Collections.singletonMap(
                    "test", DataConverter.getDefaultInstance().toPayload(testKey).get());
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Object deserializeContext(Map<String, Payload> context) {
        if (context.containsKey("test")) {
            return DataConverter.getDefaultInstance()
                    .fromPayload(context.get("test"), String.class, String.class);

        } else {
            return null;
        }
    }

    @Override
    public Object getCurrentContext() {
        return MDC.get("test");
    }

    @Override
    public void setCurrentContext(Object context) {
        MDC.put("test", String.valueOf(context));
    }

}
