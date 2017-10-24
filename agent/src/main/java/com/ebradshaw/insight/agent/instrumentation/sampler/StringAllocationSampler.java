package com.ebradshaw.insight.agent.instrumentation.sampler;

import com.ebradshaw.insight.agent.events.Events;
import com.google.common.eventbus.EventBus;
import com.google.monitoring.runtime.instrumentation.Sampler;

public class StringAllocationSampler implements Sampler{

    private final EventBus eventBus;

    public StringAllocationSampler(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void sampleAllocation(int count, String desc, Object newObj, long size) {
        if(count == -1 && "java/lang/String".equals(desc)){
            eventBus.post(Events.stringCreated((String) newObj));
        }
    }
}
