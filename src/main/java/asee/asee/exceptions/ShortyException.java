package asee.asee.exceptions;

public class ShortyException extends Exception{

    public ShortyException(){}

    public ShortyException(String message){
        super(message);
    }

    public ShortyException(String message, String details){
        super(message + " because: " + details);
    }
}
