package org.ignia.comprobante.ui.model;

import java.util.List;

public record SidebarConfig(String title, List<SidebarItem> items, SummaryCard summary) {
}
