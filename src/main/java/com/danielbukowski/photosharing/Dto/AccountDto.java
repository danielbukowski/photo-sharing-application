package com.danielbukowski.photosharing.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;


@Builder
public record AccountDto (
        UUID accountId,
        String nickname,
        String email,
        @JsonInclude(NON_EMPTY)
        String biography,
        boolean isEmailVerified,
        @JsonInclude(NON_NULL)
        @JsonFormat(pattern="yyyy-MM-dd HH:mm")
        LocalDateTime accountVerifiedAt,
        List<String> roles,
        List<String> permissions) {
}
