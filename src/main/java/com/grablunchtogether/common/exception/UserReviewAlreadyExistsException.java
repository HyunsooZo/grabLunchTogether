package com.grablunchtogether.common.exception;

public class UserReviewAlreadyExistsException extends RuntimeException {
    public UserReviewAlreadyExistsException(String message) {
        super(message);
    }
}
