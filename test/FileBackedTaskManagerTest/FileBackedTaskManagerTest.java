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
    public void shouldSaveAndLoadEmptyFile() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loadedManager.getTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(loadedManager.getEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(loadedManager.getSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }

    @Test
    public void shouldSaveMultipleTasks() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        manager.createTask(task1);
        manager.createTask(task2);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getTasks().size(), "Должно быть 2 задачи.");
        assertTrue(loadedManager.getTasks().contains(task1), "Загруженный менеджер должен содержать Задача 1");
        assertTrue(loadedManager.getTasks().contains(task2), "Загруженный менеджер должен содержать Задача 2");
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








