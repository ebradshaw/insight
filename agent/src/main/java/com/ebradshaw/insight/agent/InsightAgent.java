package com.ebradshaw.insight.agent;

import com.ebradshaw.insight.agent.instrumentation.FilteredAllocationInstrumeter;
import com.ebradshaw.insight.agent.instrumentation.springboot.SpringBootFilterInstrumenter;
import com.google.monitoring.runtime.instrumentation.AllocationRecorder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


public class InsightAgent {

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        initializeInstrumentation(inst);
        injectRuntimeClassloader();
        initializeRuntime(agentArgs);
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

        Class[] c = inst.getAllLoadedClasses();

        //Small hack here - Since we're using our own FilteredAllocationInstrumenter(), we can't use
        //AllocationInstrumenter.premain(...) which would usually set this.  So, we set it ourselves.
        Field field = AllocationRecorder.class.getDeclaredField("instrumentation");
        field.setAccessible(true);
        field.set(null, inst);
        field.setAccessible(false);

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
            System.err.println("Instrumenters were unable to retransform early loaded classes.");
        }
    }

    /**
     * We need to inject our custom runtim classloader into the application's classloader chain to properly handle classes
     * that depend on the servlet api, so we use reflection to insert it as the parent of the Extension Class Loader.
     * This seems to work well, but may be a tad risky!
     */
    private static void injectRuntimeClassloader() throws IOException, NoSuchFieldException, IllegalAccessException {
        ClassLoader extClassLoader = ClassLoader.getSystemClassLoader().getParent();
        ClassLoader runtimeClassLoader = new ServletTunnelingClassLoader(extractRuntime().toURI().toURL(), null);
        Field f = ClassLoader.class.getDeclaredField("parent");
        f.setAccessible(true);
        f.set(extClassLoader, runtimeClassLoader);
        f.setAccessible(false);
    }

    private static void initializeRuntime(String agentArgs) throws Exception {
        Class<?> runtimeClass = ClassLoader.getSystemClassLoader().loadClass("com.ebradshaw.insight.agent.InsightRuntime");
        runtimeClass.getDeclaredMethod("bootstrap", String.class).invoke(null, agentArgs);
    }

    private static File extractRuntime() throws IOException {
        File output = File.createTempFile("insight-runtime", "jar");
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResource("libs/insight-runtime.jar").openStream()) {
            Files.copy(inputStream, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return output;
    }

}
