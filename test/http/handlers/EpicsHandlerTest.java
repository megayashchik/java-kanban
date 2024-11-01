package http.handlers;

import com.google.gson.Gson;
import http.HttpTaskServer;
import model.Epic;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicsHandlerTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = taskServer.getGson();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Подзадача 1", "Описание подзадачи 1");
        Epic epic2 = new Epic("Подзадача 2", "Описание подзадачи 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getEpics();

        assertEquals(2, epicsFromManager.size());
        assertTrue(epicsFromManager.contains(epic1));
        assertTrue(epicsFromManager.contains(epic2));
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Подзадача", "Описание подзадачи");
        manager.createEpic(epic);
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());

        HttpRequest request = HttpRequest.newBuilder(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertNull(manager.getEpicById(epic.getId()));
    }

    @Test
    void shouldGetEpicByIdNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1111");

        HttpRequest request = HttpRequest.newBuilder(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
