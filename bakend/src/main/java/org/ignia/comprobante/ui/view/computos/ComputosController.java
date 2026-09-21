package org.ignia.comprobante.ui.view.computos;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.computos.ComputoDTO;
import org.ignia.comprobante.computos.IComputoService;
import org.ignia.comprobante.productos.IProductService;
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
import java.util.Optional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComputosController implements SectionView {

    private static final int PAGE_SIZE = 10;
    private final IProductService productService;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final IComputoService computoService;
    private final NavigationService navigationService;

    private DataCard<ComputoDTO> dataCard;
    private String searchText = "";
    private int currentPage;

    public ComputosController(IComputoService computoService, NavigationService navigationService, IProductService productService) {
        this.computoService = computoService;
        this.navigationService = navigationService;
        this.productService = productService;
    }

    @FXML
    private void initialize(){
        navigationService.registerView(this);

        ActionBar bar = new ActionBar(
                new ActionBarConfig(
                        "CATALOGO . COMPUTOS",
                        "Buscar computo",
                        List.of(new ButtonDef(
                                "+ Nuevo computo",
                                ButtonDef.Style.PRIMARY,
                                this::openCreate
                        ))
                )
        );

        toolbarSlot.getChildren().setAll(bar);
        bar.setOnSearch(this::onSearch);

        dataCard = new DataCard<>(
                "COMPUTOS",
                "Listado de computos",
                columns(),
                rowActions()
        );

        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);

        reload();
    }

    private List<ColumnDef<ComputoDTO>> columns(){
        return List.of(
                ColumnDef.of("NOMBRE", ComputoDTO::getName),
                ColumnDef.of(
                        "DESCRIPCIÓN",
                        dto -> dto.getDescription() == null ? "" : dto.getDescription()
                ),
                ColumnDef.of(
                        "PRODUCTOS",
                        dto -> String.valueOf(dto.getItemCount()),
                        ColumnDef.Align.RIGHT
                )
        );
    }

    private List<RowActionDef<ComputoDTO>> rowActions() {
        return List.of(
                new RowActionDef<>("Editar", this::openEdit),
                new RowActionDef<>("Eliminar", this::delete)
        );
    }

    private void reload() {
        Page<ComputoDTO> page = computoService.page(
                searchText,
                PageRequest.of(currentPage, PAGE_SIZE)
        );

        dataCard.setData(PageData.of(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getSize(),
                page.getTotalElements()
        ));
    }

    private void openCreate() {
        ComputoDTO complete = ComputoDTO.builder().build();
        new ComputoFormDialog(null, productService).showAndWait().ifPresent(dto -> {
            try {
                computoService.save(dto);
                currentPage = 0;
                reload();
                navigationService.refreshSidebar();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void openEdit(ComputoDTO dto) {
        ComputoDTO complete = computoService.findById(dto.getId());

        new ComputoFormDialog(complete, productService).showAndWait().ifPresent(updated -> {
            try {
                computoService.update(dto.getId(), updated);
                reload();
                navigationService.refreshSidebar();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void delete(ComputoDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar computo");
        confirm.setHeaderText("¿Eliminar «" + dto.getName() + "»?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                computoService.delete(dto.getId());
                currentPage = 0;
                reload();
                navigationService.refreshSidebar();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        }
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

    private void showError(String message) {
        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                message == null ? "Error desconocido" : message,
                ButtonType.OK
        );
        alert.showAndWait();
    }


}
