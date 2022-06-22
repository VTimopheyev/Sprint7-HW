package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import issues.Epic;
import issues.Subtask;
import issues.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.ZonedDateTime;

public class HttpTaskServer {

    //"http://localhost:8080/"

    public HttpTaskManager manager;
    public HttpServer server;

    public HttpTaskServer(String url) {
        try {
            this.server = HttpServer.create();
            this.server.bind(new InetSocketAddress("localhost", 8080), 0);
            server.createContext("/tasks", new TasksHandler());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        this.manager = Managers.getDefault(url);
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = null;
            String query;
            int id;
            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            query = httpExchange.getRequestURI().getQuery();
            String[] splitStrings = path.split("/");
            int pathLength = splitStrings.length;
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
            Gson gson = gsonBuilder.create();

            switch (method) {

                case "GET":
                    if (pathLength == 2) {
                        response = gson.toJson(manager.getPrioritizedIssuesList());
                    } else if (pathLength > 2) {
                        if (httpExchange.getRequestURI().getQuery() != null) {
                            query = httpExchange.getRequestURI().getQuery();
                            System.out.println(query);
                            id = getUserId(query);
                            response = gson.toJson(manager.getIssueById(id));
                        } else if (splitStrings[2].equals("history")) {
                            response = gson.toJson(manager.historyManager.getHistory());
                        }
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "POST":
                    String issueAsJson = new String(httpExchange.getRequestBody().readAllBytes());
                    if (splitStrings[2].equals("task")) {
                        Task task = gson.fromJson(issueAsJson, Task.class);
                        System.out.println(task);
                        if (manager.tasks.containsKey(task.getId())) {
                            manager.updateTask(task);
                            System.out.println("Task was updated" + manager.tasks.get(task.getId()));
                        } else {
                            manager.addTask(task);
                            System.out.println("Task was created" + manager.tasks.get(task.getId()));
                        }
                    } else if (splitStrings[2].equals("epic")) {
                        Epic epic = gson.fromJson(issueAsJson, Epic.class);
                        if (manager.epics.containsKey(epic.getId())) {
                            manager.updateEpic(epic);
                            System.out.println("Epic was updated" + manager.epics.get(epic.getId()));
                        } else {
                            manager.addEpic(epic);
                            System.out.println("Epic was created" + manager.epics.get(epic.getId()));
                        }
                    } else if (splitStrings[2].equals("subtask")) {
                        Subtask subtask = gson.fromJson(issueAsJson, Subtask.class);
                        if (manager.subtasks.containsKey(subtask.getId())) {
                            manager.updateSubtask(subtask);
                            System.out.println("Subtask was updated" + manager.subtasks.get(subtask.getId()));
                        } else {
                            manager.addSubtask(subtask);
                            System.out.println("Subtask was created" + manager.subtasks.get(subtask.getId()));
                        }
                    }
                    httpExchange.sendResponseHeaders(201, 0);
                    httpExchange.close();
                    break;
                case "DELETE":
                    if (httpExchange.getRequestURI().getQuery() != null) {
                        query = httpExchange.getRequestURI().getQuery();
                        id = getUserId(query);
                        manager.deleteIssueById(id);
                        System.out.println("Issue was deleted");
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    httpExchange.close();
                    break;
                default:
                    httpExchange.sendResponseHeaders(400, 0);
                    httpExchange.close();
            }
        }

        private int getUserId(String query) {
            String[] splittedQuery = query.split("=");
            return Integer.parseInt(splittedQuery[1]);
        }
    }
}
