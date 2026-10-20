package org.ignia.comprobante.ui.view.cotizaciones;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.cotizacion.CotizacionDto;
import org.ignia.comprobante.cotizacion.ICotizacionService;
import org.ignia.comprobante.cotizacion.State;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.components.DialogTheme;
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
public class CotizacionesController implements SectionView {

    private static final int PAGE_SIZE = 12;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;


    private final ICotizacionService cotizacionService;

    private final NavigationService navigationService;

    private DataCard<CotizacionDto> dataCard;
    private State stateFilter;
    private int currentPage;
    private String searchText = "";

    public CotizacionesController(ICotizacionService cotizacionService, NavigationService navigationService) {
        this.cotizacionService = cotizacionService;
        this.navigationService = navigationService;
    }

@FXML
    private void initialize() {

        navigationService.registerView(this);

        ActionBar bar = new ActionBar(new ActionBarConfig(
                null,
                "Buscar cotizacion por id",
                List.of()
        ));

        toolbarSlot.getChildren().setAll(bar);

        dataCard = new DataCard<>("COTIZACIONES", "Listado de cotizaciones", columns(), rowActions());
        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);
        bar.setOnSearch(this::onSearch);

        reload();
}

private List<ColumnDef<CotizacionDto>> columns() {

        return List.of(
                ColumnDef.of("Numero de cotizacion", c -> String.valueOf(c.getId()), 100),
                ColumnDef.of("Cliente", CotizacionesController::fullName, 200),
                ColumnDef.of("Computo", c -> c.getComputoName() == null ? "" : c.getComputoName(), 200),
                ColumnDef.of("Total", c -> c.getTotal() == null ? "" : String.format("$%,.2f", c.getTotal()), 130),
                ColumnDef.of("Fecha", c -> c.getCreatedAt() == null ? "" : c.getCreatedAt().toString(), 160),
                ColumnDef.of("Estado", c -> c.getState() == null ? "" : c.getState().toString(), 120)
        );
}

private List<RowActionDef<CotizacionDto>> rowActions() {

        return List.of(
                new RowActionDef<>("Ver detalle", this::openDetail),
                new RowActionDef<>("Cambiar estado", this::changeState)
        );
}

private void openDetail(CotizacionDto dto) {
        new CotizacionDetailDialog(dto).showAndWait();
}

private void changeState(CotizacionDto dto) {

    Dialog<State> dialog = new Dialog<>();
    dialog.setTitle("Cambiar estado");
    dialog.setHeaderText("Cotizacion numero " + dto.getId());

    DialogPane pane = dialog.getDialogPane();
    pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
    pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

    ChoiceBox<State> stateChoice = new ChoiceBox<>(
            FXCollections.observableArrayList(State.values()
            ));
            stateChoice.setValue(dto.getState());
            pane.setContent(stateChoice);

            dialog.setResultConverter(bt -> bt == ButtonType.OK ? stateChoice.getValue() : null);

            dialog.showAndWait().ifPresent(
                    newState -> {
                        try {
                            cotizacionService.updateState(dto.getId(), newState);
                            reload();
                            navigationService.refreshSidebar();
                        } catch (RuntimeException ex) {
                            {
                                showError(ex.getMessage());
                            }
                        }
                    });
}

@Override
    public void onSidebarSelection(SidebarItem item) {
        stateFilter = "all".equals(item.id()) ? null : State.valueOf(item.id());
        currentPage = 0;
        reload();
}

private void goToPage(int page) {
        currentPage = page;
        reload();
}

private void reload() {

    Page<CotizacionDto> page = cotizacionService.page(
            stateFilter,
            searchText,
            PageRequest.of(currentPage, PAGE_SIZE)
    );

    dataCard.setData(PageData.of(page.getContent(), page.getNumber(), page.getTotalPages(), page.getSize(), page.getTotalElements()));
}

private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        DialogTheme.apply(alert);
        alert.setTitle("No se pudo completar la acción");
        alert.setHeaderText(null);
        alert.showAndWait();
}

private void onSearch(String q) {
        searchText = q == null ? "" : q.trim();
        currentPage = 0;
        reload();
}

private static String fullName(CotizacionDto dto) {

        String name = dto.getClientName() == null ? "" : dto.getClientName().trim();
        String lastName = dto.getClientLastName() == null ? "" : dto.getClientLastName().trim();
        String fullName = (name + " " + lastName).trim();
        return fullName.isEmpty() ? "Sin cliente" : fullName;
}

}