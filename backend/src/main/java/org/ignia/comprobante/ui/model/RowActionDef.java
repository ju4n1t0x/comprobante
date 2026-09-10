package org.ignia.comprobante.ui.model;

import java.util.function.Consumer;

public record RowActionDef<T>(String label, Consumer<T> onAction) {
}
