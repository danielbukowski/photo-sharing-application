package com.danielbukowski.photosharing.Dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Builder
public record AccountDto (
        UUID accountId,
        String nickname,
        String email,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String biography,
        boolean isEmailVerified,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @JsonFormat(pattern="yyyy-MM-dd HH:mm")
        LocalDateTime accountVerifiedAt,
        int numberOfUploadedImages,
        boolean isLocked,
        List<String> roles,
        List<String> permissions) {
}
