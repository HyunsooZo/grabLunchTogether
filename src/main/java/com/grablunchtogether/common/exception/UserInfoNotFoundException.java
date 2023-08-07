package com.grablunchtogether.common.exception;

public class UserInfoNotFoundException extends RuntimeException {
    public UserInfoNotFoundException(String message) {
        super(message);
    }
}
