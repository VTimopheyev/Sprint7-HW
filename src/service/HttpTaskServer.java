package service;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {


    public HttpTaskServer() throws IOException {
        HttpServer.create(new InetSocketAddress(8080), 0);
    }



}
