package com.ebradshaw.insight.agent.instrumentation.springboot

public class TestAnnotationContext {
    public static Class[] registered
    public static boolean refreshCalled

    public final void register(Class<?>... annotatedClasses) {
        refreshCalled = false
        registered = annotatedClasses
    }

    protected void prepareRefresh() {
        refreshCalled = true
    }

}
