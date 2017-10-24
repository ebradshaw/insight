package com.ebradshaw.insight.agent.instrumentation;

import com.google.monitoring.runtime.instrumentation.AllocationInstrumenter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class FilteredAllocationInstrumeter implements ClassFileTransformer {

    private static final String[] blacklisted = new String[]{
            "com/ebradshaw/insight/agent",
            "java/lang/ThreadLocal",
            "ognl/"
    };

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[]
            classfileBuffer) throws IllegalClassFormatException {

        for (String prefix : blacklisted) {
            if (className.startsWith(prefix)) {
                return classfileBuffer;
            }
        }

        return AllocationInstrumenter.instrument(classfileBuffer, loader);
    }
}
