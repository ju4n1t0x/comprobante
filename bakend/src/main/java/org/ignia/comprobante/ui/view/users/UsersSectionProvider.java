package org.ignia.comprobante.ui.view.users;


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

@Component
public class UsersSectionProvider implements SectionProvider {

    @Autowired
    private IUserService userService;

    @Override
    public SectionId sectionId() {
        return SectionId.USUARIOS;
    }

    @Override
    public SectionDescriptor descriptor() {
        return new SectionDescriptor(sectionId(), "/fxml/users/users.fxml", this::sidebarConfig);
    }

    private SidebarConfig sidebarConfig(){
        long total = userService.findAll().size();

        List<SidebarItem> items = new ArrayList<>();
        items.add(new SidebarItem("all", "Todos los usuarios", total, SidebarItem.Tone.DEFAULT));
        return new SidebarConfig("USUARIOS", items, new SummaryCard("Usuarios", String.valueOf(total), "Total de usuarios"));
    }
}
