package org.ignia.comprobante.ui.model;

import java.util.function.Function;

public record ColumnDef<T>(String header, Function<T, String> text, Align align, Function<T, String> styleClass) {

    public enum Align { LEFT, RIGHT }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text) {
        return new ColumnDef<>(header, text, Align.LEFT, t -> "");
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, Align align) {
        return new ColumnDef<>(header, text, align, t -> "");
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, Align align, Function<T, String> styleClass) {
        return new ColumnDef<>(header, text, align, styleClass);
    }
}
