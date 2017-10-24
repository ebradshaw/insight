package com.ebradshaw.insight.agent.events;

public class ServletRequestCompleteEvent extends InstrumentedEvent{

    public ServletRequestCompleteEvent(long threadId) {
        super(threadId);
    }
}
