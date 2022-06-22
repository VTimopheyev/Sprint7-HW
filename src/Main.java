import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import issues.Epic;
import issues.Subtask;
import issues.Task;
import service.*;

import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;


public class Main {

    public static void main(String[] args) throws IOException {

        KVServer server = new KVServer();
        server.start();
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

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        Task updatedTask = task1;
        updatedTask.setDescription("It is been updated");
        System.out.println(gson.toJson(updatedTask));

        System.out.println("ready");


        /*
        Task task1 = new Task("First task", "Very first task");
        Task task2 = new Task("Second task", "Another one");
        Epic epic1 = new Epic("First epic", "Some description");
        Epic epic2 = new Epic("Second epic", "Some description");
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);

        Subtask subtask1 = new Subtask("FirstSubtask", "Some description", epic1.getId());
        Subtask subtask2 = new Subtask("Second subtask", "Some description", epic1.getId());
        Subtask subtask3 = new Subtask("Second subtask", "Some description", epic2.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.addSubtask(subtask3);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(manager.getSubtasks());
        System.out.println(value);
        KVTaskClient client = new KVTaskClient("http://localhost:8078");
        client.saveSubtasksToServer(manager.getSubtasks());
        client.saveTasksToServer(manager.getTasks());
        client.saveEpicsToServer(manager.getEpics());
        client.saveHistoryToServer(manager.getHistoryManager().getHistory());
        client.savePrioritizedTasksToServer(manager.getPrioritizedIssuesList());

        System.out.println(server.data);

        client.loadManagerFromServer(client.API_TOKEN);*/
    }
}
