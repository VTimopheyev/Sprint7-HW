package service;

public class HttpTaskManager extends FileBackedTasksManager implements TaskManager {

    public KVTaskClient client;


    public HttpTaskManager(String URL) {
        super(URL);
        this.client = new KVTaskClient(URL);
    }

    @Override
    public void save(){
        System.out.println("kukusiki");
    }

    public HttpTaskManager load (){
        return new HttpTaskManager("localhostic");
    }


}

