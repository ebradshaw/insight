package com.ebradshaw.insight.agent.events;

public class ServletRequestErrorEvent extends InstrumentedEvent {

    public ServletRequestErrorEvent(long threadId) {
        super(threadId);
    }
}
