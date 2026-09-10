package org.ignia.comprobante.ui.model;

import java.util.List;

public record PageData<T>(List<T> items, int page, int totalPages, int pageSize, long totalElements) {

    public static <T> PageData<T> of(List<T> items, int page, int totalPages, int pageSize, long totalElements) {
        return new PageData<>(items, page, totalPages, pageSize, totalElements);
    }
}
