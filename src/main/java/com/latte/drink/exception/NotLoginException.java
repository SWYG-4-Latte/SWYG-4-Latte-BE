package com.latte.drink.exception;

public class NotLoginException extends RuntimeException {
    public NotLoginException(String message) {
        super(message);
    }
}
