package com.ebradshaw.insight.agent

import spock.lang.Specification

class InsightConfigTest extends Specification{

    def "InsightConfig.fromAgentArgs should parse port properly"(){
        given:
        def config = InsightConfig.fromAgentArgs("port:2000");
        expect:
        config.port == 2000
    }

    def "InsightConfig.fromAgentArgs should successfully return when passed a null input"(){
        when:
        InsightConfig.fromAgentArgs(null);
        then:
        noExceptionThrown()
    }

}
