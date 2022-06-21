package service;

import issues.Epic;
import issues.Subtask;
import issues.Task;

import java.util.HashMap;
import java.util.TreeMap;

public interface TaskManager {

    HistoryManager getHistoryManager();

    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Epic> getEpics();

    void addTask(Task task);

    void updateTask(Task updatedTask);

    void addEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    void updateEpicStatus(int id);

    boolean checkSubtasksInEpic(int id);

    boolean checkSubtasksAllDoneInEpic(int id);

    boolean checkSubtasksAllNewInEpic(int id);

    void addSubtask(Subtask subtask);

    void updateSubtask(Subtask updatedSubtask);

    Task getIssueById(int id);

    void deleteIssueById(int id);

    void removeAllRelatedSubtasks(int epicId);

    void updateEpicStartTime(int epicId);

    void updateEpicEndTime(int epicId);

    void updateEpicDuration(int epicId);

    TreeMap getPrioritizedIssuesList();

    boolean validateNewDateForTask(Task issueById);

    void refreshPrioritizedIssuesList();
}








