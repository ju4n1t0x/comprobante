package org.ignia.comprobante.ui.view.clients;

import org.ignia.comprobante.cliente.IClientService;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ClientSectionProvider  implements SectionProvider {

    private final IClientService clientService;

    public ClientSectionProvider(IClientService clientService){
        this.clientService = clientService;
    }



    @Override
    public SectionId sectionId() {
        return SectionId.CLIENTES;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/clients/clients.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig(){
        long total = clientService.countClients();
        Map<String, Long> countsByCity = clientService.countsByCity();

        List<SidebarItem> items = new ArrayList<>();
        items.add(new SidebarItem("all", "Todos los clientes", total, SidebarItem.Tone.DEFAULT));
        countsByCity.forEach((city, cnt) ->
                items.add(new SidebarItem("city-" + city, city, cnt, SidebarItem.Tone.DEFAULT)));

        return new SidebarConfig("Clientes", items,
                new SummaryCard("Padron", String.valueOf(total), "clientes activos"));

    }
}
