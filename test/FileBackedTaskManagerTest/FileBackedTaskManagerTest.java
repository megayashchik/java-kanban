package FileBackedTaskManagerTest;


import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private File file;
    private FileBackedTaskManager fileBackedTaskManager;


    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    void fileExistTest() {
        assertTrue(file.exists(), "Файл " + file.getName() + " не существует!");
    }

    @Test
    void checkLoadTaskFromFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(loadedManager.getTasks(), "Задачи не считаны из файла!");
    }

    @Test
    void checkDeleteTaskTest() {
        Task task = new Task("Задача к удалению", "Описание", 1, TaskStatus.NEW);
        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.deleteTaskById(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, loadedManager.getTasks().size(), "Задача не была удалена!");
    }

    @Test
    void checkDeleteEpicTest() {
        Epic epic = new Epic("Эпик к удалению", "Описание", 1, TaskStatus.NEW);
        fileBackedTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача к удалению", "Описание", epic.getId(), TaskStatus.NEW);
        fileBackedTaskManager.createSubtask(subtask);

        fileBackedTaskManager.deleteEpicById(epic.getId());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, loadedManager.getEpics().size(), "Эпик не был удален!");
        assertEquals(0, loadedManager.getSubtasks().size(), "Подзадача не была удалена!");
    }
}








