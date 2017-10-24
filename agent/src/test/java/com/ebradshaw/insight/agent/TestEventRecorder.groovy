package com.ebradshaw.insight.agent

import com.google.common.eventbus.Subscribe

class TestEventRecorder {

    public List<Object> events = new ArrayList<>()

    @Subscribe
    public void receiveEvent(Object event){
        this.events.add(event)
    }

}
