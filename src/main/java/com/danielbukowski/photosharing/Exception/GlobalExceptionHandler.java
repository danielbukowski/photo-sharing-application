package com.danielbukowski.photosharing.Exception;


import com.danielbukowski.photosharing.Dto.ValidationExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AccountNotFoundException.class})
    public ResponseEntity<?> handleAccountNotFoundException(AccountNotFoundException ex) {
        var bodyResponse = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.getReasonPhrase())
                .reason(ex.getMessage())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                bodyResponse,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        var bodyResponse = ExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .reason(ex.getMessage())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .build();

        return new ResponseEntity<>(
                bodyResponse,
                HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, List<String>> foundedFieldErrors = new HashMap<>();

        for (var field : ex.getBindingResult().getFieldErrors()) {
            var fieldValue = foundedFieldErrors.computeIfAbsent(field.getField(), k -> new ArrayList<>());
            fieldValue.add(field.getDefaultMessage());
        }

        var bodyResponse = ValidationExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(ServletUriComponentsBuilder.fromCurrentRequest().toUriString())
                .fieldNames(foundedFieldErrors)
                .reason("The fields have not meet the requirements")
                .build();
        return new ResponseEntity<>(
                bodyResponse, HttpStatus.BAD_REQUEST
        );
    }
}
