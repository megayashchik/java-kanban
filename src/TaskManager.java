import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int id = 0;

    private int generateId() {
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
        if (epics.containsKey(subtask.getEpicId())) {
            subtask.setId(generateId());
            epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            updateStatus(subtask.getEpicId());
        } else {
            System.out.println("Такого эпика не существует");
        }
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
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).deleteSubtaskId(id);
            subtasks.remove(id);
            updateStatus(epicId);
        } else {
            System.out.println("Такой подзадачи не существует");
        }
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
            epic.clearSubtaskIds();
            updateStatus(epic.getId());
        }
        subtasks.clear();
        System.out.println("Список подзадач пуст");
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        Epic newEpic = epics.get(epic.getId());
        if (newEpic != null) {
            newEpic.setTitle(epic.getTitle());
            newEpic.setDescription(epic.getDescription());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (subtasks.get(subtask.getId()).getEpicId() == subtask.getEpicId()) {
                subtasks.put(subtask.getId(), subtask);
                updateStatus(subtask.getEpicId());
            }
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    private void updateStatus(int id) {
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
}
