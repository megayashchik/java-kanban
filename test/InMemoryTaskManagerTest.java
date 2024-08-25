import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;


class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void init() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createNewTask_shouldSaveTask() {
        Task task = new Task("Задача_1", "Описание_1");

        taskManager.createTask(task);
        Task actualTask = taskManager.getTaskById(task.getId());

        Assertions.assertNotNull(actualTask, "Задача не была сохранена");
        Assertions.assertEquals(task, actualTask);
    }

    @Test
    void updateTask_shouldUpdateTaskWithSpecifiedTitleAndDescription() {
        Task task = new Task("Задача_1", "Описание_1");

        taskManager.createTask(task);
        Task savedTask = taskManager.getTaskById(task.getId());
        savedTask.setTitle("Задача_1_Updated");
        savedTask.setDescription("Описание_1_Updated");
        taskManager.updateTask(savedTask);
        Task actualUpdatedTask = taskManager.getTaskById(task.getId());

        Assertions.assertEquals(task, actualUpdatedTask);
    }

    @Test
    void deleteTaskById_shouldDeleteTask() {
        Task task = new Task("Задача_1", "Описание_1");

        taskManager.createTask(task);
        Task actualTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals(task, actualTask);
        taskManager.deleteTaskById(task.getId());
        Task deletedTask = taskManager.getTaskById(task.getId());

        Assertions.assertNull(deletedTask, "Задача с " + task.getId() + " id не удалена");
    }

    @Test
    void deleteAllTasks_shouldDeleteAllTasks() {
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_2", "Описание_2");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteAllTasks();

        Assertions.assertEquals(0, taskManager.getTasks().size(), "Не удалось удалить все задачи");
        Assertions.assertNotNull(taskManager.getTasks());
    }

    @Test
    void createNewEpic_shouldSaveEpic() {
        Epic epic = new Epic("Эпик_1", "Описание_1");

        taskManager.createEpic(epic);
        Epic actualEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertNotNull(actualEpic, "Эпик не был сохранен");
        Assertions.assertEquals(epic, actualEpic);
    }

    @Test
    void updateEpic_shouldUpdateEpicWithSpecifiedTitleAndDescription() {
        Epic epic = new Epic("Эпик_1", "Описание_1");

        taskManager.createEpic(epic);
        Epic savedEpic = taskManager.getEpicById(epic.getId());
        savedEpic.setTitle("Эпик_1_Updated");
        savedEpic.setDescription("Описание_1_Updated");
        taskManager.updateEpic(savedEpic);
        Epic actualUpdatedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertEquals(epic, actualUpdatedEpic);
    }

    @Test
    void deleteEpicById_shouldDeleteEpic() {
        Epic epic = new Epic("Эпик_1", "Описание_1");

        taskManager.createEpic(epic);
        Epic actualEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(epic, actualEpic);
        taskManager.deleteEpicById(epic.getId());
        Epic deletedEpic = taskManager.getEpicById(epic.getId());

        Assertions.assertNull(deletedEpic, "Эпик с " + epic.getId() + " id не удалён");
    }

    @Test
    void deleteAllEpics_shouldDeleteAllEpics() {
        Epic epic1 = new Epic("Эпик_1", "Описание_1");
        Epic epic2 = new Epic("Эпик_2", "Описание_2");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.deleteAllEpics();

        Assertions.assertEquals(0, taskManager.getEpics().size(), "Не удалось удалить все эпики");
        Assertions.assertNotNull(taskManager.getEpics());
    }

    @Test
    void createNewSubtask_shouldSaveSubtask() {
        Epic epic = new Epic("Эпик_1", "Описание_1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача_1", "Описание_1", epic.getId());

        taskManager.createSubtask(subtask);
        Subtask actualSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertNotNull(actualSubtask, "Подзадача не была сохранена");
        Assertions.assertEquals(subtask, actualSubtask);
    }

    @Test
    void updateSubtask_shouldUpdateSubtaskWithSpecifiedTitleAndDescription() {
        Epic epic = new Epic("Эпик_1", "Описание_1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача_1", "Описание_1", epic.getId());

        taskManager.createSubtask(subtask);
        Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
        savedSubtask.setTitle("Подзадача_1_Updated");
        savedSubtask.setDescription("Описание_1_Updated");
        taskManager.updateSubtask(savedSubtask);
        Subtask actualUpdatedSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertNotNull(actualUpdatedSubtask);
        Assertions.assertEquals("Подзадача_1_Updated", actualUpdatedSubtask.getTitle());
        Assertions.assertEquals("Описание_1_Updated", actualUpdatedSubtask.getDescription());
        Assertions.assertEquals(subtask, actualUpdatedSubtask);
    }

    @Test
    void deleteSubtaskById_shouldDeleteSubtask() {
        Epic epic = new Epic("Эпик_1", "Описание_1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача_1", "Описание_1", epic.getId());

        taskManager.createSubtask(subtask);
        Subtask actualUpdatedSubtask = taskManager.getSubtaskById(subtask.getId());
        Assertions.assertEquals(subtask, actualUpdatedSubtask);
        taskManager.deleteSubtaskById(subtask.getId());
        Subtask deletedSubtfsk = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertNull(deletedSubtfsk, "Подзадача с " + subtask.getId() + " id не удалёна");
    }

    @Test
    void deleteAllSubtasks_shouldDeleteAllSubtasks() {
        Epic epic = new Epic("Эпик_1", "Описание_1");
        taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача_1", "Описание_1", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача_2", "Описание_2", epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteAllEpics();

        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Не удалось удалить все подзадачи");
        Assertions.assertNotNull(taskManager.getSubtasks());
    }

    @Test
    void updateSubtask_shouldUpdateSubtaskWithSpecifiedId() { // изменить на status
        Epic epic = new Epic("Эпик_1", "Описание_1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача_1", "Описание_1", epic.getId());

        taskManager.createSubtask(subtask);
        subtask.setTitle("Подзадача_1_Updated");
        subtask.setDescription("Описание_1_Updated");
        taskManager.updateSubtask(subtask);

        Subtask expectedUpdatedSubtask = new Subtask("Подзадача_1_Updated", "Описание_1_Updated", subtask.getEpicId());
        expectedUpdatedSubtask.setId(subtask.getId());
        Subtask actualUpdatedSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertEquals(expectedUpdatedSubtask, actualUpdatedSubtask);
    }

    @Test
    void checkIfTasksAreEqualWithTheSameId() {
        Task task1 = new Task("Задача_1", "Описание_1");
        Task task2 = new Task("Задача_1", "Описание_1");

        task1.setId(1);
        task2.setId(1);

        Assertions.assertEquals(task1, task2);
}

    @Test
    void checkIfInheritorsOfTasksAreEqualWithTheSameId() {
        Epic epic1 = new Epic("Эпик_1", "Описание_1");
        Epic epic2 = new Epic("Эпик_1", "Описание_1");

        epic1.setId(1);
        epic2.setId(1);

        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    void taskManagerAlwaysReturnInitializedInstancesOfManagers() {
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager);
    }

    @Test
    void checkIfInMemoryTaskManagerAddDifferentTypesOfTask() {
        Task task = new Task("Задача_1", "Описание_1");
        Epic epic = new Epic("Эпик_1", "Описание_1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача_1", "Описание_1", epic.getId());

        taskManager.createTask(task);
        taskManager.createSubtask(subtask);

        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void checkThatTasksWithTheSpecifiedIdDoNotConflictWithGeneratedId() {
        Task taskWithGeneratedId = new Task("Задача_1", "Описание_1");
        Task taskWithSpecifiedId = new Task("Задача_2", "Описание_2");

        taskManager.createTask(taskWithGeneratedId);
        taskWithSpecifiedId.setId(4);
        taskManager.createTask(taskWithSpecifiedId);

        Assertions.assertEquals(taskWithGeneratedId, taskManager.getTaskById(taskWithGeneratedId.getId()));
        Assertions.assertEquals(taskWithSpecifiedId, taskManager.getTaskById(taskWithSpecifiedId.getId()));
        Assertions.assertNotEquals(taskWithGeneratedId.getId(), taskWithSpecifiedId.getId());
    }

    @Test
    void checkIfTaskUnchangedWhenAddToManager() {
        Task task = new Task("Задача_1", "Описание_1");

        taskManager.createTask(task);
        Task checkedTask = taskManager.getTaskById(task.getId());

        Assertions.assertEquals(task.getTitle(), checkedTask.getTitle());
        Assertions.assertEquals(task.getDescription(), checkedTask.getDescription());
        Assertions.assertEquals(task.getId(), checkedTask.getId());
        Assertions.assertEquals(task, checkedTask, "Задачи не равны");

    }

    @Test
    void checkThatTaskAddedToHistoryManagerRetainPreviousVersion() {
        Task task = new Task("Задача_1", "Описание_1");

        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        Task newTask = new Task("Новая_Задача_1", "Новое_Описание_1");
        newTask.setId(task.getId());
        newTask.setTitle("Новая_Задача_1");
        newTask.setDescription("Новое_Описание_1");
        taskManager.updateTask(newTask);
        taskManager.getTaskById(newTask.getId());
        List<Task> history = taskManager.getHistory();

        Assertions.assertEquals(2, history.size(), "Должно быть 2 задачи");
        Assertions.assertEquals(task, history.get(0), "Первой задачи нет");
        Assertions.assertEquals(newTask, history.get(1), "Второй задачи нет");
    }
}









