package com.example.newspeed.global.error;

public class PasswordMismatchException  extends RuntimeException {
    public PasswordMismatchException(String message) {
        super(message);
    }
}
