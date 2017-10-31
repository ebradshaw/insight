package com.ebradshaw.insight.agent.request

import com.ebradshaw.insight.agent.events.Events
import spock.lang.Specification

class RequestEventHandlerSpec extends Specification {

    RequestEventHandler requestEventHandler
    RequestMetricsService requestMetricsService

    def setup() {
        requestMetricsService = Mock(RequestMetricsService)
        requestEventHandler = new RequestEventHandler(requestMetricsService)
    }

    def "metrics logged during a request are tallied into request metrics"() {
        when:
        requestEventHandler.requestStarted(Events.servletRequestStarted("uuid", "url"))
        requestEventHandler.stringCreated(Events.stringCreated(""))
        requestEventHandler.memoryAllocated(Events.memoryAllocated(100))
        requestEventHandler.requestComplete(Events.servletRequestComplete())
        then:
        1 * requestMetricsService.save({ RequestMetrics it -> it.stringsCreated == 1 && it.memoryAllocated == 100 })
    }

    def "metrics logged before a request are not tallied"() {
        when:
        requestEventHandler.stringCreated(Events.stringCreated(""))
        requestEventHandler.memoryAllocated(Events.memoryAllocated(100))
        requestEventHandler.requestStarted(Events.servletRequestStarted("uuid", "url"))
        requestEventHandler.requestComplete(Events.servletRequestComplete())
        then:
        1 * requestMetricsService.save({ RequestMetrics it -> it.stringsCreated == 0 && it.memoryAllocated == 0 })
    }

    def "request events terminated with an error result in Status.ERROR"() {
        when:
        requestEventHandler.requestStarted(Events.servletRequestStarted("uuid", "url"))
        requestEventHandler.requestErrored(Events.servletRequestError())
        then:
        1 * requestMetricsService.save({ RequestMetrics it -> it.status.equals(RequestMetrics.Status.ERROR) })
    }

    def "multiple request started calls that go uncompleted result in an IllegalStateException"() {
        when:
        requestEventHandler.requestStarted(Events.servletRequestStarted("uuid", "url"))
        requestEventHandler.requestStarted(Events.servletRequestStarted("uuid", "url"))
        then:
        thrown IllegalStateException
    }

    def "calling requestComplete without calling request started results in an IllegalStateException"() {
        when:
        requestEventHandler.requestComplete(Events.servletRequestComplete())
        then:
        thrown IllegalStateException
    }

    def "calling requestErrored without calling request started results in an IllegalStateException"() {
        when:
        requestEventHandler.requestErrored(Events.servletRequestError())
        then:
        thrown IllegalStateException
    }

    def "events logged concurrently different threads are tracked separately"() {
        when:
        def task = {
                requestEventHandler.requestStarted(Events.servletRequestStarted("uuid", "url"))
                requestEventHandler.stringCreated(Events.stringCreated(""))
                requestEventHandler.memoryAllocated(Events.memoryAllocated(100))
                requestEventHandler.requestComplete(Events.servletRequestComplete()) }
        List<Thread> threads = (1..1000).collect { new Thread(task) }
        threads.each { it.start() }
        threads.each { it.join() }

        then:
        1000 * requestMetricsService.save({ RequestMetrics it -> it.stringsCreated == 1 && it.memoryAllocated == 100 })
    }

}
