import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAX_SIZE_OF_HISTORY = 10;

    private final List<Task> history = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (history.size() >= MAX_SIZE_OF_HISTORY) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
