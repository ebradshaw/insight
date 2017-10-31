package com.ebradshaw.insight.agent;

import com.ebradshaw.insight.agent.request.RequestEventHandler;
import com.ebradshaw.insight.agent.request.RequestMetricsService;
import com.ebradshaw.insight.agent.request.RequestMetricsServiceImpl;
import com.ebradshaw.insight.agent.sampler.MemoryAllocationSampler;
import com.ebradshaw.insight.agent.sampler.StringAllocationSampler;
import com.ebradshaw.insight.agent.server.InsightMetricsHandler;
import com.ebradshaw.insight.agent.server.InsightStaticHandler;
import com.google.common.eventbus.EventBus;
import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class InsightRuntime {

    public static EventBus eventBus;

    public static void bootstrap(String agentArgs) throws IOException {
        InsightConfig config = InsightConfig.fromAgentArgs(agentArgs);

        eventBus = new EventBus();

        //Register samples to handle string/memory allocations and dispatch them to the event bus
        AllocationRecorder.addSampler(new StringAllocationSampler(eventBus));
        AllocationRecorder.addSampler(new MemoryAllocationSampler(eventBus));

        //Initialize metrics service and create event handler to allocate metrics for requests
        RequestMetricsService requestMetricsService = new RequestMetricsServiceImpl();
        RequestEventHandler requestEventHandler = new RequestEventHandler(requestMetricsService);
        eventBus.register(requestEventHandler);

        //Initialize http server to serve static web application and provide metrics endpoint
        initializeServer(config.getPort(), requestMetricsService);

        //Initialize log4j
        initializeLogging();
    }

    private static void initializeLogging() {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("insight-logging/log4j.properties")) {
            PropertyConfigurator.configure(inputStream);
        } catch (IOException ex) {
            System.err.println("Failed to initialize Log4j");
        }
    }

    private static void initializeServer(int port, RequestMetricsService requestMetricsService) throws IOException {
        HttpHandler metricsHandler = new InsightMetricsHandler(requestMetricsService);
        HttpHandler staticContentHandler = new InsightStaticHandler();
        final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/metrics", metricsHandler);
        server.createContext("/", staticContentHandler);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
        }));
    }

}
