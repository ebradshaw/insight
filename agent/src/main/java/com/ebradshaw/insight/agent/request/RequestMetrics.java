package com.ebradshaw.insight.agent.request;

public class RequestMetrics {

    public enum Status {
        COMPLETE,
        ERROR
    }

    private final String uuid;
    private final String url;
    private final long requestTime;
    private final long stringsCreated;
    private final long memoryAllocated;
    private final Status status;

    public RequestMetrics(String uuid, String url, long requestTime, long stringsCreated, long memoryAllocated, Status status) {
        this.uuid = uuid;
        this.url = url;
        this.requestTime = requestTime;
        this.stringsCreated = stringsCreated;
        this.memoryAllocated = memoryAllocated;
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public long getStringsCreated() {
        return stringsCreated;
    }

    public long getMemoryAllocated() {
        return memoryAllocated;
    }

    public Status getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "RequestMetrics{" +
                "uuid='" + uuid + '\'' +
                ", url='" + url + '\'' +
                ", requestTime=" + requestTime +
                ", stringsCreated=" + stringsCreated +
                ", memoryAllocated=" + memoryAllocated +
                ", status=" + status +
                '}';
    }
}
