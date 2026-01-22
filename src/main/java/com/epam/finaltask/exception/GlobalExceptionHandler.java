package com.epam.finaltask.exception;

import com.epam.finaltask.dto.RemoteResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({EntityAlreadyExistsException.class})
    public ResponseEntity<RemoteResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        log.warn("Entity already exists: code={}, message={}", ex.getErrorCode(), ex.getMessage(), ex);
        RemoteResponse remoteResponse =
                RemoteResponse.create(false, ex.getErrorCode(), ex.getMessage(), null);
        return new ResponseEntity<>(remoteResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<RemoteResponse> handleEntityNotFound(CustomEntityNotFoundException ex) {
        log.warn("Entity not found: code={}, message={}", ex.getErrorCode(), ex.getMessage(), ex);
        RemoteResponse remoteResponse =
                RemoteResponse.create(false, ex.getErrorCode(), ex.getMessage(), null);
        return new ResponseEntity<>(remoteResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RemoteResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(ex.getMessage());

        log.warn("Validation error: {}", errors, ex);

        RemoteResponse remoteResponse = RemoteResponse.create(
                false,
                StatusCodes.INVALID_DATA.name(),
                errors,
                null
        );

        return new ResponseEntity<>(remoteResponse, HttpStatus.BAD_REQUEST);
    }
}
