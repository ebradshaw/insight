package com.ebradshaw.insight.agent.events;

public class ServletRequestStartedEvent extends InstrumentedEvent{
    private final String uuid;
    private final String url;

    public ServletRequestStartedEvent(long threadId, String uuid, String url) {
        super(threadId);
        this.uuid = uuid;
        this.url = url;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUrl() {
        return url;
    }
}
