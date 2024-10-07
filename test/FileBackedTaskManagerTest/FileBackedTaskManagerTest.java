package FileBackedTaskManagerTest;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    public void tearDown() {
        tempFile.delete();
    }

    @Test
    public void shouldCreateAndSaveTask() {
        Task task = new Task("Задача", "Описание задачи");
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getTasks();

        assertEquals(1, loadedTasks.size(), "Должна быть одна задача после загрузки");
        assertEquals(task, loadedTasks.get(0), "Загруженная задача должна совпадать с исходной");
    }

    @Test
    public void shouldLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    public void shouldSetCorrectIdAfterCreateAndSave() {
        Task task = new Task("Задача", "Описание задачи");
        manager.createTask(task);
        int expectedId = task.getId();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskById(expectedId);

        assertNotNull(loadedTask, "Задача должна быть загружена из файла");
        assertEquals(expectedId, loadedTask.getId(),
                "ID задачи после загрузки должен совпадать с ожидаемым ID");
    }

    @Test
    public void testUpdateAndSaveTask() {
        Task task = new Task("Задача", "Описание задачи");
        manager.createTask(task);
        task.setTitle("Обновленная Задача");
        manager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loadedManager.getTaskById(task.getId());

        assertEquals("Обновленная Задача", loadedTask.getTitle(),
                "Загруженная задача должна иметь обновленное название");
    }

    @Test
    public void testDeleteAndSaveTask() {
        Task task = new Task("Задача", "Описание задачи");
        manager.createTask(task);
        manager.deleteTaskById(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertNull(loadedManager.getTaskById(task.getId()), "Задача должна быть удалена после загрузки");
    }

    @Test
    public void shouldSaveAndLoadEpicWithSubtasks() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Epic> loadedEpics = loadedManager.getEpics();
        List<Subtask> loadedSubtasks = loadedManager.getSubtasksByEpicId(epic.getId());

        assertEquals(1, loadedEpics.size(), "Должен быть один эпик после загрузки");
        assertEquals(2, loadedSubtasks.size(), "Должно быть две подзадачи после загрузки");
    }

    @Test
    public void shouldDeleteEpicWithSubtasks() {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteEpicById(epic1.getId());

        assertNull(manager.getEpicById(epic1.getId()), "Эпик 1 должен быть удалён");
        assertNull(manager.getSubtaskById(subtask1.getId()), "Подзадача 1 должна быть удаленна");
        assertNull(manager.getSubtaskById(subtask2.getId()), "Подзадача 2должна быть удаленна");
    }
}








