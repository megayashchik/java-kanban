public class Managers {

    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistoryManager();
        return new InMemoryTaskManager(historyManager);
    }

    private static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
