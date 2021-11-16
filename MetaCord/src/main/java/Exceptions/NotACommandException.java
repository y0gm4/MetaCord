package Exceptions;

public class NotACommandException extends Exception{

    public NotACommandException(String error){
        super(error);
    }

}
