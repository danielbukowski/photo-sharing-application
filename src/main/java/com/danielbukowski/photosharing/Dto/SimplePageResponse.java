package com.danielbukowski.photosharing.Dto;

import java.util.List;

public record SimplePageResponse<T>(
        long numberOfElements,
        List<T> data,
        int currentPage,
        int totalPages,
        boolean last) {
}
