import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicStatusTest {

    private TaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void allSubtasksNew() {
        Epic epic = new Epic("Эпик", "Описание эпика");

        manager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            Subtask subtask = new Subtask("Подзадача " + i, "Описание подзадачи", epic.getId(),
                    TaskStatus.NEW);
            manager.createSubtask(subtask);
        }

        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void allSubtasksDone() {
        Epic epic = new Epic("Эпик", "Описание эпика");

        manager.createEpic(epic);
        for (int i = 0; i < 3; i++) {
            Subtask subtask = new Subtask("Подзадача " + i, "Описание подзадачи", epic.getId(),
                    TaskStatus.DONE);
            manager.createSubtask(subtask);
        }

        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void subtasksNewAndDone() {
        Epic epic = new Epic("Эпик", "Описание эпика");

        manager.createEpic(epic);
        manager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", epic.getId(),
                TaskStatus.NEW));
        manager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", epic.getId(),
                TaskStatus.DONE));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void subtasksInProgress() {
        Epic epic = new Epic("Эпик", "Описание эпика");

        manager.createEpic(epic);
        manager.createSubtask(new Subtask("Подзадача", "Описание подзадачи", epic.getId(),
                TaskStatus.IN_PROGRESS));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }
}
