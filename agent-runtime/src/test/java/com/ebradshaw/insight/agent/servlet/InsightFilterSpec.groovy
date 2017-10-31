//package com.ebradshaw.insight.agent.servlet
//
//import com.ebradshaw.insight.agent.TestEventRecorder
//import com.ebradshaw.insight.agent.events.ServletRequestCompleteEvent
//import com.ebradshaw.insight.agent.events.ServletRequestErrorEvent
//import com.ebradshaw.insight.agent.events.ServletRequestStartedEvent
//import com.google.common.eventbus.EventBus
//import spock.lang.Specification
//
//import javax.servlet.FilterChain
//import javax.servlet.ServletRequest
//import javax.servlet.ServletResponse
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//class InsightFilterSpec extends Specification {
//
//    InsightFilter filter
//    FilterChain filterChain
//    EventBus eventBus
//
//    def setup(){
//        eventBus = new EventBus()
//        InsightFilter.setEventBus(eventBus)
//        filterChain = Mock(FilterChain)
//        filter = new InsightFilter()
//    }
//
//    def "Insight filter adds a UUID to the request headers"() {
//        given:
//        ServletResponse response = Mock(HttpServletResponse)
//        ServletRequest request = Mock(HttpServletRequest)
//        request.getRequestURL() >> new StringBuffer("URL")
//        when:
//        filter.doFilter(request, response, filterChain)
//        then:
//        1 * response.addHeader("insight-id", _)
//    }
//
//    def "Insight filter creates ServletRequestStarted and ServletRequestComplete events on successful request"() {
//        given:
//        ServletResponse response = Mock(HttpServletResponse)
//        ServletRequest request = Mock(HttpServletRequest)
//        request.getRequestURL() >> new StringBuffer()
//        TestEventRecorder testEventRecorder = new TestEventRecorder()
//        eventBus.register(testEventRecorder)
//        when:
//        filter.doFilter(request, response, filterChain)
//        then:
//        testEventRecorder.events[0] instanceof ServletRequestStartedEvent
//        testEventRecorder.events[1] instanceof ServletRequestCompleteEvent
//    }
//
//    def "Insight filter creates ServletRequestStarted and ServletRequestError events on failed request"() {
//        given:
//        ServletResponse response = Mock(HttpServletResponse)
//        ServletRequest request = Mock(HttpServletRequest)
//        request.getRequestURL() >> new StringBuffer()
//        TestEventRecorder testEventRecorder = new TestEventRecorder()
//        eventBus.register(testEventRecorder)
//        filterChain.doFilter(_, _) >> { throw new RuntimeException() }
//        when:
//        filter.doFilter(request, response, filterChain)
//        then:
//        thrown RuntimeException
//        testEventRecorder.events[0] instanceof ServletRequestStartedEvent
//        testEventRecorder.events[1] instanceof ServletRequestErrorEvent
//    }
//
//}
