package com.ebradshaw.insight.agent.instrumentation.springboot

import com.ea.agentloader.AgentLoader
import com.ebradshaw.insight.agent.servlet.InsightFilter
import spock.lang.Specification

import java.lang.instrument.Instrumentation

public class SpringBootFilterInstrumenterIT extends Specification {

    public static class AgentWrapper {
        public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
            inst.addTransformer(new SpringBootFilterInstrumenter("com/ebradshaw/insight/agent/instrumentation/springboot/TestAnnotationContext"));
            inst.retransformClasses(TestAnnotationContext)
        }
    }

    def setupSpec(){
        AgentLoader.loadAgentClass(AgentWrapper.class.getName(), null);
    }

    def "initialization should register the insight filter and call refresh"(){
        new TestAnnotationContext()
        expect:
        TestAnnotationContext.registered == [ InsightFilter.class ];
        TestAnnotationContext.refreshCalled
    }

}
