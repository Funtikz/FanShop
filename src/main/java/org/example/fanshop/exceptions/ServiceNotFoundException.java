package org.example.fanshop.exceptions;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(String string) {
        super(string);
    }

    public ServiceNotFoundException(){
        super("Такой услуги не существует");
    }
}
