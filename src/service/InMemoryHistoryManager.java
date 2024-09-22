package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {


    private final LinkedListCustom history = new LinkedListCustom();

    @Override
    public void addInHistory(Task task) {
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void removeFromHistory(int id) {
        history.remove(id);
    }
}
