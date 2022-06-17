package service;

public class Managers {

    public static  TaskManager getDefault(String filePath) {
        return new FileBackedTasksManager(filePath);
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}


