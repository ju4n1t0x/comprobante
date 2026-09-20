package org.ignia.comprobante.ui.view.sales;

import org.ignia.comprobante.cliente.IClientService;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.sales.ISalesService;
import org.ignia.comprobante.sales.State;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionProvider;
import org.ignia.comprobante.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SalesSectionProvider implements SectionProvider {

    @Autowired
    private ISalesService salesService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IClientService clientService;

    @Autowired
    private IUserService userService;


    @Override
    public SectionId sectionId() {
        return SectionId.VENTAS;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/sales/sales.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig() {
        long totalClosed = salesService.countByState(State.CLOSED);
        long totalPending = salesService.countByState(State.PENDING);
        long total = totalClosed + totalPending;

        List<SidebarItem> items = new ArrayList<>();
        items.add(new SidebarItem("all", "Todas las ventas", total, SidebarItem.Tone.DEFAULT));
        items.add(new SidebarItem("closed", "Ventas cerradas", totalClosed, SidebarItem.Tone.DEFAULT));
        items.add(new SidebarItem("pending", "Ventas pendientes", totalPending, SidebarItem.Tone.WARN));
        return new SidebarConfig("VENTAS", items,
                new SummaryCard("Ventas", String.valueOf(total), "Total de ventas"));
    }
}
