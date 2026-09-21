package org.ignia.comprobante.ui;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.security.SessionService;
import org.ignia.comprobante.ui.components.SidebarMenu;
import org.ignia.comprobante.ui.components.TopNavBar;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.TabDef;
import org.ignia.comprobante.ui.model.UserChip;
import org.ignia.comprobante.ui.shell.SectionRegistry;
import org.ignia.comprobante.user.UserModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MainGateController {

    @FXML private StackPane navBarSlot;
    @FXML private StackPane sidebarSlot;
    @FXML private StackPane contentPane;

    private final NavigationService navigation;
    private final SectionRegistry registry;
    private final SessionService sessionService;

    public MainGateController(NavigationService navigation, SectionRegistry registry, SessionService sessionService) {
        this.navigation = navigation;
        this.registry = registry;
        this.sessionService = sessionService;
    }

    @FXML
    private void initialize() {
        UserModel user = sessionService.getCurrentUser();

        SidebarMenu sidebar = new SidebarMenu();
        sidebarSlot.getChildren().setAll(sidebar);

        List<TabDef> tabs = registry.sections().stream()
                .map(id -> new TabDef(id, id.label()))
                .toList();

        //user badge navbar
        String initials = user.getUserName().substring(0,1).toUpperCase();
        TopNavBar nav = new TopNavBar(tabs, new UserChip(user.getUserName(), user.getRole().name(), initials), navigation::openSection);
        navBarSlot.getChildren().setAll(nav);

        navigation.bind(contentPane, sidebar);

        SectionId first = registry.sections().getFirst();

        nav.setActive(first);
        navigation.openSection(first);
    }
}
