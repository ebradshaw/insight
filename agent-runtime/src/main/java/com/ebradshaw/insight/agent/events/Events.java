package com.ebradshaw.insight.agent.events;

import com.ebradshaw.insight.agent.request.RequestMetrics;

public class Events {

    private static long currentThreadId() {
        return Thread.currentThread().getId();
    }

    public static MemoryAllocatedEvent memoryAllocated(long size) {
        return new MemoryAllocatedEvent(currentThreadId(), size);
    }

    public static ServletRequestStartedEvent servletRequestStarted(String uuid, String url) {
        return new ServletRequestStartedEvent(currentThreadId(), uuid, url);
    }

    public static ServletRequestCompleteEvent servletRequestComplete() {
        return new ServletRequestCompleteEvent(currentThreadId());
    }

    public static ServletRequestErrorEvent servletRequestError() {
        return new ServletRequestErrorEvent(currentThreadId());
    }

    public static StringCreatedEvent stringCreated(String value) {
        return new StringCreatedEvent(currentThreadId(), value);
    }

    public static RequestMetricsEvent requestMetricsEvent(RequestMetrics requestMetrics){
        return new RequestMetricsEvent(requestMetrics);
    }

}
