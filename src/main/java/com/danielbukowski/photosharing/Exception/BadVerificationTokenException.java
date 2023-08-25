package com.danielbukowski.photosharing.Exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class BadVerificationTokenException extends RuntimeException {

    public BadVerificationTokenException(String message) {
        super(message);
    }
}
