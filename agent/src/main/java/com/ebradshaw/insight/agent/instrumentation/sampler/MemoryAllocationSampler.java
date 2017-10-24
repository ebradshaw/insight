package com.ebradshaw.insight.agent.instrumentation.sampler;

import com.ebradshaw.insight.agent.events.Events;
import com.google.common.eventbus.EventBus;
import com.google.monitoring.runtime.instrumentation.Sampler;

public class MemoryAllocationSampler implements Sampler{

    private final EventBus eventBus;

    public MemoryAllocationSampler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void sampleAllocation(int count, String desc, Object newObj, long size) {
        eventBus.post(Events.memoryAllocated(size));
    }
}
