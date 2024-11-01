package http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.Handlers.*;
import service.Managers;
import service.TaskManager;
import util.DurationAdapter;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final TaskManager taskManager;
    private final int port = 8080;
    private final HttpServer httpServer;
    private final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public Gson getGson() {
        return gson;
    }

    public void start() {
        httpServer.start();
        System.out.println("Http-сервер запущен на " + port + " порту.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Http-сервер на порту" + port + " остановлен.");
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);

        server.start();
    }
}



