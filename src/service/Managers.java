package service;

public class Managers {

    public static HttpTaskManager getDefault(String URL)  {
        return new HttpTaskManager(URL);
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}


