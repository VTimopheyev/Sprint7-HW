package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import issues.Epic;
import issues.Subtask;
import issues.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;

public class HttpTaskServerTest {

    HttpTaskServer server;

    @BeforeAll
    public static void startKVServer() {
        KVServer server = null;
        try {
            server = new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
    }

    @AfterEach
    public void serverStop() {
        server.server.stop(0);
    }

    public void createTestInstances() {
        HttpTaskServer taskServer = new HttpTaskServer("http://localhost:8078");
        taskServer.server.start();
        Task task1 = new Task("First task", "Very first task");
        Task task2 = new Task("Second task", "Another one");
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        taskServer.manager.addTask(task1);
        taskServer.manager.addTask(task2);
        taskServer.manager.addEpic(epic1);
        taskServer.manager.addEpic(epic2);
        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", epic1.getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", epic1.getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", epic2.getId());
        taskServer.manager.addSubtask(subtask1);
        taskServer.manager.addSubtask(subtask2);
        taskServer.manager.addSubtask(subtask3);
        taskServer.manager.getIssueById(1);
        taskServer.manager.getIssueById(2);
        taskServer.manager.getIssueById(3);
        this.server = taskServer;
    }

    @Test
    public void checkGettingOfPrioritizedIssuesFromServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String prioritizedIssues = response.body();
            System.out.println(prioritizedIssues);
            Assertions.assertNotNull(prioritizedIssues, "The list of prioritized issues hasn`t been received");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkGettingOfTasksFromServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String task = response.body();
            System.out.println(task);
            Assertions.assertNotNull(task, "Issue hasn`t been received");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkGettingOfSubtasksFromServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask?id=5");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String subtask = response.body();
            System.out.println(subtask);
            Assertions.assertNotNull(subtask, "Issue hasn`t been received");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkGettingOfEpicsFromServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epics?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String epic = response.body();
            System.out.println(epic);
            Assertions.assertNotNull(epic, "Issue hasn`t been received");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkGettingOfHistoryFromServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String history = response.body();
            System.out.println(history);
            Assertions.assertNotNull(history, "History hasn`t been received");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkCreatingOfIssueOnServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        Task taskToCreate = new Task("Some title for the task", "Description for the issue");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(taskToCreate);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            Assertions.assertNotNull(response.body(), "Issue hasn`t been created");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkUpdatingOfIssueOnServer() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        Task taskToCreate = new Task("Some title for the task", "Description for the issue");
        taskToCreate.setId(2);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(taskToCreate);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            //System.out.println(response.body());
            Assertions.assertNotNull(response.body(), "Issue hasn`t been updated");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkDeletingOfIssueByRequest() {
        createTestInstances();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            Assertions.assertFalse(server.manager.tasks.containsKey(1), "The issue was not deleted");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
