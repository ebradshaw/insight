package com.ebradshaw.insight.agent.com.ebradshaw.insight.agent.instrumentation.sampler

import com.ebradshaw.insight.agent.TestEventRecorder
import com.ebradshaw.insight.agent.events.MemoryAllocatedEvent
import com.ebradshaw.insight.agent.instrumentation.sampler.MemoryAllocationSampler
import com.google.common.eventbus.EventBus
import spock.lang.Specification

class MemoryAllocationSamplerSpec extends Specification {

    MemoryAllocationSampler memoryAllocationSampler
    EventBus eventBus

    def setup(){
        eventBus = new EventBus()
        memoryAllocationSampler = new MemoryAllocationSampler(eventBus)
    }

    def "sampleAllocation dispatchs a MemoryAllocated event any object is allocated"(){
        given:
        TestEventRecorder eventRecorder = new TestEventRecorder()
        eventBus.register(eventRecorder)
        when:
        memoryAllocationSampler.sampleAllocation(-1, "java/lang/Object", new Object(), 100)
        then:
        eventRecorder.events.size() == 1
        (eventRecorder.events[0] as MemoryAllocatedEvent).allocatedBytes == 100
    }

}
