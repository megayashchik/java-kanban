package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, int epicId, LocalDateTime localDateTime, Duration duration) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId) {
        super(title, description);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int epicId, TaskStatus status) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, int id, TaskStatus status, int epicId) {
        super(title, description, status);
        this.epicId = epicId;
    }

    public Subtask(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
    }

    public Subtask(String title, String description, int id, TaskStatus status, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(title, description, id, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return super.getTitle()
                + ", " + super.getDescription()
                + ", id: " + super.getId()
                + ", статус:" + super.getStatus()
                + ", продолжительность: " + super.getDuration()
                + ", начало: " + super.getStartTimeToString()
                + ", конец: " + super.getEndTimeToString()
                + ", epicID: " + epicId;
    }
}