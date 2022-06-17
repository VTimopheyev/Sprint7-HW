package service;

public class ManagerSaveException extends RuntimeException{
    String message;

    public ManagerSaveException (String message){
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
