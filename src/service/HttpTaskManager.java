package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import issues.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {

    private KVTaskClient client;
    private HttpServer server;

    public HttpTaskManager(String URL) throws IOException {
        super(URL);
        this.client = new KVTaskClient(URL);
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/tasks/", new TasksHandler());
    }

    class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String response = null;
            String query;
            int id;
            String path = httpExchange.getRequestURI().getPath();
            URL url = new URL(path);
            String[] splitStrings = path.split("/");
            int pathLength = splitStrings.length;
            String method = httpExchange.getRequestMethod();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            Gson gson = gsonBuilder.create();


            switch (method) {

                case "GET":
                    if (pathLength == 2) {
                        response = gson.toJson(getAllIssuesList());
                    } else if (pathLength > 2) {
                        if (url.getQuery() != null) {
                            query = url.getQuery();
                            id = getUserId(query);
                            response = gson.toJson(getIssueById(id));
                        } else if (splitStrings[2].equals("history")) {
                            response = gson.toJson(historyManager.getHistory());
                        }
                    }
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    break;
                case "PUT":
                    String issueAsJson = String.valueOf(httpExchange.getRequestBody());
                    if (splitStrings[2].equals("task")) {
                        Task task = gson.fromJson(issueAsJson, Task.class);
                        if (tasks.containsKey(task.getId())) {
                            updateTask(task);
                        } else {
                            addTask(task);
                        }
                    } else if (splitStrings[2].equals("epic")) {
                        Epic epic = gson.fromJson(issueAsJson, Epic.class);
                        if (epics.containsKey(epic.getId())) {
                            updateEpic(epic);
                        } else {
                            addEpic(epic);
                        }
                    } else if (splitStrings[2].equals("subtask")) {
                        Subtask subtask = gson.fromJson(issueAsJson, Subtask.class);
                        if (subtasks.containsKey(subtask.getId())) {
                            updateSubtask(subtask);
                        } else {
                            addSubtask(subtask);
                        }
                    }
                    httpExchange.sendResponseHeaders(201, 0);
                    httpExchange.close();
                    break;
                case "DELETE":
                    if (url.getQuery() != null) {
                        query = url.getQuery();
                        id = getUserId(query);
                        deleteIssueById(id);
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

