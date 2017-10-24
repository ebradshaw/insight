# Insight
Insight is a Java agent for automatically aggregating request metrics from a web application.  For each request, we track request time, the number of Strings allocated as well as the total memory allocated.  It hooks into the web application runtime by injecting a special tracking filter into Servlet 3.0 compliant web servers.

The individual requests as well as aggregate data can be viewed at `localhost:8081/index.html`.  

## Usage
The Java Agent can be used attached to a web application on the command line as follows:
```java
java -javaagent path/to/insight-agent.jar
```
By default, the agent will launch a web server for metrics browsing on port 8081.  To modify the port, pass it as an agent argument:
```java
java -javaagent path/to/insight-agent.jar=port:{your_port}
```

## Development
Checkout the source code and run from the root:
```
gradlew build
```
The compiled agent jar file will be found under `./agent/build/libs/insight-agent.jar`

## Demo
To run a super simple demo, checkout the source code and run from the root:
```
gradlew demo
```
Navigate your browser to `localhost:8081/index.html` where the Insight UI will be running.  To generate sample http requests, browse to `localhost:8080/test`

## Limitations
* Currently only tracks allocations made by the requesting thread, not child threads.
* Request history is not persistant and only the 1000 most recent requests are tracked in memory.  Future implementations may include a persistance implementation.

## Dependencies
This agent relies on Google's [Allocation Instrumenter](https://github.com/google/allocation-instrumenter) to track String object creation as well as Memory allocation.  Thanks Google!
