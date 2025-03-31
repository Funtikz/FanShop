package org.example.fanshop.exceptions;

public class ResponseException extends RuntimeException{
    public ResponseException(String message){
        super(message);
    }

    public ResponseException(){
        super("Ошибка");
    }
}
