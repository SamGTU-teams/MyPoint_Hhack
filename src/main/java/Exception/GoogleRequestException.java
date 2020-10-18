package Exception;

public class GoogleRequestException extends RuntimeException{
    public GoogleRequestException(String message) {
        super(message);
    }
    //Exactly two waypoints required in transit requests

    public GoogleRequestException() {
        super();
    }
}
