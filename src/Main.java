import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import issues.Epic;
import issues.Subtask;
import issues.Task;
import service.DurationAdapter;
import service.HttpTaskManager;
import service.KVTaskClient;
import service.ZonedDateTimeAdapter;
import java.io.IOException;
import java.time.Duration;
import java.time.ZonedDateTime;


public class Main {

    public static void main(String[] args) throws IOException {

        KVServer server = new KVServer();
        server.start();
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");
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

        client.loadManagerFromServer(client.API_TOKEN);
    }
}
