package service;

import issues.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    public static void runServer() {
        KVServer server = null;
        try {
            server = new KVServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.start();
    }

    @Test
    public void testingOfLoadingManagerInstanceFromServer() {
        runServer();
        HttpTaskManager taskManager = Managers.getDefault("http://localhost:8078");
        Task task1 = new Task("First task", "Very first task");
        Task task2 = new Task("Second task", "Another one");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        String token1 = taskManager.client.getAPI_TOKEN();
        System.out.println(token1);
        System.out.println(taskManager.tasks);

        HttpTaskManager taskManager1 = taskManager.load(token1);
        System.out.println(taskManager1.tasks.size());
        Assertions.assertEquals(2, taskManager.tasks.size(), "Issue hasn`t been received");
    }


}
