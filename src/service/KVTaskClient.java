package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import issues.Epic;
import issues.Subtask;
import issues.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KVTaskClient {
    URI url;
    public String API_TOKEN;
    HttpClient client;


    public KVTaskClient(String URL) {
        this.client = HttpClient.newHttpClient();
        this.url = URI.create(URL);
        registerToServer();
    }

    private void registerToServer() {
        try {
            System.out.println("Registering to KVServer...");
            String url = "http://localhost:8078/register/";
            URI regUrl = URI.create(url);
            HttpRequest request = HttpRequest.newBuilder().uri(regUrl).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Registered");
            this.API_TOKEN = String.valueOf(response.body());
            System.out.println("API Token saved: " + this.API_TOKEN);
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveTasksToServer(HashMap<Integer, Task> tasks) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(tasks);
        URI saveUrl = URI.create(url + "/save/tasks?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(request.bodyPublisher());
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveSubtasksToServer(HashMap<Integer, Subtask> subtasks) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(subtasks);
        URI saveUrl = URI.create(url + "/save/subtasks?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveEpicsToServer(HashMap<Integer, Epic> epics) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(epics);
        URI saveUrl = URI.create(url + "/save/epics?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void saveHistoryToServer(List<Task> history) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(history);
        URI saveUrl = URI.create(url + "/save/history?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void savePrioritizedTasksToServer(TreeMap<ZonedDateTime, Integer> prioritizedIssues) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
        Gson gson = gsonBuilder.create();
        String value = gson.toJson(prioritizedIssues);
        URI saveUrl = URI.create(url + "/save/prioritizedIssues?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().uri(saveUrl).POST(HttpRequest.BodyPublishers.ofString(value)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public HttpTaskManager loadManagerFromServer(String API_TOKEN) {

        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078");

        try {
            //Tasks
            String url = "http://localhost:8078/load/tasks?API_TOKEN=" + API_TOKEN;
            URI regUrl = URI.create(url);
            HttpRequest request = HttpRequest.newBuilder().uri(regUrl).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String mapAsString = response.body();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.serializeNulls();
            gsonBuilder.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter().nullSafe());
            gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter().nullSafe());
            Gson gson = gsonBuilder.create();
            Type taskType = new TypeToken<HashMap<Integer, Task>>(){}.getType();
            HashMap<Integer, Task> tasks = gson.fromJson(mapAsString, taskType);
            manager.tasks = tasks;
            System.out.println(manager.getTasks());

            //Subtasks
            url = "http://localhost:8078/load/subtasks?API_TOKEN=" + API_TOKEN;
            regUrl = URI.create(url);
            request = HttpRequest.newBuilder().uri(regUrl).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            mapAsString = response.body();
            Type subtaskType = new TypeToken<HashMap<Integer, Subtask>>(){}.getType();
            HashMap<Integer, Subtask> subtasks = gson.fromJson(mapAsString, subtaskType);
            manager.subtasks = subtasks;
            System.out.println(manager.getSubtasks());


            //Epics
            url = "http://localhost:8078/load/epics?API_TOKEN=" + API_TOKEN;
            regUrl = URI.create(url);
            request = HttpRequest.newBuilder().uri(regUrl).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            mapAsString = response.body();
            Type epicType = new TypeToken<HashMap<Integer, Epic>>(){}.getType();
            HashMap<Integer, Epic> epics = gson.fromJson(mapAsString, epicType);
            manager.epics = epics;
            System.out.println(manager.getEpics());

            //History
            url = "http://localhost:8078/load/history?API_TOKEN=" + API_TOKEN;
            regUrl = URI.create(url);
            request = HttpRequest.newBuilder().uri(regUrl).GET().build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            mapAsString = response.body();
            Type historyType = new TypeToken<List<Integer>>(){}.getType();
            List<Integer> history = gson.fromJson(mapAsString, historyType);
            if (!history.isEmpty()) {
                for (Integer id : history) {
                    manager.getIssueById(id);
                }
            }
            System.out.println(manager.historyManager.getHistory());

            //Prioritized Tasks
            manager.refreshPrioritizedIssuesList();
            System.out.println(manager.getPrioritizedIssuesList());



        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
