package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistoryManager();

    private int id = 0;

    private int generateId() {
        return ++id;
    }

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с " + id + " id не существует");
        } else {
            historyManager.addInHistory(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Задачи с " + id + " id не существует?");
        } else {
            historyManager.addInHistory(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Задачи с " + id + " id не существует");
        } else {
            historyManager.addInHistory(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtaskIds = epics.get(id).getSubtaskIds();
            for (Integer subtaskId : subtaskIds) {
                subtasksList.add(subtasks.get(subtaskId));
            }
            return subtasksList;
        } else {
            System.out.println("Такого эпика не существует");
            return null;
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            historyManager.removeFromHistory(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksIds = epics.get(id).getSubtaskIds();
            for (Integer subtasksId : subtasksIds) {
                subtasks.remove(subtasksId);
                historyManager.removeFromHistory(subtasksId);
            }
            epics.remove(id);
            historyManager.removeFromHistory(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).deleteSubtaskId(id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
            historyManager.removeFromHistory(id);
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            for (Integer id : tasks.keySet()) {
                historyManager.removeFromHistory(id);
            }

            tasks.clear();
        } else {
            System.out.println("Список задач пуст");
        }
    }

    @Override
    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            for (Integer id : epics.keySet()) {
                historyManager.removeFromHistory(id);
            }

            subtasks.clear();
            epics.clear();
        } else {
            System.out.println("Список эпиков пуст");
        }
    }

    @Override
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskIds();
            updateEpicStatus(epic.getId());
        }

        for (Integer id : subtasks.keySet()) {
            historyManager.removeFromHistory(id);
        }

        subtasks.clear();
        System.out.println("Список подзадач пуст");
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId());

        if (existingEpic != null) {
            existingEpic.setTitle(epic.getTitle());
            existingEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (subtasks.get(subtask.getId()).getEpicId() == subtask.getEpicId()) {
                subtasks.put(subtask.getId(), subtask);
                updateEpicStatus(subtask.getEpicId());
            }
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    private void updateEpicStatus(int id) {
        int statusNew = 0;
        int statusDone = 0;

        ArrayList<Integer> subtasksList = epics.get(id).getSubtaskIds();
        for (Integer taskId : subtasksList) {
            if (subtasks.get(taskId).getStatus().equals(TaskStatus.DONE)) {
                statusDone++;
            } else if (subtasks.get(taskId).getStatus().equals(TaskStatus.NEW)) {
                statusNew++;
            }
        }
        if (subtasksList.size() == statusNew  || subtasksList.isEmpty()) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else if (subtasksList.size() == statusDone) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}



