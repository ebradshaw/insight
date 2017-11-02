package com.ebradshaw.insight.agent.sampler

import com.ebradshaw.insight.agent.TestEventRecorder
import com.ebradshaw.insight.agent.events.StringCreatedEvent
import com.ebradshaw.insight.agent.sampler.StringAllocationSampler
import com.google.common.eventbus.EventBus
import spock.lang.Specification

class StringAllocationSampleSpec extends Specification{

    StringAllocationSampler stringAllocationSampler
    EventBus eventBus

    def setup(){
        eventBus = new EventBus()
        stringAllocationSampler = new StringAllocationSampler(eventBus)
    }

    def "sampleAllocation dispatchs a StringCreated event when a single string is allocated"(){
        given:
        TestEventRecorder eventRecorder = new TestEventRecorder()
        eventBus.register(eventRecorder)
        when:
        stringAllocationSampler.sampleAllocation(-1, "java/lang/String", "Test", 100)
        then:
        eventRecorder.events.size() == 1
        (eventRecorder.events[0] as StringCreatedEvent).value == "Test"
    }

    def "no events are dispatched if an array of Strings is passed"(){
        given:
        TestEventRecorder eventRecorder = new TestEventRecorder()
        eventBus.register(eventRecorder)
        when:
        stringAllocationSampler.sampleAllocation(0, "java/lang/String", "Test", 100)
        then:
        eventRecorder.events.size() == 0
    }

    def "no events are dispatched if a non String object is passed"(){
        given:
        TestEventRecorder eventRecorder = new TestEventRecorder()
        eventBus.register(eventRecorder)
        when:
        stringAllocationSampler.sampleAllocation(-1, "java/lang/Object", "Test", 100)
        then:
        eventRecorder.events.size() == 0
    }

}
