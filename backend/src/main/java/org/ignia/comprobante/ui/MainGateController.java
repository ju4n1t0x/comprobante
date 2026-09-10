package org.ignia.comprobante.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.ui.components.SidebarMenu;
import org.ignia.comprobante.ui.components.TopNavBar;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.TabDef;
import org.ignia.comprobante.ui.model.UserChip;
import org.ignia.comprobante.ui.shell.SectionRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainGateController {

    @FXML private StackPane navBarSlot;
    @FXML private StackPane sidebarSlot;
    @FXML private StackPane contentPane;

    private final NavigationService navigation;
    private final SectionRegistry registry;

    public MainGateController(NavigationService navigation, SectionRegistry registry) {
        this.navigation = navigation;
        this.registry = registry;
    }

    @FXML
    private void initialize() {
        SidebarMenu sidebar = new SidebarMenu();
        sidebarSlot.getChildren().setAll(sidebar);

        List<TabDef> tabs = registry.sections().stream()
                .map(id -> new TabDef(id, id.label()))
                .toList();
        TopNavBar nav = new TopNavBar(tabs, new UserChip("Usuario", "Rol", "U"), navigation::openSection);
        navBarSlot.getChildren().setAll(nav);

        navigation.bind(contentPane, sidebar);

        nav.setActive(SectionId.CATEGORIAS);
        navigation.openSection(SectionId.CATEGORIAS);
    }
}
