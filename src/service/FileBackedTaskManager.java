package service;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;
    private static final String TASK_FIELD_NAMES = "id,type,name,status,description,epic_id," +
            "start_time,end_time,duration";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(TASK_FIELD_NAMES);
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(toStringTask(task));
                writer.newLine();
            }
            for (Epic epic : getEpics()) {
                writer.write(toStringEpic(epic) + "\n");
                for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                    writer.write(toStringSubtask(subtask) + "\n");
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
        int epicId = Integer.parseInt(part[5]);

        LocalDateTime startTime = null;
        if (!part[6].equals("null")) {
            startTime = LocalDateTime.parse(part[6], DATE_TIME_FORMATTER);
        }

        LocalDateTime endTime = null;
        if (!part[7].equals("null")) {
            endTime = LocalDateTime.parse(part[7], DATE_TIME_FORMATTER);
        }

        Duration duration = Duration.ofMinutes(Long.parseLong(part[8]));

        switch (type) {
            case TASK:
                return new Task(title, description, id, status, startTime, duration);
            case EPIC:
                ArrayList<Integer> subtaskIds = new ArrayList<>();
                return new Epic(title, description, id, status, subtaskIds, startTime, endTime, duration);
            case SUBTASK:
                return new Subtask(title, description, id, status, epicId, startTime, duration);
            default:
                throw new IllegalStateException("Неправильный тип задачи: " + type + " .Исходная строка: " + str);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Task task = fromString(line);
                maxId = Math.max(maxId, task.getId());

                switch (task.getType()) {
                    case TASK:
                        fileBackedTaskManager.addTask(task);
                        fileBackedTaskManager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        fileBackedTaskManager.addEpic((Epic) task);
                        break;
                    case SUBTASK:
                        fileBackedTaskManager.addSubtask((Subtask) task);
                        fileBackedTaskManager.prioritizedTasks.add(task);
                        break;
                }
            }

            for (Subtask subtask : fileBackedTaskManager.subtasks.values()) {
                Epic epic = fileBackedTaskManager.epics.get(subtask.getEpicId());
                epic.addSubtaskId(subtask.getId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());

        }
        fileBackedTaskManager.id = maxId + 1;
        return fileBackedTaskManager;
    }

    public String toStringTask(Task task) {
        return task.getId() + ","
                + task.getType() + ","
                + task.getTitle() + ","
                + task.getStatus() + ","
                + task.getDescription() + ","
                + task.getStartTimeToString() + ","
                + task.getEndTimeToString() + ","
                + task.getDurationToMinutes();
    }

    private String toStringEpic(Epic epic) {
        return epic.getId() + ","
                + epic.getType() + ","
                + epic.getTitle() + ","
                + epic.getStatus() + ","
                + epic.getDescription() + ","
                + epic.getSubtaskIds().toString().replace("[", "")
                .replace("]", "") + ","
                + epic.getStartTimeToString() + ","
                + epic.getEndTimeToString() + ","
                + epic.getDuration().toString();
    }

    private String toStringSubtask(Subtask subtask) {
        return subtask.getId() + ","
                + subtask.getType() + ","
                + subtask.getTitle() + ","
                + subtask.getStatus() + ","
                + subtask.getDescription() + ","
                + subtask.getEpicId() + ","
                + subtask.getStartTimeToString() + ","
                + subtask.getEndTimeToString() + ","
                + subtask.getDuration().toString();
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

        Task task1 = new Task("Задача 1", "Описание задачи 1",
                LocalDateTime.of(2024, 10, 20, 10, 0), Duration.ofMinutes(100));
        Task task2 = new Task("Задача 2", "Описание задачи 2",
                LocalDateTime.of(2024, 10, 20, 12, 0), Duration.ofMinutes(100));
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", epic1.getId(),
                LocalDateTime.of(2024, 10, 20, 14, 0), Duration.ofMinutes(100));
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", epic1.getId(),
                LocalDateTime.of(2024, 10, 20, 16, 30), Duration.ofMinutes(100));
        Subtask subtask3 = new Subtask("Подзадача 3", "Описание подзадачи 3", epic1.getId(),
                LocalDateTime.of(2024, 10, 20, 19, 00), Duration.ofMinutes(100));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        System.out.println("\nЗадачи в TaskManager");
        FileBackedTaskManager.printAllTasks(taskManager);

//        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(
//                new File("src/resources/java-kanban.csv"));
//
//        System.out.println("\nПроверка, что все задачи, эпики, подзадачи, которые были в старом менеджере, " +
//                "есть в новом.");
//        FileBackedTaskManager.printAllTasks(fileBackedTaskManager);

    }
}



