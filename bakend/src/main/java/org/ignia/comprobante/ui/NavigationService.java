package org.ignia.comprobante.ui;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.ui.components.SidebarMenu;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.shell.SectionDescriptor;
import org.ignia.comprobante.ui.shell.SectionRegistry;
import org.ignia.comprobante.ui.shell.SectionView;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class NavigationService {

    private final SpringFXMLLoader loader;
    private final SectionRegistry registry;

    private StackPane contentPane;
    private SidebarMenu sidebarMenu;
    private SectionView currentView;
    private SectionId currentSection;

    public NavigationService(SpringFXMLLoader loader, SectionRegistry registry) {
        this.loader = loader;
        this.registry = registry;
    }

    public void bind(StackPane contentPane, SidebarMenu sidebarMenu) {
        this.contentPane = contentPane;
        this.sidebarMenu = sidebarMenu;
        this.sidebarMenu.setOnSelect(this::onSidebarSelect);
    }

    public void openSection(SectionId id) {
        SectionDescriptor descriptor = registry.descriptor(id);
        currentSection = id;
        currentView = null;
        sidebarMenu.configure(descriptor.sidebarConfig().get());
        try {
            Parent view = loader.load(descriptor.viewFxml());
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar " + descriptor.viewFxml(), e);
        }
    }

    public void refreshSidebar() {
        if (currentSection != null) {
            SectionDescriptor descriptor = registry.descriptor(currentSection);
            sidebarMenu.configure(descriptor.sidebarConfig().get());
        }
    }

    public void registerView(SectionView view) {
        this.currentView = view;
    }

    private void onSidebarSelect(SidebarItem item) {
        if (currentView != null) {
            currentView.onSidebarSelection(item);
        }
    }
}
