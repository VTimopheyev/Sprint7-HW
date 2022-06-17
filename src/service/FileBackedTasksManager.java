package service;

import issues.*;

import java.io.*;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static issues.IssueType.*;


public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final String filePath;


    public FileBackedTasksManager(String filePath) {
        super();
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    //id,type,name,status,description,startDate,duration,endDate,epic
    private String issueToString(Task task) {
        StringBuilder sb = new StringBuilder();
        String issueAsString = null;
        if (task.getType() == TASK) {
            sb.append(task.getId() + "," + task.getType() + "," + task.getTitle() + "," +
                    task.getStatus() + "," + task.getDescription() +
                    "," + getDateAsString(task.getStartDate()) + "," + getDurationAsLong(task.getIssueDuration()) +
                    "," + getDateAsString(task.getEndTime()));
            issueAsString = sb.toString();
            return issueAsString;
        } else if (task.getType() == SUBTASK) {
            Subtask subtask = (Subtask) task;
            sb.append(subtask.getId() + "," + subtask.getType() + "," + subtask.getTitle() + "," +
                    subtask.getStatus() + "," + subtask.getDescription() +
                    "," + getDateAsString(subtask.getStartDate()) + "," +
                    getDurationAsLong(subtask.getIssueDuration()) + "," + getDateAsString(subtask.getStartDate()) +
                    "," + subtask.getEpicId());
            issueAsString = sb.toString();
            return issueAsString;
        } else if (task.getType() == EPIC) {
            Epic epic = (Epic) task;
            sb.append(epic.getId() + "," + epic.getType() + "," + epic.getTitle() + "," + epic.getStatus() +
                    "," + epic.getDescription() + "," + getDateAsString(epic.getStartDate()) +
                    "," + getDurationAsLong(epic.getIssueDuration()) + "," + getDateAsString(epic.getStartDate()));
            issueAsString = sb.toString();
            return issueAsString;
        }
        return null;
    }

    public void save() {

        StringBuilder sb = new StringBuilder();
        String history;
        history = historyToString(historyManager);
        sb.append("id,type,name,status,description,startDate,duration,endDate,epic");
        sb.append("\n");

        try {
            for (Task task : tasks.values()) {
                sb.append(issueToString(task));
                sb.append("\n");
            }
            for (Epic epic : epics.values()) {
                sb.append(issueToString(epic));
                sb.append("\n");
            }
            for (Subtask subtask : subtasks.values()) {
                sb.append(issueToString(subtask));
                sb.append("\n");
            }
            sb.append("\n");

            sb.append(history);


            FileWriter fw = new FileWriter(filePath);
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private String getDateAsString(ZonedDateTime date) {
        if (date == null) {
            return null;
        } else {
            return date.toString();
        }
    }

    private Long getDurationAsLong(Duration duration) {
        if (duration == null) {
            return null;
        } else {
            return duration.toHours();
        }
    }

    public static FileBackedTasksManager loadFromFile(String filePath) {
        FileBackedTasksManager tasksManager = new FileBackedTasksManager(filePath);
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            reader.readLine();
            String line;
            String lineWithHistory;

            while (true) {
                line = reader.readLine();
                if (line.length() == 0) {
                    lineWithHistory = reader.readLine();
                    tasksManager.restoreHistory(lineWithHistory);
                    break;
                }
                tasksManager.stringToIssue(line);
            }
            reader.close();
            tasksManager.restoreIdCount();
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return tasksManager;
    }

    //"id,type,name,status,description,startDate,duration,endDate,epic"
    private void stringToIssue(String line) {
        String[] splittedLine = line.split(",");
        IssueType type = checkType(splittedLine[1]);
        IssueStatus status = checkStatus(splittedLine[3]);
        Duration issueDuration = restoreDuration(splittedLine[6]);
        ZonedDateTime issueStartDate = restoreDate(splittedLine[5]);
        ZonedDateTime issueEndTime = restoreDate(splittedLine[7]);

        //"id,type,name,status,description,startDate,duration,endDate,epic"
        int id = Integer.parseInt(splittedLine[0]);
        switch (type) {
            case TASK:
                Task task = new Task(id, type, splittedLine[2], status, splittedLine[4],
                        issueStartDate, issueDuration);
                tasks.put(id, task);
                break;
            case EPIC:
                Epic epic = new Epic(id, type, splittedLine[2], status, splittedLine[4],
                        issueStartDate, issueDuration, issueEndTime);
                epics.put(id, epic);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(splittedLine[8]);
                Subtask subtask = new Subtask(id, type, splittedLine[2], status, splittedLine[4],
                        issueStartDate, issueDuration, epicId);
                subtasks.put(id, subtask);
                break;
        }
    }

    private IssueType checkType(String value) {
        IssueType type = null;
        switch (value) {
            case "TASK":
                type = TASK;
                break;
            case "SUBTASK":
                type = SUBTASK;
                break;
            case "EPIC":
                type = EPIC;
                break;
        }
        return type;
    }

    private IssueStatus checkStatus(String value) {
        IssueStatus status = IssueStatus.NONE;
        switch (value) {
            case "IN_PROGRESS":
                status = IssueStatus.IN_PROGRESS;
                break;
            case "DONE":
                status = IssueStatus.DONE;
                break;
            case "NEW":
                status = IssueStatus.NEW;
                break;
        }
        return status;
    }

    private Duration restoreDuration(String line) {
        if (line.equals("null")) {
            return null;
        } else {
            return Duration.ofHours(Long.parseLong(line));
        }
    }

    private ZonedDateTime restoreDate(String line) {
        if (line.equals("null")) {
            return null;
        } else {
            return ZonedDateTime.parse(line);
        }
    }

    private void restoreIdCount() {
        int idCount = 0;
        for (int id : tasks.keySet()) {
            if (id > idCount) {
                idCount = id;
            }
        }
        for (int id : subtasks.keySet()) {
            if (id > idCount) {
                idCount = id;
            }
        }
        for (int id : epics.keySet()) {
            if (id > idCount) {
                idCount = id;
            }
        }
        this.idCount = idCount;
    }

    private void restoreHistory(String lineWithHistory) {
        String[] savedHistory = lineWithHistory.split(",");
        for (String idAsString : savedHistory) {
            int id = Integer.parseInt(idAsString);
            historyManager.add(getIssueById(id), id);
        }
    }


    private String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        List<Task> history = manager.getHistory();
        for (Task task : history) {
            sb.append(task.getId() + ",");
        }
        String historyInLine = sb.toString();
        return historyInLine;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void updateEpicStatus(int id) {
        super.updateEpicStatus(id);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public Task getIssueById(int id) {
        Task task = super.getIssueById(id);
        save();
        return task;
    }

    @Override
    public void deleteIssueById(int id) {
        super.deleteIssueById(id);
        save();
    }

    @Override
    public void removeAllRelatedSubtasks(int epicId) {
        super.removeAllRelatedSubtasks(epicId);
        save();
    }

    ArrayList<Task> getAllIssuesList() {
        ArrayList<Task> list = new ArrayList<>();
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                list.add(task);
            }
        }
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                list.add(epic);
            }
        }
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                list.add(subtask);
            }
        }
        return list;
    }
}

