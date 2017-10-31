package com.ebradshaw.insight.agent.events;

public class MemoryAllocatedEvent extends InstrumentedEvent{

    private final long allocatedBytes;

    public MemoryAllocatedEvent(long threadId, long allocatedBytes) {
        super(threadId);
        this.allocatedBytes = allocatedBytes;
    }

    public long getAllocatedBytes() {
        return allocatedBytes;
    }
}
