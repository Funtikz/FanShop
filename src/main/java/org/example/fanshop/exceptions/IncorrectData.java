package org.example.fanshop.exceptions;


public class IncorrectData  extends RuntimeException {
    public IncorrectData(){
        super();
    }

    public IncorrectData(String message){
        super(message);
    }
}
