package com.ebradshaw.insight.agent.server;

import com.ebradshaw.insight.agent.request.RequestMetrics;
import com.ebradshaw.insight.agent.request.RequestMetricsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class InsightMetricsHandler implements HttpHandler {

    private final RequestMetricsService requestMetricsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InsightMetricsHandler(RequestMetricsService requestMetricsService) {
        this.requestMetricsService = requestMetricsService;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        List<RequestMetrics> requestMetrics = requestMetricsService.list();
        String output = objectMapper.writeValueAsString(requestMetrics);
        t.getResponseHeaders().set("Content-Type", "application/json");
        t.sendResponseHeaders(200, output.length());
        OutputStream os = t.getResponseBody();
        os.write(output.getBytes());
        os.close();
    }
}
