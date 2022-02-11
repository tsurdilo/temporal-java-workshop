package io.workshop.c1s1;

import com.google.common.base.CaseFormat;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class HistoryJsonUtils {
    private static final JsonPath EVENT_TYPE_PATH = JsonPath.compile("$.events.*.eventType");
    private static final JsonPath TASK_QUEUE_KIND_PATH =
            JsonPath.compile("$.events.*.*.taskQueue.kind");
    private static final String EVENT_TYPE_PREFIX = "EVENT_TYPE_";
    private static final String TASK_QUEUE_KIND_PREFIX = "TASK_QUEUE_KIND_";

    public static String protoJsonToHistoryFormatJson(String protoJson) {
        DocumentContext parsed = JsonPath.parse(protoJson);
        parsed.map(
                EVENT_TYPE_PATH,
                (currentValue, configuration) ->
                        enumProtoToHistory((String) currentValue, EVENT_TYPE_PREFIX));
        parsed.map(
                TASK_QUEUE_KIND_PATH,
                (currentValue, configuration) ->
                        enumProtoToHistory((String) currentValue, TASK_QUEUE_KIND_PREFIX));
        return parsed.jsonString();
    }

    public static String historyFormatJsonToProtoJson(String historyFormatJson) {
        DocumentContext parsed = JsonPath.parse(historyFormatJson);
        parsed.map(
                EVENT_TYPE_PATH,
                (currentValue, configuration) ->
                        enumHistoryToProto((String) currentValue, EVENT_TYPE_PREFIX));
        parsed.map(
                TASK_QUEUE_KIND_PATH,
                (currentValue, configuration) ->
                        enumHistoryToProto((String) currentValue, TASK_QUEUE_KIND_PREFIX));
        return parsed.jsonString();
    }

    private static String enumProtoToHistory(String protoEnumValue, String prefix) {
        if (!protoEnumValue.startsWith(prefix)) {
            throw new IllegalArgumentException("protoEnumValue should start with " + prefix + " prefix");
        }
        protoEnumValue = protoEnumValue.substring(prefix.length());
        return screamingCaseEventTypeToCamelCase(protoEnumValue);
    }

    private static String enumHistoryToProto(String historyEnumValue, String prefix) {
        return prefix + camelCaseToScreamingCase(historyEnumValue);
    }

    // https://github.com/temporalio/gogo-protobuf/commit/b38fb010909b8f81e2e600dc6f04925fc71d6a5e
    private static String camelCaseToScreamingCase(String camel) {
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.UPPER_UNDERSCORE).convert(camel);
    }

    // https://github.com/temporalio/gogo-protobuf/commit/b38fb010909b8f81e2e600dc6f04925fc71d6a5e
    private static String screamingCaseEventTypeToCamelCase(String screaming) {
        return CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(screaming);
    }
}
