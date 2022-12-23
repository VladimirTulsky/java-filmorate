package server;

import com.sun.net.httpserver.HttpServer;
import managers.HTTPTaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(HTTPTaskManager taskManager) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
        httpServer.start();
    }

    public void startServer() {
        System.out.println("Сервер запущен на " + PORT + "порту");
        httpServer.start();
    }

    public void stopServer() {
        System.out.println("Сервер на порту " + PORT + "остановлен");
        httpServer.stop(1);
    }
}


