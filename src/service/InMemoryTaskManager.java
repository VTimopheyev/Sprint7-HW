package service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

import issues.*;

public class InMemoryTaskManager implements TaskManager {

    protected int idCount;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HistoryManager historyManager;
    protected TreeMap<ZonedDateTime, Integer> prioritizedIssuesList;

    Comparator<ZonedDateTime> priorityComparator = (zonedDateTime, other) -> {
        if (zonedDateTime == null) {
            return 1;
        } else if (other == null) {
            return -1;
        } else if (zonedDateTime.isBefore(other)) {
            return -1;
        }
        return 1;
    };

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedIssuesList = new TreeMap<>(priorityComparator);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public void addTask(Task task) {
        if (validateNewDateForTask(task)) {
            idCount++;
            task.setId(idCount);
            task.setStatus(IssueStatus.NEW);
            tasks.put(task.getId(), task);
            if (task.getStartDate() != null) {
                prioritizedIssuesList.put(task.getStartDate(), task.getId());
            }else {
                prioritizedIssuesList.put(task.getStartDate(), task.getId());
            }
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (validateNewDateForTask(updatedTask)) {
            if (tasks.containsKey(updatedTask.getId())) {
                tasks.put(updatedTask.getId(), updatedTask);
            }
            prioritizedIssuesList.put(updatedTask.getStartDate(), updatedTask.getId());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        idCount++;
        epic.setId(idCount);
        epic.setStatus(IssueStatus.NEW);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {

        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    @Override
    public void updateEpicStatus(int id) {
        boolean subtasksInEpic = checkSubtasksInEpic(id);

        if (subtasksInEpic && checkSubtasksAllDoneInEpic(id)) {
            epics.get(id).setStatus(IssueStatus.DONE);
        } else if (!subtasksInEpic || checkSubtasksAllNewInEpic(id)) {
            epics.get(id).setStatus(IssueStatus.NEW);
        } else if (subtasksInEpic && !checkSubtasksAllDoneInEpic(id)) {
            epics.get(id).setStatus(IssueStatus.IN_PROGRESS);
        }
    }

    public boolean checkSubtasksInEpic(int id) {

        for (int key : subtasks.keySet()) {
            if (subtasks.get(key).getEpicId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean checkSubtasksAllDoneInEpic(int id) {

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id && !(subtask.getStatus()).equals(IssueStatus.DONE)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkSubtasksAllNewInEpic(int id) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == id && !(subtask.getStatus()).equals(IssueStatus.NEW)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (validateNewDateForSubtask(subtask)) {
            idCount++;
            subtask.setId(idCount);
            subtask.setStatus(IssueStatus.NEW);
            subtasks.put(subtask.getId(), subtask);
            if (epics.get(subtask.getEpicId()).getStatus().equals(IssueStatus.DONE)) {
                epics.get(subtask.getEpicId()).setStatus(IssueStatus.IN_PROGRESS);
            }
            updateEpicStartTime(subtask.getEpicId());
            updateEpicDuration(subtask.getEpicId());
            updateEpicEndTime(subtask.getEpicId());
            prioritizedIssuesList.put(subtask.getStartDate(), subtask.getId());;
        }
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (validateNewDateForSubtask(updatedSubtask)) {
            if (subtasks.containsKey(updatedSubtask.getId())) {
                subtasks.put(updatedSubtask.getId(), updatedSubtask);
            }
            updateEpicStatus(updatedSubtask.getEpicId());
            updateEpicStartTime(updatedSubtask.getEpicId());
            updateEpicDuration(updatedSubtask.getEpicId());
            updateEpicEndTime(updatedSubtask.getEpicId());
            prioritizedIssuesList.put(updatedSubtask.getStartDate(), updatedSubtask.getId());
        }
    }

    @Override
    public Task getIssueById(int id) {

        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id), id);
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            historyManager.add(epics.get(id), id);
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id), id);
            return subtasks.get(id);
        }
        return null;
    }

    @Override
    public void deleteIssueById(int id) {

        if (tasks.containsKey(id)) {
            historyManager.removeTaskFromHistory(tasks.get(id), id);
            tasks.remove(id);
            refreshPrioritizedIssuesList();
        } else if (epics.containsKey(id)) {
            historyManager.removeTaskFromHistory(epics.get(id), id);
            epics.remove(id);
            removeAllRelatedSubtasks(id);

        } else if (subtasks.containsKey(id)) {
            int epicId = subtasks.get(id).getEpicId();
            historyManager.removeTaskFromHistory(subtasks.get(id), id);
            subtasks.remove(id);
            updateEpicStatus(epicId);
            updateEpicStartTime(epicId);
            updateEpicDuration(epicId);
            updateEpicEndTime(epicId);
            refreshPrioritizedIssuesList();
        }
    }

    @Override
    public void removeAllRelatedSubtasks(int epicId) {
        ArrayList<Integer> subtasksToDelete = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                subtasksToDelete.add(subtask.getId());
            }
        }
        for (Integer id : subtasksToDelete) {
            historyManager.removeTaskFromHistory(subtasks.get(id), id);
            subtasks.remove(id);
        }
    }

