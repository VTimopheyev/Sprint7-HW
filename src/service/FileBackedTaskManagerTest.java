package service;

import issues.Epic;
import issues.Subtask;
import issues.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.Files.exists;
import static service.FileBackedTasksManager.loadFromFile;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    public void createAllTestingInstances() {
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
        this.manager = (FileBackedTasksManager) taskManager;
    }

    public void createOnlyManagerInstance() {
        TaskManager taskManager = Managers.getDefault("sources/test.csv");
        this.manager = (FileBackedTasksManager) taskManager;
    }

    @Test
    public void testingSavingManagerToFile() {
        createAllTestingInstances();
        Path targetFile = Paths.get(manager.getFilePath());
        Assertions.assertTrue(exists(targetFile), "Target file was not created");
    }

    @Test
    public void testingLoadingManagerInstanceFromCsvFile() {
        createAllTestingInstances();
        manager.getIssueById(1);
        manager.getIssueById(2);
        manager.getIssueById(3);
        FileBackedTasksManager newManager = loadFromFile("sources/test.csv");
        Assertions.assertEquals(2, newManager.getTasks().size(), "FileBackedManager was not created");
        Assertions.assertEquals(3, newManager.historyManager.getHistory().size(), "History has not been saved correctly");
    }
}
