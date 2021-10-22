package io.workshop.s2;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ContentLengthInfo {
    private Map<String, Integer> websiteMap = new HashMap<>();
    public ContentLengthInfo() {
    }

    public Map<String, Integer> getWebsiteMap() {
        return websiteMap;
    }

    public void setWebsiteMap(Map<String, Integer> websiteMap) {
        this.websiteMap = websiteMap;
    }

    public void add(String url, int count) {
        websiteMap.put(url, count);
    }

    public void add(ContentLengthInfo info) {
        websiteMap.putAll(info.getWebsiteMap());
    }

    @Override
    public String toString() {
        String result = websiteMap.entrySet()
                .stream()
                .map(entry -> entry.getKey() + " --> " + entry.getValue())
                .collect(Collectors.joining(", "));
        return result;
    }
}
