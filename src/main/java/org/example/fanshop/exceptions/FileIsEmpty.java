package org.example.fanshop.exceptions;

public class FileIsEmpty extends RuntimeException {
    public FileIsEmpty(){
        super("Файл пустой!");
    }

    public FileIsEmpty(String message){
        super(message);
    }
}
