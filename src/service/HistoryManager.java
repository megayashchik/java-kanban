package service;

import model.Task;

import java.util.List;

public interface HistoryManager {

    void addInHistory(Task task);

    void removeFromHistory(int id);

    List<Task> getHistory();
}
