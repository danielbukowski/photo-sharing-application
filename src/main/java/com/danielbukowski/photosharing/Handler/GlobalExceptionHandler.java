package com.danielbukowski.photosharing.Handler;


import com.danielbukowski.photosharing.Dto.ExceptionResponse;
import com.danielbukowski.photosharing.Dto.ValidationExceptionResponse;
import com.danielbukowski.photosharing.Exception.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAnyException() {
        var responseBody = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .reason(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(
            {AccountAlreadyExistsException.class,
            InvalidPasswordException.class,
            BadVerificationTokenException.class,
            S3Exception.class,
            ImageException.class}
    )
    public ResponseEntity<?> handleBadRequestExceptions(RuntimeException ex) {
        var responseBody = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .reason(ex.getMessage())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(
            {AccountNotFoundException.class,
            ImageNotFoundException.class}
    )
    public ResponseEntity<?> handleNotFoundExceptions(RuntimeException ex) {
        var responseBody = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .reason(ex.getMessage())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> foundedFieldErrors = new HashMap<>();

        for (var field : ex.getBindingResult().getFieldErrors()) {
            var fieldValue = foundedFieldErrors.computeIfAbsent(field.getField(), k -> new ArrayList<>());
            fieldValue.add(field.getDefaultMessage());
        }

        var responseBody = ValidationExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .fieldNames(foundedFieldErrors)
                .reason("The fields did not meet the requirements")
                .build();
        return new ResponseEntity<>(
                responseBody,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        var responseBody = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .reason(ex.getMessage())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException() {
        var responseBody = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .reason("You don't have access to this resource")
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                responseBody,
                HttpStatus.FORBIDDEN
        );
    }

}
