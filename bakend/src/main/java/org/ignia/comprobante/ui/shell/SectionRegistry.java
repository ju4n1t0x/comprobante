package org.ignia.comprobante.ui.shell;


import org.ignia.comprobante.security.SessionService;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.user.Role;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SectionRegistry {

    private final SessionService sessionService;
    private final Map<SectionId, SectionDescriptor> descriptors = new LinkedHashMap<>();

    public SectionRegistry(List<SectionProvider> providers, SessionService sessionService) {
        this.sessionService = sessionService;
        for (SectionId id : SectionId.values()) {
            descriptors.put(id, placeholder(id));
        }
        for (SectionProvider provider : providers) {
            descriptors.put(provider.sectionId(), provider.descriptor());
        }
    }

    public List<SectionId> sections() {

        Role role = sessionService.getCurrentUserRole();
        return descriptors.keySet().stream()
                .filter(id -> hashAcces(id, role))
                .toList();
    }

    private boolean hashAcces(SectionId id, Role role){
        return switch(role){
            case SUPER_ADMIN -> true;
            case ADMIN -> id != SectionId.USUARIOS;
            case SELLER -> id == SectionId.VENTAS
                        || id == SectionId.CLIENTES
                        || id == SectionId.PRODUCTOS
                        || id == SectionId.CATEGORIAS
                        || id == SectionId.COTIZAR;
        };
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
