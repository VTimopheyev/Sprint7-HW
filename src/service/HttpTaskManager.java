package service;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {

    public KVTaskClient client;


    public HttpTaskManager(String URL) {
        super(URL);
        this.client = new KVTaskClient(URL);
    }

    @Override
    public void save() {
        client.saveTasksToServer(tasks);
        client.saveEpicsToServer(epics);
        client.saveSubtasksToServer(subtasks);
        client.saveHistoryToServer(historyManager.getHistory());
    }

    public HttpTaskManager load(String url) {
        return client.loadManagerFromServer(url);
    }
}

