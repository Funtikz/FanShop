package org.example.fanshop.exceptions;

public class DifferentPassword extends RuntimeException{
    public DifferentPassword(){
        super("Пароли не совпадают");
    }

    public DifferentPassword(String message){
        super(message);
    }
}
