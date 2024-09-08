package com.project.exception;

public class NotLoadingCompleted extends RuntimeException{
    public NotLoadingCompleted(String message) {
        super(message);
    }
}
