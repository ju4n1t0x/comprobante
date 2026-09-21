package org.ignia.comprobante.ui.view.cotizarCubierta;

import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CotizarSectionProvider implements SectionProvider {

    @Override
    public SectionId sectionId(){
        return SectionId.COTIZAR;
    }

    @Override
    public SectionDescriptor descriptor(){
        return new SectionDescriptor(
                sectionId(),
                "/fxml/cotizar/cotizar.fxml",
                () -> new SidebarConfig(
                        "COTIZAR",
                        List.of(
                                new SidebarItem(
                                        "all",
                                        "Cotizar cubierta",
                                        0,
                                        SidebarItem.Tone.DEFAULT
                                )
                        ),
                        new SummaryCard("Cotizacion", "--", "Nueva cotizacion")
                )
        );
    }
}
