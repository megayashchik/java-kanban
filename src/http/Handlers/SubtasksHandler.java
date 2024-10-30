package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private Subtask getSubtaskFromRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
        return gson.fromJson(jsonObject, Subtask.class);
    }

    private void handlerGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getSubtasks();
        sendOkResponse(exchange, gson.toJson(subtasks));
    }

    private void handlerGetSubtaskById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 4) {
            sendNotFoundResponse(exchange, "Неверный путь " + path);
            return;
        }

        try {
            String subtuskIdString = parts[2];
            int subtaskId = Integer.parseInt(subtuskIdString);
            Subtask subtask = taskManager.getSubtaskById(subtaskId);
            if (subtask != null) {
                sendOkResponse(exchange, gson.toJson(subtask));
            } else {
                sendNotFoundResponse(exchange, "Подзадача с id " + subtaskId + " не найдена.");
            }
        } catch (NumberFormatException e) {
            sendNotFoundResponse(exchange, "Неверное значение id подзадачи.");
        }

    }

    private void handleCreateSubtask(HttpExchange exchange, Subtask subtask) throws IOException {
        try {
            taskManager.createSubtask(subtask);
            sendCreatedResponse(exchange, "Подзадача создана.");
        } catch (ManagerSaveException | IOException e) {
            sendInternalServerErrorResponse(exchange, "Подзадача не создана.");
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange, Subtask subtask) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Subtask subtaskToUpdate = gson.fromJson(requestBody, Subtask.class);

        try {
            taskManager.updateSubtask(subtaskToUpdate);
            sendOkResponse(exchange, "Подзадача " + subtaskToUpdate.getId() + " обновлена.");
        } catch (Exception e) {
            System.out.println("Ошибка обновления подзадачи.");
            sendHasInteractionsResponse(exchange, e.getMessage());
        }
    }

    private void handleDeleteSubtaskById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 3) {
            sendNotFoundResponse(exchange, "Неверный путь.");
            return;
        }

        try {
            String subtaskIdString = parts[2];
            int subtaskId = Integer.parseInt(subtaskIdString);
            taskManager.deleteSubtaskById(subtaskId);
            sendOkResponse(exchange, "Подзадача удалёна.");
        } catch (Exception e) {
            sendNotFoundResponse(exchange, "Произошла ошибка при удалении подзадачи.");
        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        switch (requestMethod) {
            case "GET":
                if (parts.length == 3 && "subtasks".equals(parts[1])) {
                    handlerGetSubtasks(exchange);
                } else if (parts.length == 4 && "subtasks".equals(parts[1])) {
                    handlerGetSubtaskById(exchange);
                } else {
                    sendNotFoundResponse(exchange, "Неверный путь.");
                }
                break;
            case "POST":
                Subtask newSubtask = getSubtaskFromRequestBody(exchange);
                if (newSubtask != null) {
                    handleCreateSubtask(exchange, newSubtask);
                }
                break;
            case "DELETE":
                handleDeleteSubtaskById(exchange);
                break;
            default:
                sendNotAllowedResponse(exchange, "Метод не поддерживается.");
                break;
        }
    }
}






























