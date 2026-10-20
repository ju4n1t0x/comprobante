package org.ignia.comprobante.ui.view.cotizaciones;

import org.ignia.comprobante.cotizacion.ICotizacionService;
import org.ignia.comprobante.cotizacion.State;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CotizacionesSectionProvider implements SectionProvider {

    @Autowired
    private ICotizacionService cotizacionService;

    public CotizacionesSectionProvider(ICotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }


    @Override
    public SectionId sectionId() {
        return SectionId.COTIZACIONES;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/cotizaciones/cotizaciones.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig() {

        long pendientes = cotizacionService.countByState(State.PENDIENTE);
        long aprobadas = cotizacionService.countByState(State.APROBADA);
        long rechazadas = cotizacionService.countByState(State.RECHAZADA);
        long total = pendientes + aprobadas + rechazadas;

        List<SidebarItem> items = new ArrayList<>();
        items.add(new SidebarItem("all", "Todas las cotizaciones", total, SidebarItem.Tone.DEFAULT));
        items.add(new SidebarItem(State.PENDIENTE.name(), "Pendientes", pendientes, SidebarItem.Tone.WARN));
        items.add(new SidebarItem(State.APROBADA.name(), "Aprobadas", aprobadas, SidebarItem.Tone.DEFAULT));
        items.add(new SidebarItem(State.RECHAZADA.name(), "Rechazadas", rechazadas, SidebarItem.Tone.DEFAULT));

        return new SidebarConfig("COTIZACIONES", items,
                new SummaryCard("Cotizaciones", String.valueOf(total), "Total de cotizaciones"));

    }
}
