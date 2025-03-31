package org.example.fanshop.exceptions;

public class MappingException extends RuntimeException {
    MappingException (){
        super("Не удалось преобразовать объект");
    }
    public MappingException(String message){
        super(message);
    }

}
