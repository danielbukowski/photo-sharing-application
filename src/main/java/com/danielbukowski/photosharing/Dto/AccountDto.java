package com.danielbukowski.photosharing.Dto;


import lombok.Builder;

import java.util.UUID;


@Builder
public record AccountDto (
        UUID id,
        String email) {
}
