package org.example.fanshop.exceptions;

public class UserImageProfileNotFound extends RuntimeException{

    public UserImageProfileNotFound(){
        super("У пользователя нет фота");
    }

    public UserImageProfileNotFound(String message){
        super(message);
    }
}
