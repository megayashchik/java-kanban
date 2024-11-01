package http.handlers;

import com.google.gson.Gson;
import http.HttpTaskServer;
import model.Task;
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

public class TasksHandlerTest {

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
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание задачи", TaskStatus.NEW,
                Duration.ofHours(1), LocalDateTime.now());
        String json = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasks = manager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals("Задача", tasks.get(0).getTitle(), "Задача создана.");
    }

    @Test
    public void shouldGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание задачи",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task, receivedTask);
    }


    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        task.setTitle("Обновленная задача");
        String updatedTaskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder(url)
                .PUT(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task updatedTaskFromManager = manager.getTaskById(task.getId());
        assertEquals("Обновленная задача", updatedTaskFromManager.getTitle());
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание", TaskStatus.NEW,
                Duration.ofMinutes(5), LocalDateTime.now());
        manager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNull(manager.getTaskById(task.getId()));
    }

    @Test
    void shouldGetTaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/123");

        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Задача не найдена.");
    }

    @Test
    void shouldDeleteTaskByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1234");

        HttpRequest request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Задача не найдена.");
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingTask() throws IOException, InterruptedException {
        Task task = new Task("Задача", "Описание",
                TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        task.setId(12345);
        String json = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder(url)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
