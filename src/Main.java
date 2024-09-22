import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.LinkedListCustom;
import service.Managers;
import service.TaskManager;

public class Main {

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);

            for (Task task : manager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printHistory(TaskManager taskManager) {
        System.out.println("\nИстория:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId());
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        task1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(TaskStatus.NEW);
        taskManager.updateTask(task2);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);

        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task2.getId());

        Main.printHistory(taskManager);

        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask3.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getTaskById(task2.getId());

        System.out.println("\nПроверка повторов:");
        Main.printHistory(taskManager);

        taskManager.deleteTaskById(task2.getId());
        System.out.println("\nПосле удаления task2");
        Main.printHistory(taskManager);

        taskManager.deleteEpicById(epic1.getId());
        System.out.println("\nУдаление epic1 с 3 подзадачами:");
        Main.printHistory(taskManager);

    }
}




