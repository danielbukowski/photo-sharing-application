package com.danielbukowski.photosharing.Dto;

import lombok.Builder;

@Builder
public record ImageDto (
        byte[] data,
        String contentType) {
}
