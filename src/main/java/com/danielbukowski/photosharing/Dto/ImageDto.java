package com.danielbukowski.photosharing.Dto;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record ImageDto(
        byte[] data,
        String contentType,
        boolean isPrivate) implements Serializable {
}
