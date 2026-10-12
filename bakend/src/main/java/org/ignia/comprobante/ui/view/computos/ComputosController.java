package org.ignia.comprobante.ui.view.computos;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.computos.IComputoCatalogService;
import org.ignia.comprobante.computos.config.ComputoConfig;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.model.*;
import org.ignia.comprobante.ui.shell.SectionView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComputosController implements SectionView {

    private static final int PAGE_SIZE = 12;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final IComputoCatalogService catalogService;
    private final NavigationService navigationService;

    private DataCard<ComputoConfig> dataCard;
    private String searchText = "";
    private int currentPage;

    public ComputosController(IComputoCatalogService catalogService, NavigationService navigationService) {
        this.catalogService = catalogService;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {

        navigationService.registerView(this);

        ActionBar bar = new ActionBar(
                new ActionBarConfig(
                        "CATALOGO . COMPUTOS",
                        "Buscar computo",
                        List.of()
                )
        );

        bar.setOnSearch(this::onSearch);
        toolbarSlot.getChildren().setAll(bar);

        dataCard = new DataCard<>(
                "COMPUTOS",
                "Configuraciones disponibles",
                columns(),
                List.of()
        );

        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);

        reload();
    }

    private List<ColumnDef<ComputoConfig>> columns() {
        return List.of(
                ColumnDef.of("Nombre", ComputoConfig::name),
                ColumnDef.of("Descripcion", config -> config.description() == null
                ? ""
                        : config.description()
                ),
                ColumnDef.of("Productos",
                        config -> String.valueOf(config.items().size()),
                        ColumnDef.Align.RIGHT)
        );
    }

    private void reload() {
        Page<ComputoConfig> page = catalogService.page(searchText, PageRequest.of(currentPage, PAGE_SIZE));

        dataCard.setData(PageData.of(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getSize(),
                page.getTotalElements()
        ));
    }

    private void goToPage(int page) {
        currentPage = page;
        reload();
    }

    private void onSearch(String search) {
        searchText = search == null ? "" : search.trim();
        currentPage = 0;
        reload();
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {
        currentPage = 0;
        reload();
    }
}
