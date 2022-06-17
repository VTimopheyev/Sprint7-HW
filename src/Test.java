import issues.*;
import service.*;

public class Test {

    TaskManager taskManager;

    public void loadingFromFile() {
        taskManager = FileBackedTasksManager.loadFromFile("sources/test.csv");
    }

    public void createIssues() {

        TaskManager taskManager = Managers.getDefault("sources/test.csv");
        Task task1 = new Task("First task", "Very first task");
        Task task2 = new Task("Second task", "Another one");
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", epic1.getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", epic1.getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", epic2.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        this.taskManager = taskManager;
        System.out.println(taskManager);
        System.out.println(taskManager.getTasks());
    }

    public void updateIssues() {

        Task updatedTask = taskManager.getTasks().get(2);
        updatedTask.setStatus(IssueStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Epic updatedEpic;
        updatedEpic = (Epic) taskManager.getIssueById(3);
        updatedEpic.setDescription("New description");
        taskManager.updateEpic(updatedEpic);


        Subtask updatedSubtask;
        updatedSubtask = (Subtask) taskManager.getIssueById(5);
        updatedSubtask.setStatus(IssueStatus.DONE);
        taskManager.updateSubtask(updatedSubtask);

        Subtask updatedSubtask2;
        updatedSubtask2 = (Subtask) taskManager.getIssueById(6);
        updatedSubtask2.setStatus(IssueStatus.DONE);
        taskManager.updateSubtask(updatedSubtask2);

    }

    public void checkHistory() {
        /*System.out.println(taskManager.getHistoryManager().getHistory().size());
        taskManager.getIssueById(5);
        taskManager.getIssueById(2);
        System.out.println(taskManager.getHistoryManager().getHistory().size());
        taskManager.getIssueById(3);
        taskManager.getIssueById(4);
        System.out.println(taskManager.getHistoryManager().getHistory().size());
        System.out.println(taskManager.getHistoryManager().getHistory());*/
        taskManager.getIssueById(5);
        taskManager.getIssueById(2);
        System.out.println(taskManager.getHistoryManager().getHistory().size());
    }

    public void checkDelete() {
        taskManager.deleteIssueById(1);
        taskManager.deleteIssueById(6);
        taskManager.deleteIssueById(5);
    }

    public void printAllIssues() {
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }
}
