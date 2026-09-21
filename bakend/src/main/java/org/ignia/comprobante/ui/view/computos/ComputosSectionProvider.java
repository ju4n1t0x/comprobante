package org.ignia.comprobante.ui.view.computos;

import org.ignia.comprobante.computos.IComputoService;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComputosSectionProvider implements SectionProvider {

    private final IComputoService computoService;

    public ComputosSectionProvider(IComputoService computoService) {
        this.computoService = computoService;
    }


    @Override
    public SectionId sectionId() {
        return SectionId.COMPUTOS;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/computos/computos.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig(){
        long total = computoService.count();

        return new SidebarConfig(
                "COMPUTOS",
                List.of( new SidebarItem(
                        "all",
                        "Todos los cómputos",
                        total,
                        SidebarItem.Tone.DEFAULT
                )),
                new SummaryCard(
                        "Computos",
                        String.valueOf(total),
                        "Total de cómputos"
                )
        );
    }
}
