package com.ebradshaw.insight.agent.events;

public class InstrumentedEvent {

    private final long threadId;
    private final long creationTime;

    public InstrumentedEvent(long threadId) {
        this.threadId = threadId;
        this.creationTime = System.currentTimeMillis();
    }

    public long getThreadId() {
        return threadId;
    }

    public long getCreationTime() {
        return creationTime;
    }
}
