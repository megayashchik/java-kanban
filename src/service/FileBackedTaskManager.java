package service;

import exceptions.ManagerSaveException;
import model.*;


import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String TASK_FIELD_NAMES = "id,type,name,status,description,epic_id";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public String toString(Task task) {
        return task.getId() + "," + task.getType() + "," + task.getTitle() + "," + task.getStatus() +
                "," + task.getDescription();
    }

    public String toString(Epic epic) {
        return epic.getId() + "," + epic.getType() + "," + epic.getTitle() + "," + epic.getStatus() +
                "," + epic.getDescription();
    }

    public String toString(Subtask subtask) {
        return subtask.getId() + "," + subtask.getType() + "," + subtask.getTitle() + "," + subtask.getStatus() +
                "," + subtask.getDescription() + "," + subtask.getEpicId();
    }

    public void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(TASK_FIELD_NAMES);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toString(epic) + "\n");
                for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private static Task fromString(String str) {
        String[] part = str.split(",");
        int id = Integer.parseInt(part[0]);
        TaskType type = TaskType.valueOf(part[1]);
        String title = part[2];
        TaskStatus status = TaskStatus.valueOf(part[3]);
        String description = part[4];

        switch (type) {
            case TASK:
                return new Task(title, description, id, status);
            case EPIC:
                return new Epic(title, description, id, status);
            case SUBTASK:
                return new Subtask(title, description, id, status, Integer.parseInt(part[5]));
        }
        return null;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    Task task = fromString(line);

                    switch (task.getType()) {
                        case TASK:
                            fileBackedTaskManager.createTask(task);
                            break;
                        case EPIC:
                            fileBackedTaskManager.createEpic((Epic) task);
                            break;
                        case SUBTASK:
                            fileBackedTaskManager.createSubtask((Subtask) task);
                            break;
                    }
                }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());

        }
        return fileBackedTaskManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

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
    }

    public static void main(String[] args) {

        TaskManager taskManager = new FileBackedTaskManager(new File("src/resources/java-kanban.csv"));

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

        Subtask subtask4 = new Subtask("Подзадача 4", "Описание подзадачи 4", epic2.getId());
        Subtask subtask5 = new Subtask("Подзадача 5", "Описание подзадачи 5", epic2.getId());
        taskManager.createSubtask(subtask4);
        taskManager.createSubtask(subtask5);

        System.out.println("\nЗадачи в TaskManager");
        FileBackedTaskManager.printAllTasks(taskManager);

        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(
                new File("src/resources/java-kanban.csv"));

        System.out.println("\nПроверка, что все задачи, эпики, подзадачи, которые были в старом менеджере, " +
                "есть в новом.");
        FileBackedTaskManager.printAllTasks(fileBackedTaskManager);
    }
}

