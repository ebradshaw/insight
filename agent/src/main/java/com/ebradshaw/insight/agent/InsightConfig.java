package com.ebradshaw.insight.agent;

public class InsightConfig  {

    private final int port;

    public InsightConfig(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public static InsightConfig fromAgentArgs(String agentArgs) {
        if(agentArgs == null) agentArgs = "";

        String[] args = agentArgs.split(",");

        int port = 8081;

        for (String arg : args) {
            if(arg.length() == 0) continue;

            String[] keyValuePair = arg.split(":");
            if (keyValuePair.length != 2) {
                throw new IllegalArgumentException("agent args must be of format [key1:[value1],[key2]:[value2]...");
            }

            switch (keyValuePair[0]){
                case "port": port = Integer.parseInt(keyValuePair[1]); break;
            }
        }

        return new InsightConfig(port);
    }

}
