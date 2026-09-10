package org.ignia.comprobante.ui.model;

import java.util.List;

public record ActionBarConfig(String breadcrumb, String searchPlaceholder, List<ButtonDef> buttons) {
}
