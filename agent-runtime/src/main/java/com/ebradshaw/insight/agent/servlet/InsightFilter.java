package com.ebradshaw.insight.agent.servlet;

import com.ebradshaw.insight.agent.InsightRuntime;
import com.ebradshaw.insight.agent.events.Events;

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

@WebFilter(filterName = "insightFilter", asyncSupported = true)
public class InsightFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String id = UUID.randomUUID().toString();
        ((HttpServletResponse)response).addHeader("insight-id", id);

        InsightRuntime.eventBus.post(Events.servletRequestStarted(id, ((HttpServletRequest)request).getRequestURL().toString()));

        try {
            chain.doFilter(request, response);
        } catch(RuntimeException ex){
            InsightRuntime.eventBus.post(Events.servletRequestError());
            throw ex;
        }

        InsightRuntime.eventBus.post(Events.servletRequestComplete());
    }

    @Override
    public void destroy() { }
}
