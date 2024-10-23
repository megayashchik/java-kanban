import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldCreateAndGetTask() {
        Task task = new Task("Задача", "Описание задачи");

        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals(task, retrievedTask, "Задача должна быть корректно создана и получена");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Задача", "Описание задачи");
        taskManager.createTask(task);

        task.setTitle("Обновлённая задача");
        task.setDescription("Обновлённое описание задачи");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());

        assertEquals("Обновлённая задача", updatedTask.getTitle(),
                "Заголовок задачи должен быть обновлён");
        assertEquals("Обновлённое описание задачи", updatedTask.getDescription(),
                "Описание задачи должно быть обновлено");
    }

    @Test
    void shouldDeleteTaskById() {
        Task task = new Task("Задача", "Описание задачи");

        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        assertNull(taskManager.getTaskById(task.getId()), "Задача должна быть удалена");
    }

    @Test
    void shouldNotTimeIntersection() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Задача 1", "Описание задачи 1", startTime, Duration.ofHours(1));
        Task task2 = new Task("Задача 2", "Описание задачи 2", startTime, Duration.ofHours(1));

        taskManager.createTask(task1);
        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() {
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void shouldCreateAndGetEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void shouldCreateAndGetSubtask() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(epic.getId(), retrievedSubtask.getEpicId());
    }

    @Test
    void shouldUpdateEpicStatusWithSubtaskStatus() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача", "Описание подзадачи", epic.getId());
        taskManager.createSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertTrue(updatedEpic.getStatus().toString().equalsIgnoreCase("IN_PROGRESS") ||
                        updatedEpic.getStatus().toString().equalsIgnoreCase("DONE"),
                "Статус эпика должен быть IN_PROGRESS или DONE");
    }
}




















