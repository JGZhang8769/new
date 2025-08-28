package com.example.springbuilderexample.transaction;

public class CustomCheckedException extends Exception {
    public CustomCheckedException(String message) {
        super(message);
    }
}
