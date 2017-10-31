package com.ebradshaw.insight.agent.server;

import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InsightStaticHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        String path = t.getRequestURI().getPath();
        try(InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("insight-static" + path)){
            if(inputStream != null) {
                byte[] output = ByteStreams.toByteArray(inputStream);
                t.sendResponseHeaders(200, output.length);
                OutputStream os = t.getResponseBody();
                os.write(output);
                os.close();
            } else {
                t.sendResponseHeaders(404, 0);
                t.getResponseBody().close();
            }
        } catch(Exception ex){
            String output = ex.getMessage();
            t.sendResponseHeaders(400, output.length());
            OutputStream os = t.getResponseBody();
            os.write(output.getBytes());
            os.close();
        }
    }
}
