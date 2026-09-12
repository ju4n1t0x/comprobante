package org.ignia.comprobante.ui.model;

import java.util.function.Function;

public record ColumnDef<T>(String header, Function<T, String> text, Align align, Function<T, String> styleClass, double prefWidth) {

    public enum Align { LEFT, RIGHT }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text) {
        return new ColumnDef<>(header, text, Align.LEFT, t -> "", 120);
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, Align align) {
        return new ColumnDef<>(header, text, align, t -> "", 120);
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, Align align, Function<T, String> styleClass) {
        return new ColumnDef<>(header, text, align, styleClass, 120);
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, double prefWidth) {
        return new ColumnDef<>(header, text, Align.LEFT, t -> "", prefWidth);
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, Align align, double prefWidth) {
        return new ColumnDef<>(header, text, align, t -> "", prefWidth);
    }

    public static <T> ColumnDef<T> of(String header, Function<T, String> text, Align align, Function<T, String> styleClass, double prefWidth) {
        return new ColumnDef<>(header, text, align, styleClass, prefWidth);
    }
}
