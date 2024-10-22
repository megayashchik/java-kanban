package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    private String title;
    private String description;
    private int id;
    private TaskStatus status = TaskStatus.NEW;
    private LocalDateTime startTime;
    private Duration duration = Duration.ZERO;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(String title, String description, int id, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, String description, int id, TaskStatus status, LocalDateTime startTime,
                Duration duration) {
        this.title = title;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getStartTimeToString() {
        if (startTime == null) {
            return "null";
        }
        return startTime.format(DATE_TIME_FORMATTER);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plusMinutes(duration.toMinutes());
    }

    public String getEndTimeToString() {
        if (startTime == null) {
            return "null";
        }
        return getEndTime().format(DATE_TIME_FORMATTER);
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public long getDurationToMinutes() {
        return duration.toMinutes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) &&
                status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status);
    }

    @Override
    public String toString() {
        return title + ", "
                + description
                + ", id: " + id
                + ",статус " + status
                + ", начало: " + getStartTime()
                + ", конец: " + getEndTimeToString()
                + ", продолжительность: " + duration.toMinutes();
    }
}

