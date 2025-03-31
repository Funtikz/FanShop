package org.example.fanshop.exceptions;

public class FavoriteException extends RuntimeException{
    public FavoriteException(String message){
        super(message);
    }

    public FavoriteException(){
        super("Ошибка в избранной категории");
    }


}
