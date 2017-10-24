package com.ebradshaw.insight.agent.servlet;

import com.ebradshaw.insight.agent.events.Events;
import com.google.common.eventbus.EventBus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebFilter(filterName = "insightFilter")
public class InsightFilter implements Filter {

    private static EventBus eventBus;

    public static void setEventBus(EventBus eventBus) {
        InsightFilter.eventBus = eventBus;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String id = UUID.randomUUID().toString();
        ((HttpServletResponse)response).addHeader("insight-id", id);


        eventBus.post(Events.servletRequestStarted(id, ((HttpServletRequest)request).getRequestURL().toString()));

        try {
            chain.doFilter(request, response);
        } catch(RuntimeException ex){
            eventBus.post(Events.servletRequestError());
            throw ex;
        }

        eventBus.post(Events.servletRequestComplete());
    }

    @Override
    public void destroy() { }
}
