package service;

import java.util.List;

import issues.*;

public interface HistoryManager {

    void add(Task task, int id);

    void removeTaskFromHistory(Task task, int id);

    List<Task> getHistory();
}
