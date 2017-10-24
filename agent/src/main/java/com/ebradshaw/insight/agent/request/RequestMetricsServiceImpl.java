package com.ebradshaw.insight.agent.request;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RequestMetricsServiceImpl implements RequestMetricsService {

    private final static int HISTORY_SIZE = 1000;
    private final static Logger logger = LogManager.getLogger(RequestMetricsServiceImpl.class);
    private final List<RequestMetrics> requestMetrics = new ArrayList<>();

    @Override
    public RequestMetrics save(RequestMetrics entity) {
        logger.debug("Request Tracked: " + entity);

        if(requestMetrics.size() == HISTORY_SIZE) {
            requestMetrics.remove(0);
        }

        requestMetrics.add(entity);
        return entity;
    }

    @Override
    public List<RequestMetrics> list() {
        return requestMetrics;
    }
}
