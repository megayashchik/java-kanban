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

public class PrioritizedHandlerTest {

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
    void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW,
                Duration.ofHours(2), LocalDateTime.now().plusDays(1));
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS,
                Duration.ofHours(1), LocalDateTime.now().plusDays(2));
        manager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 OK");

        List<Task> prioritizedTasks = manager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Список приоритетных задач не должен быть null");
        assertEquals(2, prioritizedTasks.size(), "Количество приоритетных задач должно быть 2");
    }

    @Test
    void shouldReturnNotFoundWhenNoPrioritizedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Ожидался статус 404 Not Found");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Список приоритетных задач пуст"),
                "Ожидалось сообщение о пустом списке приоритетных задач");
    }

    @Test
    void shouldReturnMethodNotAllowedForNonGetRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder(url)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode(), "Ожидался статус 405 Method Not Allowed");

        String responseBody = response.body();
        assertTrue(responseBody.contains("Метод не поддерживается"),
                "Ожидалось сообщение о неподдерживаемом методе");
    }
}
