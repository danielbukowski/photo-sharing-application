package com.danielbukowski.photosharing.Exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ResponseStatus(BAD_GATEWAY)
public class ImageException extends RuntimeException {

    public ImageException(String message) {
        super(message);
    }
}
