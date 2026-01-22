package com.epam.finaltask.exception;

import lombok.Getter;

@Getter
public class CustomEntityNotFoundException extends RuntimeException {
    private final String code;

    public CustomEntityNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

}
