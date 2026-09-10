package org.ignia.comprobante.ui.shell;

import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SectionRegistry {

    private final Map<SectionId, SectionDescriptor> descriptors = new LinkedHashMap<>();

    public SectionRegistry(List<SectionProvider> providers) {
        for (SectionId id : SectionId.values()) {
            descriptors.put(id, placeholder(id));
        }
        for (SectionProvider provider : providers) {
            descriptors.put(provider.sectionId(), provider.descriptor());
        }
    }

    public List<SectionId> sections() {
        return List.copyOf(descriptors.keySet());
    }

    public SectionDescriptor descriptor(SectionId id) {
        return descriptors.get(id);
    }

    private SectionDescriptor placeholder(SectionId id) {
        return new SectionDescriptor(id, "/fxml/placeholder.fxml",
                () -> new SidebarConfig(id.label(),
                        List.of(new SidebarItem("none", "Sin implementar", 0, SidebarItem.Tone.DEFAULT)),
                        new SummaryCard("PRÓXIMAMENTE", "—", "")));
    }
}
