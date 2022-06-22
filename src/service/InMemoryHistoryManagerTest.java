package service;

import issues.Epic;
import issues.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InMemoryHistoryManagerTest {

    private InMemoryTaskManager manager;

    public void createTestingInstance() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("First task", "Very first task");
        Task task2 = new Task("Second task", "Another one");
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        this.manager = (InMemoryTaskManager) taskManager;
    }

    @Test
    public void testingAddingHistoryEventToHistoryAndRemovingOfEventsFromHistory() {
        createTestingInstance();
        manager.getIssueById(1);
        manager.getIssueById(1);
        manager.getIssueById(1);
        Assertions.assertEquals(1, manager.historyManager.getHistory().size(), "History has not been saved correctly");
        manager.deleteIssueById(1);
        Assertions.assertTrue(manager.historyManager.getHistory().isEmpty(), "Events were not deleted correctly");
        manager.getIssueById(1);
        manager.getIssueById(2);
        manager.getIssueById(3);
        Assertions.assertEquals(2, manager.historyManager.getHistory().size(), "History has not been saved correctly");
        Assertions.assertEquals(2, manager.historyManager.getHistory().get(0).getId(), "History events order is incorrect");
        manager.getIssueById(2);
        manager.getIssueById(2);
        manager.getIssueById(2);
        manager.getIssueById(2);
        Assertions.assertEquals(2, manager.historyManager.getHistory().size(), "History has not been saved correctly");
        Assertions.assertEquals(3, manager.historyManager.getHistory().get(0).getId(), "History events order is incorrect");
    }

    @Test
    public void testingOfRemovingEventsFromHistory() {
        createTestingInstance();
        manager.getIssueById(1);
        manager.getIssueById(2);
        manager.getIssueById(3);
        manager.getIssueById(4);
        manager.deleteIssueById(1);
        Assertions.assertEquals(2, manager.historyManager.getHistory().get(0).getId(), "History events order is incorrect");
        manager.deleteIssueById(2);
        Assertions.assertEquals(3, manager.historyManager.getHistory().get(0).getId(), "History events order is incorrect");
        manager.deleteIssueById(3);
        manager.deleteIssueById(4);
        Assertions.assertTrue(manager.historyManager.getHistory().isEmpty(), "Events were not deleted correctly");
    }

    @Test
    public void testingOfgettingOfHistory() {
        createTestingInstance();
        manager.getIssueById(1);
        manager.getIssueById(2);
        manager.getIssueById(3);
        Assertions.assertNotNull(manager.historyManager.getHistory(), "History was not saved");
        Assertions.assertEquals(3, manager.historyManager.getHistory().size(), "History was not saved correctly");
    }
}
