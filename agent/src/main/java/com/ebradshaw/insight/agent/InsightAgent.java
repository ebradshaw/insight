package com.ebradshaw.insight.agent;

import com.ebradshaw.insight.agent.instrumentation.FilteredAllocationInstrumeter;
import com.ebradshaw.insight.agent.instrumentation.sampler.MemoryAllocationSampler;
import com.ebradshaw.insight.agent.instrumentation.sampler.StringAllocationSampler;
import com.ebradshaw.insight.agent.instrumentation.springboot.SpringBootFilterInstrumenter;
import com.ebradshaw.insight.agent.request.RequestEventHandler;
import com.ebradshaw.insight.agent.request.RequestMetricsService;
import com.ebradshaw.insight.agent.request.RequestMetricsServiceImpl;
import com.ebradshaw.insight.agent.server.InsightMetricsHandler;
import com.ebradshaw.insight.agent.server.InsightStaticHandler;
import com.ebradshaw.insight.agent.servlet.InsightFilter;
import com.google.common.eventbus.EventBus;
import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;


public class InsightAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        initializeInstrumentation(inst);
        new InsightAgent().bootstrap(agentArgs);
    }

    private void bootstrap(String agentArgs) throws IOException {
        InsightConfig config = InsightConfig.fromAgentArgs(agentArgs);
        EventBus eventBus = new EventBus();

        InsightFilter.setEventBus(eventBus);

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

    private void initializeLogging() {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream("insight-logging/log4j.properties")) {
            PropertyConfigurator.configure(inputStream);
        } catch (IOException ex) {
            System.err.println("Failed to initialize Log4j");
        }
    }

    private void initializeServer(int port, RequestMetricsService requestMetricsService) throws IOException {
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

    private static void initializeInstrumentation(Instrumentation inst) throws Exception {
        inst.addTransformer(new FilteredAllocationInstrumeter(), true);
        inst.addTransformer(new SpringBootFilterInstrumenter());

        try {
            Class.forName("sun.security.provider.PolicyFile");
            Class.forName("java.util.ResourceBundle");
            Class.forName("java.util.Date");
            Class.forName("com.google.common.util.concurrent.SettableFuture");
            Class.forName("java.util.logging.LogManager");
        } catch (Throwable t) {
            // NOP
        }

        //Small hack here - Since we're using our own FilteredAllocationInstrumenter(), we can't use
        //AllocationInstrumenter.premain(...) which would usually set this.  So, we set it ourselves.
        Method method = AllocationRecorder.class.getDeclaredMethod("setInstrumentation", Instrumentation.class);
        method.setAccessible(true);
        method.invoke(null, inst);
        method.setAccessible(false);

        // Get the set of already loaded classes that can be rewritten.
        Class<?>[] classes = inst.getAllLoadedClasses();
        ArrayList<Class<?>> classList = new ArrayList<Class<?>>();
        for (int i = 0; i < classes.length; i++) {
            if (inst.isModifiableClass(classes[i])) {
                classList.add(classes[i]);
            }
        }

        // Reload classes, if possible.
        Class<?>[] workaround = new Class<?>[classList.size()];
        try {
            inst.retransformClasses(classList.toArray(workaround));
        } catch (UnmodifiableClassException e) {
            System.err.println("Instrumeters were unable to retransform early loaded classes.");
        }


    }

}
