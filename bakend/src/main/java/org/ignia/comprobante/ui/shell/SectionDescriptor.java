package org.ignia.comprobante.ui.shell;

import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;

import java.util.function.Supplier;

public record SectionDescriptor(SectionId id, String viewFxml, Supplier<SidebarConfig> sidebarConfig) {
}
