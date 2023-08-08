package com.danielbukowski.photosharing.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ExceptionResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        LocalDateTime timestamp,
        int status,
        String reason,
        String path) {
}
