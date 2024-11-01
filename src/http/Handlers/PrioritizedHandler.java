package http.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        List<Task> prioritizedList = taskManager.getPrioritizedTasks();
        if (prioritizedList.isEmpty()) {
            sendNotFoundResponse(exchange, "Список приоритетных задач пуст");
        } else {
            sendOkResponse(exchange, gson.toJson(prioritizedList));
        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                handleGetPrioritized(exchange);
                break;
            default:
                sendNotAllowedResponse(exchange, "Метод не поддерживается.");
                break;
        }
    }
}
