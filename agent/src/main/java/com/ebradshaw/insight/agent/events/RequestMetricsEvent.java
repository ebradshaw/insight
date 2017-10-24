package com.ebradshaw.insight.agent.events;

import com.ebradshaw.insight.agent.request.RequestMetrics;

public class RequestMetricsEvent {

    private final RequestMetrics requestMetrics;

    public RequestMetricsEvent(RequestMetrics requestMetrics) {
        this.requestMetrics = requestMetrics;
    }

    public RequestMetrics getRequestMetrics() {
        return requestMetrics;
    }
}
