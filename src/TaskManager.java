import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int id = 0;

    public int generateId() {
        return ++id;
    }

    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        epics.get(subtask.getEpicId()).getSubtaskIds().add(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateStatus(subtask.getEpicId());
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Task getTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с " + id + " id не существует");
        }
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Задачи с " + id + " id не существует?");
        }
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        if (!subtasks.containsKey(id)) {
            System.out.println("Задачи с " + id + " id не существует");
        }
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasksByEpicId(int id) {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        ArrayList<Integer> subtaskIds = epics.get(id).getSubtaskIds();
        for (Integer subtaskId : subtaskIds) {
            subtasksList.add(subtasks.get(subtaskId));
        }
        return subtasksList;
    }

    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksIds = epics.get(id).getSubtaskIds();
            for (Integer subtasksId : subtasksIds) {
                subtasks.remove(subtasksId);
            }
            epics.remove(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    public void deleteSubtaskById(int id) {
        int epicId = subtasks.get(id).getEpicId();
        epics.get(epicId).getSubtaskIds().remove(id);
        subtasks.remove(id);
        updateStatus(epicId);
    }

    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        } else {
            System.out.println("Список задач пуст");
        }
    }

    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            subtasks.clear();
            epics.clear();
        } else {
            System.out.println("Список эпиков пуст");
        }
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            updateStatus(epic.getId());
            epics.put(epic.getId(), epic);
        }
        subtasks.clear();
        System.out.println("Список сабтасков пуст");
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epic.setSubtaskIds(epics.get(epic.getId()).getSubtaskIds());
        epic.setStatus(epics.get(epic.getId()).getStatus());
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus(subtask.getEpicId());
    }

    public void updateStatus(int id) {
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
        if (subtasksList.size() == statusDone  || subtasksList.isEmpty()) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else if (subtasksList.size() == statusNew) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
