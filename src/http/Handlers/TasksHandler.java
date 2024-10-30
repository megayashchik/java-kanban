package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private Task getTaskFromRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
        return gson.fromJson(jsonObject, Task.class);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getTasks();
        sendOkResponse(exchange, gson.toJson(tasks));
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length == 3) {
            System.out.println("Неверный путь: " + path);
            sendNotFoundResponse(exchange, "неверный путь");
            return;
        }

        try {
            String idString = parts[2];
            int id = Integer.parseInt(idString);
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                sendOkResponse(exchange, gson.toJson(task));
            } else {
                sendNotFoundResponse(exchange, "Задача с id = " + id + " не найдена.");
            }
        } catch (NumberFormatException e) {
            sendNotFoundResponse(exchange, "Неверное значение id задачи.");
        }
    }

    private void handleCreateTask(HttpExchange exchange, Task task) throws IOException {
        try {
            task.setStatus(TaskStatus.NEW);
            taskManager.createTask(task);
            sendCreatedResponse(exchange, "Задача создана.");
        } catch (ManagerSaveException | IOException e) {
            sendInternalServerErrorResponse(exchange, "Задача не создана.");
        }
    }

    private void handleUpdateTask(HttpExchange exchange, Task task) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes());
        Task taskToUpdate = gson.fromJson(requestBody, Task.class);

        try {
            taskManager.updateTask(taskToUpdate);
            sendOkResponse(exchange, "Задача " + taskToUpdate.getId() + " обновлена.");
        } catch (Exception e) {
            System.out.println("Ошибка обновления задачи.");
            sendHasInteractionsResponse(exchange, e.getMessage());
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange) throws IOException {
        String requestUri = exchange.getRequestURI().toString();
        String[] uriParts = requestUri.split("/");
        if (uriParts.length != 3) {
            sendNotFoundResponse(exchange, "Не верный запрос.");
            return;
        }

        try {
            String taskIdString = uriParts[2];
            int taskId = Integer.parseInt(taskIdString);
            taskManager.deleteTaskById(taskId);
            sendOkResponse(exchange, "Задача удалена.");
        } catch (Exception e) {
            sendNotFoundResponse(exchange, "Произошла ошибка при удалении задачи.");
        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        switch (requestMethod) {
            case "GET":
                if (parts.length == 2) {
                    handleGetTaskById(exchange);
                } else {
                    handleGetTasks(exchange);
                }
                break;
            case "POST":
                Task newTask = getTaskFromRequestBody(exchange);
                handleCreateTask(exchange, newTask);
                break;
            case "PUT":
                Task taskToUpdate = getTaskFromRequestBody(exchange);
                handleUpdateTask(exchange, taskToUpdate);
                break;
            case "DELETE":
                handleDeleteTaskById(exchange);
                break;
            default:
                sendNotAllowedResponse(exchange, "Метод не поддерживается.");
                break;
        }
    }
}