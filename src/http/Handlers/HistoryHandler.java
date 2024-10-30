package http.Handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> historyList = taskManager.getHistory();
        if (historyList.isEmpty()) {
            sendNotFoundResponse(exchange, "Список истории пуст");
        } else {
            sendOkResponse(exchange, gson.toJson(historyList));
        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();

        switch (requestMethod) {
            case "GET":
                handleGetHistory(exchange);
                break;
            default:
                sendNotAllowedResponse(exchange, "Метод не поддерживается.");
                break;
        }
    }
}
