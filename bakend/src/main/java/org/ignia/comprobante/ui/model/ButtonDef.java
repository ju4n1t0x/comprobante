package org.ignia.comprobante.ui.model;

public record ButtonDef(String label, Style style, Runnable onAction) {

    public enum Style { PRIMARY, GHOST }
}
