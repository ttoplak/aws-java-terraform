package com.example.myapp.products.exceptions;

public class InvalidObjectException extends Exception {

    public InvalidObjectException(String message) {
        super(message);
    }

    public InvalidObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
