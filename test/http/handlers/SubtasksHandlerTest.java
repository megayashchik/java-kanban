package http.handlers;

import com.google.gson.Gson;
import http.HttpTaskServer;
import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtasksHandlerTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = taskServer.getGson();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, TaskStatus.NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofHours(1));
        String json = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Подзадача не была создана");

        List<Subtask> subtasks = manager.getSubtasks();
        assertEquals(1, subtasks.size(), "Подзадача не добавлена в менеджер");
        assertEquals("Подзадача", subtasks.get(0).getTitle(), "Название подзадачи некорректное");
    }

    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, TaskStatus.NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении всех подзадач");

        List<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "Подзадачи не возвращены");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач");
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, TaskStatus.NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при получении подзадачи по ID");

        Subtask receivedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtask, receivedSubtask, "Подзадача не совпадает с ожидаемой");
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", 2, TaskStatus.NEW,
                epic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ошибка при удалении подзадачи");
        assertNull(manager.getSubtaskById(subtask.getId()), "Подзадача не была удалена");
    }

    @Test
    void shouldReturn404ForNonExistingSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/123");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 для несуществующей подзадачи");
    }
}
