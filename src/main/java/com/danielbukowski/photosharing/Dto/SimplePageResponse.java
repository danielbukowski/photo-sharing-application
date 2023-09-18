package com.danielbukowski.photosharing.Dto;

import java.util.List;

public record SimplePageResponse<T>(
        long numberOfElements,
        List<T> content,
        int currentPage,
        int totalPages,
        boolean last) {
}
