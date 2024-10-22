package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
    }

    public Epic(String title, String description, int id, TaskStatus status) {
        super(title, description, id, status);
    }

    public Epic(String title, String description, int id, TaskStatus status, ArrayList<Integer> subtaskIds,
                LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        super(title, description, id, status, startTime, duration);
        this.subtaskIds = subtaskIds;
        this.endTime = endTime;
    }

    public Epic(String name, String description, TaskStatus status, int id) { ///
        super(description, name, status);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return super.getTitle()
                + ", " + super.getDescription()
                + ", id " + super.getId()
                + ", статус " + super.getStatus()
                + ", подзадачи " + subtaskIds
                + ", начало: " + getStartTimeToString()
                + ", конец: " + getEndTimeToString()
                + ", продолжительность: " + super.getDuration();
    }

    @Override
    public String getEndTimeToString() {
        if (endTime == null) {
            return "null";
        }
        return endTime.format(DATE_TIME_FORMATTER);
    }

    public void deleteSubtaskId(int id) {
        subtaskIds.remove(Integer.valueOf(id));
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    public void addSubtaskId(int id) {
        subtaskIds.add(id);
    }
}