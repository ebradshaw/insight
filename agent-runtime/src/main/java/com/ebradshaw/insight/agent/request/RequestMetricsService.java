package com.ebradshaw.insight.agent.request;

import java.util.List;

public interface RequestMetricsService {

    RequestMetrics save(RequestMetrics entity);
    List<RequestMetrics> list();

}
