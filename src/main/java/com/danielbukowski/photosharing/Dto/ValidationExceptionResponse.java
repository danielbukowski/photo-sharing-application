package com.danielbukowski.photosharing.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Builder
public record ValidationExceptionResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        LocalDateTime timestamp,
        int status,
        String reason,
        Map<String, List<String>> fieldNames,
        String path) {
}
