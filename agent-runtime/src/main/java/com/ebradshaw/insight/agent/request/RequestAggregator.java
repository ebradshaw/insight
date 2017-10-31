package com.ebradshaw.insight.agent.request;

class RequestAggregator {

    private final String uuid;
    private final String url;
    private final long startTime;

    private long stringsCreated = 0;
    private long memoryAllocated = 0;

    RequestAggregator(String uuid, String url, long startTime) {
        this.uuid = uuid;
        this.url = url;
        this.startTime = startTime;
    }

    void recordStringCreation(){
        stringsCreated++;
    }

    void recordMemoryAllocation(long size){
        memoryAllocated += size;
    }

    RequestMetrics requestComplete(long eventTime){
        return new RequestMetrics(uuid,
                url,
                eventTime - startTime,
                stringsCreated,
                memoryAllocated,
                RequestMetrics.Status.COMPLETE);
    }

    RequestMetrics requestError(long eventTime){
        return new RequestMetrics(uuid,
                url,
                eventTime - startTime,
                stringsCreated,
                memoryAllocated,
                RequestMetrics.Status.ERROR);
    }

}
