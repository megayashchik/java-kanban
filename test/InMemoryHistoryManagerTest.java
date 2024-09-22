import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistoryManager();
    }

    @Test
    void addInHistory_ShouldAddToHistory() {
        Task task1 = new Task("Задача_1", "Описание_1");
        taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());
        historyManager.addInHistory(task1);
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(1, history.size(), "Задача_1 должна быть в истории");
    }

    @Test
    void addInHistory_TwoTaskShouldBeAddToHistory_thenShowLastAdded() {
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_2", "Описание_2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        historyManager.addInHistory(task1);
        historyManager.addInHistory(task2);
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(2, history.size(), "Задача_2 должна быть последней в истории");
    }

    @Test
    void addInHistory_ShouldReplaceTheFirstTask() {
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_2", "Описание_2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        historyManager.addInHistory(task1);
        historyManager.addInHistory(task2);
        historyManager.addInHistory(task1);

        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(task2, history.get(0), "task2 должен быть первым");
        Assertions.assertEquals(task1, history.get(1), "task2 должен быть первым");
    }

    @Test
    void addInHistory_ShouldBeShownInOrderOfAddition() {
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_2", "Описание_2");
        Task task3 = new Task("Задача_3", "Описание_3");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        historyManager.addInHistory(task2);
        historyManager.addInHistory(task3);
        historyManager.addInHistory(task1);
        List<Task> history = historyManager.getHistory();

        Assertions.assertEquals(3, history.size(), "В истории должно быть 3 задачи");
        Assertions.assertEquals(task2, history.get(0), "task2 должен быть первым");
        Assertions.assertEquals(task3, history.get(1), "task3 должен быть вторым");
        Assertions.assertEquals(task1, history.get(2), "task1 должен быть последним");
    }

    @Test
    void addInHistory_ShouldBeNoDuplicates() {
        Task task = new Task("Задача_1", "Описание_1");
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());
        historyManager.addInHistory(task);
        historyManager.addInHistory(task);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size(), "Должна быть только одна задача");
    }

    @Test
    void removeFromHistory_ShouldRemoveFromMiddleOfHistory() {
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_2", "Описание_2");
        Task task3 = new Task("Задача_3", "Описание_3");
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        historyManager.addInHistory(task1);
        historyManager.addInHistory(task2);
        historyManager.addInHistory(task3);

        List<Task> history = taskManager.getHistory();
        Assertions.assertEquals(3, history.size(), "Должно быть 3 задачи");

        historyManager.removeFromHistory(task2.getId());
        history = historyManager.getHistory();

        Assertions.assertFalse(history.contains(task2), "Задача_2 должна быть удалена из истории");
    }

    @Test
    void removedSubtasks_ShouldNotRetainIds() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача_1", "Описание подзадачи_1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача_2", "Описание подзадачи_2", epic.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        historyManager.addInHistory(epic);
        historyManager.addInHistory(subtask1);
        historyManager.addInHistory(subtask2);
        System.out.println("Состояние истории до удаления: " + historyManager.getHistory());
        System.out.println("ID подзадач в эпике перед удалением: " + epic.getSubtaskIds());
        taskManager.deleteSubtaskById(subtask1.getId());
        taskManager.deleteSubtaskById(subtask2.getId());

        List<Task> history = historyManager.getHistory();
        System.out.println("Состояние истории после удаления: " + historyManager.getHistory());
        System.out.println("ID подзадач в эпике после удалением: " + epic.getSubtaskIds());
        Assertions.assertEquals(1, history.size(), "Должен быть остаться только 1 эпик");
        Assertions.assertTrue(history.contains(epic), "Эпик должен быть в истории");
        Assertions.assertFalse(history.contains(subtask1), "Подзадача_1 не должна содержаться в истории");
        Assertions.assertFalse(history.contains(subtask2), "Подзадача_2 не должна содержаться в истории");

        List<Integer> listOfSubtasksIds = epic.getSubtaskIds();
        Assertions.assertFalse(listOfSubtasksIds.contains(subtask1.getId()), "Эпик не должен содержать id подзадачи_1");
        Assertions.assertFalse(listOfSubtasksIds.contains(subtask2.getId()), "Эпик не должен содержать id подзадачи_2");
    }

    @Test
    void updatedTaskShouldRetainInHistory() {
        Task task = new Task("Задача", "Описание");
        taskManager.createTask(task);

        historyManager.addInHistory(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.contains(task), "Обновленная задача должна быть в истории");
    }
}
