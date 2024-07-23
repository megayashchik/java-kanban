public class Main {

    private static void printAllTasks(TaskManager taskManager) {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getEpics()) {
            System.out.println(epic);
            for (Task task : taskManager.getSubtasksByEpicId(epic.getId())) {
                System.out.println("Подзадачи: " + task);
            }
        }
    }

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", " Описание подзадачи 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", " Описание подзадачи 2", epic1.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic2.getId());
        taskManager.createSubtask(subtask3);

        Main.printAllTasks(taskManager);

        Task updatedTask1 = new Task("Обновлённая задача 1", "Описание обновлённой задачи 1",
                TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask1);

        Subtask updatedSubtask1 = new Subtask("Обновлённая подзадача 1", "Описание обновлённой подзадачи 1",
                TaskStatus.IN_PROGRESS, subtask1.getId(), subtask1.getEpicId());
        taskManager.updateSubtask(updatedSubtask1);
        Subtask updatedSubtask2 = new Subtask("Обновлённая подзадача 2", "Описание обновлённой подзадачи 2",
                TaskStatus.DONE, subtask2.getId(), subtask2.getEpicId());
        taskManager.updateSubtask(updatedSubtask2);

        System.out.println("\nПосле изменения статуса");
        Main.printAllTasks(taskManager);

        System.out.println("\nПосле удаления");
        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(3);
        Main.printAllTasks(taskManager);
    }
}
