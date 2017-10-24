package com.ebradshaw.insight.agent.events;

public class StringCreatedEvent extends InstrumentedEvent {

    private final String value;

    public StringCreatedEvent(long threadId, String value) {
        super(threadId);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
