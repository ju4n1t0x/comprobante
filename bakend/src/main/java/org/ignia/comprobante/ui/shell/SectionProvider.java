package org.ignia.comprobante.ui.shell;

import org.ignia.comprobante.ui.model.SectionId;

public interface SectionProvider {

    SectionId sectionId();

    SectionDescriptor descriptor();
}
