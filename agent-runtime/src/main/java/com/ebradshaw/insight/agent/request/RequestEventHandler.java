package com.ebradshaw.insight.agent.request;

import com.ebradshaw.insight.agent.events.MemoryAllocatedEvent;
import com.ebradshaw.insight.agent.events.ServletRequestCompleteEvent;
import com.ebradshaw.insight.agent.events.ServletRequestErrorEvent;
import com.ebradshaw.insight.agent.events.ServletRequestStartedEvent;
import com.ebradshaw.insight.agent.events.StringCreatedEvent;
import com.google.common.eventbus.Subscribe;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestEventHandler {

    private final Map<Long, RequestAggregator> aggregatorMap;
    private final RequestMetricsService requestMetricsService;

    public RequestEventHandler(RequestMetricsService requestMetricsService){
        this.aggregatorMap = Collections.synchronizedMap(new HashMap<>());
        this.requestMetricsService = requestMetricsService;
    }

    private RequestAggregator createRequestAggregator(Long id, String uuid, String url, long creationTime){
        return aggregatorMap.put(id, new RequestAggregator(uuid, url, creationTime));
    }

    private RequestAggregator getRequestAggregator(Long id){
        return aggregatorMap.get(id);
    }

    private RequestAggregator removeRequestAggregator(Long id){
        return aggregatorMap.remove(id);
    }

    private boolean currentlyTracking(Long id){
        return aggregatorMap.containsKey(id);
    }

    @Subscribe
    public void requestStarted(ServletRequestStartedEvent event) {
        if(currentlyTracking(event.getThreadId())){
            throw new IllegalStateException("Request tracking has already been started for thread [" + event.getThreadId() + "]");
        }
        createRequestAggregator(event.getThreadId(), event.getUuid(), event.getUrl(), event.getCreationTime());
    }

    @Subscribe
    public void requestComplete(ServletRequestCompleteEvent event){
        validateCompletion(event.getThreadId());
        RequestMetrics metrics = removeRequestAggregator(event.getThreadId()).requestComplete(event.getCreationTime());
        requestMetricsService.save(metrics);
    }

    @Subscribe
    public void requestErrored(ServletRequestErrorEvent event){
        validateCompletion(event.getThreadId());
        RequestMetrics metrics = removeRequestAggregator(event.getThreadId()).requestError(event.getCreationTime());
        requestMetricsService.save(metrics);
    }

    private void validateCompletion(long threadId){
        if(!currentlyTracking(threadId)){
            throw new IllegalStateException("Request tracking has not been started for thread [" + threadId + "]");
        }
    }

    @Subscribe
    public void stringCreated(StringCreatedEvent event){
        if(currentlyTracking(event.getThreadId())){
            aggregatorMap.get(event.getThreadId()).recordStringCreation();
        }
    }

    @Subscribe
    public void memoryAllocated(MemoryAllocatedEvent event){
        if(currentlyTracking(event.getThreadId())){
            aggregatorMap.get(event.getThreadId()).recordMemoryAllocation(event.getAllocatedBytes());
        }
    }

}
