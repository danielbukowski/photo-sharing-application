package com.danielbukowski.photosharing.Dto;


import lombok.Builder;


@Builder
public record AccountDto (
        String email) {
}
