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
}



