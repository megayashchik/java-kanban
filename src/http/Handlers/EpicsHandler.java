package http.Handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    private Epic getEpicFromRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseReader(isr).getAsJsonObject();
        return gson.fromJson(jsonObject, Epic.class);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getEpics();
        sendOkResponse(exchange, gson.toJson(epics));
    }

    private void handleGetEpicById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 3) {
            sendNotFoundResponse(exchange, "Неверный путь " + path);
            return;
        }

        try {
            String idString = parts[2];
            int id = Integer.parseInt(idString);
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                sendOkResponse(exchange, gson.toJson(epic));
            } else {
                sendNotFoundResponse(exchange, "Эпик с id " + id + " не найден.");
            }
        } catch (NumberFormatException e) {
            sendNotFoundResponse(exchange, "Неверное значение id эпика.");
        }
    }

    private void getEpicSubtasks(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 4) {
            sendNotFoundResponse(exchange, "Неверный путь.");
            return;
        }

        try {
            String epicIdString = parts[2];
            int epicId = Integer.parseInt(epicIdString);
            List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);
            sendOkResponse(exchange, gson.toJson(subtasks));
        } catch (NumberFormatException e) {
            sendNotFoundResponse(exchange, "Неверное значение id эпика");
        }
    }

    private void handleCreateEpic(HttpExchange exchange, Epic epic) throws IOException {
        try {
            taskManager.createEpic(epic);
            sendCreatedResponse(exchange, "Эпик создан");
        } catch (ManagerSaveException | IOException e) {
            sendInternalServerErrorResponse(exchange, "Задача не создана.");
        }
    }

    private void handleDeleteEpicById(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        if (parts.length != 3) {
            sendNotFoundResponse(exchange, "Неверный путь.");
            return;
        }

        try {
            String epicIdString = parts[2];
            int epicId = Integer.parseInt(epicIdString);
            taskManager.deleteEpicById(epicId);
            sendOkResponse(exchange, "Эпик удалён.");
        } catch (Exception e) {
            sendNotFoundResponse(exchange, "Произошла ошибка при удалении эпика.");

        }
    }

    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        switch (requestMethod) {
            case "GET":
                if (parts.length == 2 && "epics".equals(parts[1])) {
                    handleGetEpics(exchange);
                } else if (parts.length == 3 && "epics".equals(parts[1])) {
                    handleGetEpicById(exchange);
                } else if (parts.length == 4 && "epics".equals(parts[1]) && "subtasks".equals(parts[3])) {
                    getEpicSubtasks(exchange);
                }
                break;
            case "POST":
                Epic newEpic = getEpicFromRequestBody(exchange);
                if (newEpic != null) {
                    handleCreateEpic(exchange, newEpic);
                }
                break;
            case "DELETE":
                handleDeleteEpicById(exchange);
                break;
            default:
                sendNotAllowedResponse(exchange, "Метод не поддерживается.");
                break;
        }
    }
}
