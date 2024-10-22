package service;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();

    protected HistoryManager historyManager = Managers.getDefaultHistoryManager();

    protected int id = 0;

    static final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId);
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    private int generateId() {
        return ++id;
    }

    @Override
    public void createTask(Task task) {
        Optional<Task> intersectingTask = checkTimeIntersection(task);
        if (intersectingTask.isPresent()) {
            throw new ManagerSaveException(task.getTitle()
                    + " пресекается с другой задачей по времени или продолжительности");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
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
            Optional<Task> intersectingTask = checkTimeIntersection(subtask);
            if (intersectingTask.isPresent()) {
                throw new ManagerSaveException(subtask.getTitle()
                        + " пресекается с другой задачей по времени или продолжительности");
            }
            epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            updateEpicStatus(subtask.getEpicId());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    private void updateIdAfterLoad(int loadedId) {
        if (loadedId > id) {
            id = loadedId;
        }
    }

    protected void addTask(Task task) {
        tasks.put(task.getId(), task);
        updateIdAfterLoad(task.getId());
    }

    protected void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateIdAfterLoad(epic.getId());
    }

    protected void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateIdAfterLoad(subtask.getId());

        if (epics.containsKey(subtask.getId())) {
            epics.get(subtask.getEpicId()).addSubtaskId(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public List<Task> getTasks() {
        return tasks.values().stream().toList();
    }

    @Override
    public List<Epic> getEpics() {
        return epics.values().stream().toList();
    }

    @Override
    public List<Subtask> getSubtasks() {
        return subtasks.values().stream().toList();
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
            System.out.println("Задачи с " + id + " id не существует");
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
    public List<Subtask> getSubtasksByEpicId(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return Collections.emptyList();
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            historyManager.removeFromHistory(id);
            tasks.remove(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> subtasksIds = epics.get(id).getSubtaskIds();
            for (Integer subtasksId : subtasksIds) {
                prioritizedTasks.remove(subtasks.get(subtasksId));
                historyManager.removeFromHistory(subtasksId);
                subtasks.remove(subtasksId);
            }
            historyManager.removeFromHistory(id);
            epics.remove(id);
        } else {
            System.out.println("Задачи с " + id + " id не существует");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            epics.get(epicId).deleteSubtaskId(id);
            prioritizedTasks.remove(subtasks.get(id));
            historyManager.removeFromHistory(id);
            subtasks.remove(id);
            recalculateEpicStatus(epics.get(epicId));
        } else {
            System.out.println("Такой подзадачи не существует");
        }
    }

    @Override
    public void deleteAllTasks() {
        if (!tasks.isEmpty()) {
            for (Integer id : tasks.keySet()) {
                historyManager.removeFromHistory(id);
                prioritizedTasks.remove(tasks.get(id));
            }
            tasks.clear();
        } else {
            System.out.println("Список задач пуст");
        }
    }

    @Override
    public void deleteAllEpics() {
        if (!epics.isEmpty()) {
            for (Integer epicId : epics.keySet()) {
                List<Integer> subtasksIds = epics.get(epicId).getSubtaskIds();
                for (Integer subtasksId : subtasksIds) {
                    prioritizedTasks.remove(subtasks.get(subtasksId));
                    historyManager.removeFromHistory(subtasksId);
                    subtasks.remove(subtasksId);
                }
                historyManager.removeFromHistory(epicId);
            }
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
            prioritizedTasks.remove(subtasks.get(id));
            historyManager.removeFromHistory(id);
        }
        subtasks.clear();
        System.out.println("Список подзадач пуст");
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            Optional<Task> intersectingTask = checkTimeIntersection(task);
            if (intersectingTask.isPresent()) {
                throw new ManagerSaveException("Обновление задачи " + task.getTitle()
                        + " пересекается по времени с задачей " + intersectingTask.get().getTitle());
            }
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.removeIf(prioritizedTask -> prioritizedTask.getId() == task.getId());
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existingEpic = epics.get(epic.getId());
        if (existingEpic != null) {
            existingEpic.setTitle(epic.getTitle());
            existingEpic.setDescription(epic.getDescription());
            updateEpicStatus(epic.getId());
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();

        if (!subtasks.containsKey(id) || checkTimeIntersection(subtask).isPresent()) {
            return;
        }

        if (subtasks.get(id).getEpicId() == subtask.getEpicId()) {
            prioritizedTasks.remove(subtasks.get(id));
            prioritizedTasks.add(subtask);
            subtasks.put(id, subtask);
            recalculateEpicStatus(epics.get(subtask.getEpicId()));
        }
    }

    private void updateEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            System.out.println("Эпика с id " + id + " не существует.");
            return;
        }

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        Map<TaskStatus, Long> statusCount = epics.get(id).getSubtaskIds().stream()
                .map(subtasks::get)
                .collect(Collectors.groupingBy(Subtask::getStatus, Collectors.counting()));

        long statusNew = statusCount.getOrDefault(TaskStatus.NEW, 0L);
        long statusDone = statusCount.getOrDefault(TaskStatus.DONE, 0L);
        long total = epics.get(id).getSubtaskIds().size();

        if (total == statusNew || total == 0) {
            epics.get(id).setStatus(TaskStatus.NEW);
        } else if (total == statusDone) {
            epics.get(id).setStatus(TaskStatus.DONE);
        } else {
            epics.get(id).setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void recalculateEpicStatus(Epic epic) {
        List<Subtask> subtaskList = getSubtasksByEpicId(epic.getId());

        if (subtaskList.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(Duration.ZERO);
            return;
        }

        long statusInProgress = subtaskList.stream()
                .filter(subtask -> subtask.getStatus() == TaskStatus.IN_PROGRESS).count();
        long statusDone = subtaskList.stream()
                .filter(subtask -> subtask.getStatus() == TaskStatus.DONE).count();

        if (statusInProgress == subtaskList.size()) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (statusDone == subtaskList.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.NEW); // проверить ещё
        }

        LocalDateTime startTime = subtaskList.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull) // изменено
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime endTime = subtaskList.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull) // изменено
                .max(LocalDateTime::compareTo)
                .orElse(null);
        Duration duration = subtaskList.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);
        epic.setDuration(duration);
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            if (task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    private Optional<Task> checkTimeIntersection(Task task) {
        return getPrioritizedTasks().stream()
                .filter(existingTask -> task.getStartTime() != null && existingTask.getStartTime() != null)
                .filter(prioritizedTask -> task.getId() != prioritizedTask.getId())
                .filter(prioritizedTask -> isOverlapping(task, prioritizedTask))
                .findFirst();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }
}



