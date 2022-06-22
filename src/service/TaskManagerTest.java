package service;

import issues.Epic;
import issues.Subtask;
import issues.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static issues.IssueStatus.*;


abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    public void createAllTestingInstances() {
        TaskManager taskManager = Managers.getDefault();
        this.manager = (T) taskManager;
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
        this.manager = (T) taskManager;
    }

    public void createOnlyManagerInstance() {
        TaskManager taskManager = Managers.getDefault();
        this.manager = (T) taskManager;
    }


    @Test
    void testGettingHistoryManager() {
        createOnlyManagerInstance();
        Assertions.assertNotNull(manager.getHistoryManager(), "History manager was not created");
    }

    @Test
    void testingTaskCreation() {
        createOnlyManagerInstance();
        Task task1 = new Task("First task", "Very first task");
        Task task2 = new Task("Second task", "Another one");
        manager.addTask(task1);
        manager.addTask(task2);
        Assertions.assertNotNull(manager.getIssueById(1), "Issue was not created");
        Assertions.assertEquals(task1, manager.getIssueById(1), "Issue was not created");
    }

    @Test
    void testUpdateForTask() {
        createAllTestingInstances();
        Task updatedTask = manager.getIssueById(2);
        updatedTask.setStatus(IN_PROGRESS);
        manager.updateTask(updatedTask);
        Assertions.assertEquals(IN_PROGRESS, manager.getIssueById(2).getStatus(), "Issue wasn`t updated correctly");
    }

    @Test
    void testEpicCreation() {
        createOnlyManagerInstance();
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Assertions.assertEquals(epic1, manager.getIssueById(1), "Issue was not created");
    }

    @Test
    void testEpicUpdate() {
        createAllTestingInstances();
        Epic updatedEpic;
        updatedEpic = (Epic) manager.getIssueById(3);
        updatedEpic.setDescription("New description");
        manager.updateEpic(updatedEpic);
        Assertions.assertEquals("New description", manager.getIssueById(3).getDescription(), "Issue wasn`t updated correctly");
    }

    @Test
    void testSubtaskCreation() {
        createOnlyManagerInstance();
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", manager.getIssueById(1).getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", manager.getIssueById(1).getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", manager.getIssueById(2).getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(subtask1, manager.getIssueById(subtask1.getId()), "Issue was not created");
    }

    @Test
    void testSubtaskUpdate() {
        createAllTestingInstances();
        Subtask updatedSubtask;
        updatedSubtask = (Subtask) manager.getIssueById(5);
        updatedSubtask.setStatus(DONE);
        manager.updateSubtask(updatedSubtask);

        Subtask updatedSubtask2;
        updatedSubtask2 = (Subtask) manager.getIssueById(6);
        updatedSubtask2.setStatus(DONE);
        manager.updateSubtask(updatedSubtask2);
        Assertions.assertEquals(DONE, manager.getIssueById(5).getStatus(), "Issue wasn`t updated correctly");
        Assertions.assertEquals(DONE, manager.getIssueById(6).getStatus(), "Issue wasn`t updated correctly");
    }

    @Test
    void testEpicStatusUpdate() {
        createAllTestingInstances();
        manager.getIssueById(5).setStatus(DONE);
        manager.getIssueById(6).setStatus(DONE);
        manager.updateSubtask((Subtask) manager.getIssueById(5));
        manager.updateSubtask((Subtask) manager.getIssueById(6));
        Assertions.assertEquals(DONE, manager.getIssueById(3).getStatus(), "Issue wasn`t updated correctly");
        manager.getIssueById(5).setStatus(NEW);
        manager.getIssueById(6).setStatus(NEW);
        manager.updateSubtask((Subtask) manager.getIssueById(5));
        manager.updateSubtask((Subtask) manager.getIssueById(6));
        Assertions.assertEquals(NEW, manager.getIssueById(3).getStatus(), "Issue wasn`t updated correctly");
        manager.getIssueById(5).setStatus(IN_PROGRESS);
        manager.updateSubtask((Subtask) manager.getIssueById(5));
        Assertions.assertEquals(IN_PROGRESS, manager.getIssueById(3).getStatus(), "Issue wasn`t updated correctly");
        manager.deleteIssueById(5);
        manager.deleteIssueById(6);
        Assertions.assertEquals(NEW, manager.getIssueById(3).getStatus(), "Issue wasn`t updated correctly");
    }

    @Test
    void testCheckingSubtasksInEpic() {
        createAllTestingInstances();
        manager.deleteIssueById(5);
        manager.deleteIssueById(6);
        Assertions.assertTrue(manager.checkSubtasksInEpic(4), "Checking for subtasks in epic working wrong");
        Assertions.assertFalse(manager.checkSubtasksInEpic(3), "Checking for subtasks in epic working wrong");
    }

    @Test
    void testCheckingSubtasksAllDoneInEpic() {
        createAllTestingInstances();
        manager.getIssueById(5).setStatus(DONE);
        manager.getIssueById(6).setStatus(DONE);
        manager.updateSubtask((Subtask) manager.getIssueById(5));
        manager.updateSubtask((Subtask) manager.getIssueById(6));
        Assertions.assertTrue(manager.checkSubtasksAllDoneInEpic(3), "Checking for subtasks all done in epic working wrong");
    }

    @Test
    void testCheckingSubtasksAllNewInEpic() {
        createAllTestingInstances();
        manager.getIssueById(7).setStatus(NEW);
        Assertions.assertTrue(manager.checkSubtasksInEpic(4), "Checking for subtasks all new in epic working wrong");
    }

    @Test
    void testGettingIssueById() {
        createAllTestingInstances();
        Assertions.assertNotNull(manager.getIssueById(1), "Got no issue returned");
    }

    @Test
    void testRemovingIssueById() {
        createAllTestingInstances();
        manager.deleteIssueById(1);
        Assertions.assertNull(manager.getIssueById(1), "Removing of issue was not successful");
    }

    @Test
    void testRemovingAllRelatedSubtasks() {
        createAllTestingInstances();
        manager.deleteIssueById(4);
        Assertions.assertNull(manager.getIssueById(7), "Removing of related subtasks was not successful");
    }

    @Test
    void checkDateValidationForNewIssue() {
        createOnlyManagerInstance();

        Instant now = Instant.now();
        Instant previousYear = Instant.ofEpochMilli(1622613600000L);
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");

        ZonedDateTime timeAboutNow = ZonedDateTime.ofInstant(now, moscowZone);
        ZonedDateTime timeLastWeek = ZonedDateTime.ofInstant(now, moscowZone).minusHours(168L);
        ZonedDateTime timeYesterday = ZonedDateTime.ofInstant(now, moscowZone).minusHours(24L);
        ZonedDateTime timeLastYear = ZonedDateTime.ofInstant(previousYear, moscowZone);

        Task task1 = new Task("First task", "Very first task");
        task1.setStartDate(timeAboutNow);
        manager.addTask(task1);

        Task task2 = new Task("Second task", "Another one");
        task2.setStartDate(timeAboutNow);
        manager.addTask(task2);
        Assertions.assertEquals(1, manager.getPrioritizedIssuesList().size(), "Validation of new date working wrong");

        task2.setIssueDuration(Duration.ofHours(24L));
        manager.addTask(task2);
        Assertions.assertEquals(1, manager.getPrioritizedIssuesList().size(), "Validation of new date working wrong");

        task1.setIssueDuration(Duration.ofHours(24L));
        manager.addTask(task2);
        Assertions.assertEquals(1, manager.getPrioritizedIssuesList().size(), "Validation of new date working wrong");

        task2.setStartDate(timeLastWeek);
        manager.addTask(task2);
        Assertions.assertEquals(2, manager.getPrioritizedIssuesList().size(), "Validation of new date working wrong");

        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", epic1.getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", epic1.getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", epic2.getId());
        subtask1.setStartDate(timeYesterday);
        subtask2.setStartDate(timeLastYear);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(5, manager.getPrioritizedIssuesList().size(), "Validation of new date working wrong");

    }

    @Test
    void checkingEpicDurationAndEndDateCalculation() {
        createOnlyManagerInstance();

        Instant now = Instant.now();
        Instant previousYear = Instant.ofEpochMilli(1622613600000L);
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");

        ZonedDateTime timeLastWeek = ZonedDateTime.ofInstant(now, moscowZone).minusHours(168L);
        ZonedDateTime timeYesterday = ZonedDateTime.ofInstant(now, moscowZone).minusHours(24L);
        ZonedDateTime timeLastYear = ZonedDateTime.ofInstant(previousYear, moscowZone);

        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", epic1.getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", epic1.getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", epic1.getId());
        subtask1.setStartDate(timeYesterday);
        subtask1.setIssueDuration(Duration.ofHours(24L));
        subtask2.setStartDate(timeLastYear);
        subtask2.setIssueDuration(Duration.ofHours(100L));
        subtask3.setStartDate(timeLastWeek);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertEquals(124L, manager.getIssueById(1).getIssueDuration().toHours(), "Calculation of Epic Duration working wrong");
        Assertions.assertEquals(timeLastYear, manager.getIssueById(1).getStartDate(), "Calculation of Epic Start Date working wrong");
        Assertions.assertNotNull(manager.getIssueById(1).getEndTime(), "Calculation of Epic End Date working wrong");
        manager.deleteIssueById(3);
        Assertions.assertNotNull(manager.getIssueById(1).getEndTime(), "Calculation of Epic Start Date working wrong");
        //System.out.println(manager.getIssueById(1));
    }

    @Test
    public void testGettingPrioritizedList() {
        createOnlyManagerInstance();
        Instant now = Instant.now();
        Instant previousYear = Instant.ofEpochMilli(1622613600000L);
        ZoneId moscowZone = ZoneId.of("Europe/Moscow");
        ZonedDateTime timeLastWeek = ZonedDateTime.ofInstant(now, moscowZone).minusHours(168L);
        ZonedDateTime timeYesterday = ZonedDateTime.ofInstant(now, moscowZone).minusHours(24L);
        ZonedDateTime timeLastYear = ZonedDateTime.ofInstant(previousYear, moscowZone);
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", epic1.getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", epic1.getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", epic1.getId());
        subtask1.setStartDate(timeYesterday);
        subtask1.setIssueDuration(Duration.ofHours(24L));
        subtask2.setStartDate(timeLastYear);
        subtask2.setIssueDuration(Duration.ofHours(100L));
        subtask3.setStartDate(timeLastWeek);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);
        Assertions.assertNotNull(manager.getPrioritizedIssuesList(), "No list been created");
        Assertions.assertEquals(3, manager.getPrioritizedIssuesList().size(), "Saving to list is incorrect");
        //System.out.println(manager.getPrioritizedIssuesList());
    }
}