    @Override
    public void updateEpicStartTime(int epicId) {
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                if (subtask.getStartDate() != null) {
                    if (epics.get(epicId).getStartDate() == null || subtask.getStartDate().isBefore(epics.get(epicId).getStartDate())) {
                        epics.get(epicId).setStartDate(subtask.getStartDate());
                    }
                }
            }
        }
    }

    @Override
    public void updateEpicDuration(int epicId) {
        epics.get(epicId).setIssueDuration(null);
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                if (subtask.getIssueDuration() != null) {
                    if (epics.get(epicId).getIssueDuration() == null) {
                        epics.get(epicId).setIssueDuration(subtask.getIssueDuration());
                    } else {
                        Duration increasedDuration = epics.get(epicId).getIssueDuration().plus(subtask.getIssueDuration());
                        epics.get(epicId).setIssueDuration(increasedDuration);
                    }
                }
            }
        }
    }

    @Override
    public void updateEpicEndTime(int epicId) {
        ArrayList<Subtask> subtasksOfEpic = new ArrayList<>();
        if (checkSubtasksInEpic(epicId)) {
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epicId) {
                    if (subtask.getStartDate() != null) {
                        subtasksOfEpic.add(subtask);
                    }
                }
            }
        }
        if(!subtasksOfEpic.isEmpty()) {
            epics.get(epicId).setEpicEndTime(setEndDateForTopic(subtasksOfEpic, epicId));
        }
    }

    private ZonedDateTime setEndDateForTopic(ArrayList<Subtask> list, int EpicId) {
        ZonedDateTime latestOne = epics.get(EpicId).getStartDate();
        Subtask targetSubtask = list.get(0);
        for (Subtask subtask : list) {
            if (subtask.getStartDate().isAfter(latestOne)) {
                latestOne = subtask.getStartDate();
                targetSubtask = subtask;
            }
        }
        if (targetSubtask.getIssueDuration() == null) {
            return latestOne;
        } else {
            return targetSubtask.getEndTime();
        }
    }

    private Optional<ZonedDateTime> checkDateForNull(Object o) {
        String type = String.valueOf(o.getClass());
        if (type.equals("class issues.Task")) {
            Task task = (Task) o;
            if (task.getStartDate() == null) {
                return Optional.empty();
            } else {
                return Optional.of(task.getStartDate());
            }
        } else if (type.equals("class issues.Subtask")) {
            Subtask subtask = (Subtask) o;
            if (subtask.getStartDate() == null) {
                return Optional.empty();
            } else {
                return Optional.of(subtask.getStartDate());
            }
        }
        return Optional.empty();
    }

    private Optional<Duration> checkDurationForNull(Object o) {
        String type = String.valueOf(o.getClass());
        if (type.equals("class issues.Task")) {
            Task task = (Task) o;
            if (task.getIssueDuration() == null) {
                return Optional.empty();
            } else {
                return Optional.of(task.getIssueDuration());
            }
        } else if (type.equals("class issues.Subtask")) {
            Subtask subtask = (Subtask) o;
            if (subtask.getIssueDuration() == null) {
                return Optional.empty();
            } else {
                return Optional.of(subtask.getIssueDuration());
            }
        }
        return Optional.empty();
    }

    private Optional<ZonedDateTime> checkEndDateForNull(Object o) {
        String type = String.valueOf(o.getClass());
        if (type.equals("class issues.Task")) {
            Task task = (Task) o;
            if (task.getStartDate() != null && task.getIssueDuration() != null) {
                return Optional.of(task.getStartDate().plus(task.getIssueDuration()));
            } else {
                return Optional.empty();
            }
        } else if (type.equals("class issues.Subtask")) {
            Subtask subtask = (Subtask) o;
            if (subtask.getStartDate() != null && subtask.getIssueDuration() != null) {
                return Optional.of(subtask.getStartDate().plus(subtask.getIssueDuration()));
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean validateNewDateForTask(Task task) {
        Optional<ZonedDateTime> maybeTargetStartDate = checkDateForNull(task);
        Optional<Duration> maybeTargetDuration = checkDurationForNull(task);
        Optional<ZonedDateTime> maybeTargetEndTime = checkEndDateForNull(task);
        Optional<ZonedDateTime> maybeAnotherStartDate;
        Optional<Duration> maybeAnotherDuration;
        Optional<ZonedDateTime> maybeAnotherEndTime;


        if (prioritizedIssuesList.size() == 0 || maybeTargetStartDate.isEmpty()) {
            return true;
        } else if (maybeTargetDuration.isEmpty()) {

            for (Integer id : prioritizedIssuesList.values()) {
                String type = String.valueOf(getInstanceById(id).getClass());
                switch (type) {
                    case "class issues.Task":
                        maybeAnotherStartDate = checkDateForNull(tasks.get(id));
                        if (maybeAnotherStartDate.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            }
                        }
                        break;
                    case "class issues.Subtask":
                        maybeAnotherStartDate = checkDateForNull(subtasks.get(id));
                        if (maybeAnotherStartDate.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            }
                        }
                        break;
                }
            }
        } else if (maybeTargetDuration.isPresent()) {
            for (Integer id : prioritizedIssuesList.values()) {
                String type = String.valueOf(getInstanceById(id).getClass());
                switch (type) {
                    case "class issues.Task":
                        maybeAnotherStartDate = checkDateForNull(tasks.get(id));
                        maybeAnotherDuration = checkDurationForNull(tasks.get(id));
                        maybeAnotherEndTime = checkEndDateForNull(tasks.get(id));
                        if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            } else if (maybeTargetStartDate.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetStartDate.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeTargetEndTime.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetEndTime.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            }
                        } else if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isEmpty()) {
                            if (maybeTargetStartDate.get().isEqual(maybeAnotherStartDate.get())) {
                                return false;
                            }
                        }
                        break;
                    case "class issues.Subtask":
                        maybeAnotherStartDate = checkDateForNull(subtasks.get(id));
                        maybeAnotherDuration = checkDurationForNull(subtasks.get(id));
                        maybeAnotherEndTime = checkEndDateForNull(subtasks.get(id));
                        if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            } else if (maybeTargetStartDate.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetStartDate.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeTargetEndTime.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetEndTime.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isEmpty()) {
                                if (maybeTargetStartDate.get().isEqual(maybeAnotherStartDate.get())) {
                                    return false;
                                }
                            }

                        }
                        break;
                }
            }
        }
        return true;
    }

    public boolean validateNewDateForSubtask(Subtask subtask) {
        Optional<ZonedDateTime> maybeTargetStartDate = checkDateForNull(subtask);
        Optional<Duration> maybeTargetDuration = checkDurationForNull(subtask);
        Optional<ZonedDateTime> maybeTargetEndTime = checkEndDateForNull(subtask);
        Optional<ZonedDateTime> maybeAnotherStartDate;
        Optional<Duration> maybeAnotherDuration;
        Optional<ZonedDateTime> maybeAnotherEndTime;

        if (prioritizedIssuesList.size() == 0 || maybeTargetStartDate.isEmpty()) {
            return true;
        } else if (maybeTargetDuration.isEmpty()) {
            for (Integer id : prioritizedIssuesList.values()) {
                String type = String.valueOf(getInstanceById(id).getClass());
                switch (type) {
                    case "class issues.Task":
                        maybeAnotherStartDate = checkDateForNull(tasks.get(id));
                        if (maybeAnotherStartDate.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            }
                        }
                        break;
                    case "class issues.Subtask":
                        maybeAnotherStartDate = checkDateForNull(subtasks.get(id));
                        if (maybeAnotherStartDate.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            }
                        }
                        break;
                }
            }
        } else if (maybeTargetDuration.isPresent()) {
            for (Integer id : prioritizedIssuesList.values()) {
                String type = String.valueOf(getInstanceById(id).getClass());
                switch (type) {
                    case "class issues.Task":
                        maybeAnotherStartDate = checkDateForNull(tasks.get(id));
                        maybeAnotherDuration = checkDurationForNull(tasks.get(id));
                        maybeAnotherEndTime = checkEndDateForNull(tasks.get(id));
                        if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            } else if (maybeTargetStartDate.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetStartDate.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeTargetEndTime.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetEndTime.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isEmpty()) {
                                if (maybeTargetStartDate.get().isEqual(maybeAnotherStartDate.get())) {
                                    return false;
                                }
                            }
                        }
                        break;
                    case "class issues.Subtask":
                        maybeAnotherStartDate = checkDateForNull(subtasks.get(id));
                        maybeAnotherDuration = checkDurationForNull(subtasks.get(id));
                        maybeAnotherEndTime = checkEndDateForNull(subtasks.get(id));
                        if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isPresent()) {
                            if (maybeAnotherStartDate.get().isEqual(maybeTargetStartDate.get())) {
                                return false;
                            } else if (maybeTargetStartDate.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetStartDate.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeTargetEndTime.get().isAfter(maybeAnotherStartDate.get()) &&
                                    maybeTargetEndTime.get().isBefore(maybeAnotherEndTime.get())) {
                                return false;
                            } else if (maybeAnotherStartDate.isPresent() && maybeAnotherDuration.isEmpty()) {
                                if (maybeTargetStartDate.get().isEqual(maybeAnotherStartDate.get())) {
                                    return false;
                                }
                            }
                        }
                        break;
                }
            }
        }
        return true;
    }

    private void refreshPrioritizedIssuesList() {
        Comparator<ZonedDateTime> priorityComparator = (zonedDateTime, other) -> {
            if (zonedDateTime == null) {
                return 1;
            } else if (other == null) {
                return -1;
            } else if (zonedDateTime.isBefore(other)) {
                return -1;
            }
            return 1;
        };

        TreeMap<ZonedDateTime, Integer> prioritizedIssues = new TreeMap(priorityComparator);
        for (Task task : tasks.values()) {
            prioritizedIssues.put(task.getStartDate(), task.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            prioritizedIssues.put(subtask.getStartDate(), subtask.getId());
        }
        this.prioritizedIssuesList = prioritizedIssues;
    }

    private Task getInstanceById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        }
        return null;
    }

    public TreeMap<ZonedDateTime, Integer> getPrioritizedIssuesList() {
        return prioritizedIssuesList;
    }
}

