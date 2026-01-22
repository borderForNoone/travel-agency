package com.epam.finaltask.exception;

import lombok.Getter;

@Getter
public class CustomEntityNotFoundException extends RuntimeException {
    private final String errorCode;

    public CustomEntityNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
