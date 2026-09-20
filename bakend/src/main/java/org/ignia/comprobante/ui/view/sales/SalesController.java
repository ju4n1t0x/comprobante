package org.ignia.comprobante.ui.view.sales;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.cliente.IClientService;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.sales.ISalesService;
import org.ignia.comprobante.sales.SaleDTO;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.model.*;
import org.ignia.comprobante.ui.shell.SectionView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SalesController implements SectionView {

    private static final int PAGE_SIZE = 12;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    @Autowired
    private ISalesService salesService;

    @Autowired
    private NavigationService navigationService;

    private DataCard<SaleDTO> dataCard;
    private String sidebarFilter;
    private int currentPage;
    private String searchText = "";
    @Autowired
    private IClientService clientService;
    @Autowired
    private IProductService productService;

    @FXML
    private void initialize() {
        navigationService.registerView(this);
        ActionBar bar = new ActionBar(new ActionBarConfig(null, "Buscar venta por cliente o dni",
                List.of(
                        new ButtonDef("+ Nueva venta", ButtonDef.Style.PRIMARY, this::openCreate))));
        toolbarSlot.getChildren().setAll(bar);

        dataCard = new DataCard<>("Ventas", "Listado de ventas", columns(), rowActions());
        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);
        bar.setOnSearch(this::onSearch);

        reload();

    }

    private List<ColumnDef<SaleDTO>> columns(){
        return List.of(
                ColumnDef.of("N de venta", s -> String.valueOf(s.getId()), 80),
                ColumnDef.of("Fecha", s -> s.getDate() == null ? "" : s.getDate().toString(), 120),
                ColumnDef.of("Cliente", SaleDTO::getNameClient, 200),
                ColumnDef.of("DNI", SaleDTO::getDniClient, 120),
                ColumnDef.of("Base imponible", s -> String.format("%.2f", s.getBaseImponible()), 120),
                ColumnDef.of("Descuento", s -> String.format("%.2f", s.getDiscount()), 120),
                ColumnDef.of("IVA", s -> String.format("%.2f", s.getIva()), 120),
                ColumnDef.of("Total", s -> String.format("%.2f", s.getTotalPrice()), 120),
                ColumnDef.of("Estado", s -> s.getState() == null ? "" : s.getState().toString(), 120)
        );
    }

    private List<RowActionDef<SaleDTO>> rowActions(){
        return List.of(
                new RowActionDef<>("Editar", this::openEdit),
                new RowActionDef<>("Eliminar", this::delete),
                new RowActionDef<>("Ver detalle", this::openDetail)
        );
    }

    private void openDetail(SaleDTO dto){
        new SaleDetailDialog(dto, clientService).showAndWait();
    }

    private void openCreate(){
        new SaleFormDialog(null, clientService, productService).showAndWait().ifPresent(dto -> {
            try {
                salesService.createSale(dto);
                currentPage = 0;
                reload();
                navigationService.refreshSidebar();
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        });
    }

    private void openEdit(SaleDTO dto){
        new SaleFormDialog(dto, clientService, productService).showAndWait().ifPresent(updated -> {
            try{
            salesService.updateSale(dto.getId(), updated);
            reload();
            navigationService.refreshSidebar();
        } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    };

    private void delete(SaleDTO dto){
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        styleDialog(confirm);
        confirm.setTitle("Eliminar venta");
        confirm.setHeaderText("¿Está seguro que desea eliminar la venta N " + dto.getId() + "?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            salesService.deleteSale(dto.getId());
            currentPage = 0;
            reload();
            navigationService.refreshSidebar();
        }
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {
        String id = item.id();
        sidebarFilter = "all".equals(id) ? null : id;
        currentPage = 0;
        reload();
    }

    private void goToPage(int page){
        currentPage = page;
        reload();
    }

    private void onSearch(String q){
        searchText = q == null ? "" : q.trim();
        currentPage = 0;
        reload();
    }

    private void reload(){
        Page<SaleDTO> page = salesService.page(searchText, PageRequest.of(currentPage, PAGE_SIZE));
        dataCard.setData(PageData.of(page.getContent(), page.getNumber(),
                page.getTotalPages(), page.getSize(), page.getTotalElements()));
    }

    private String formatPrice(BigDecimal price){
        if (price == null) return "";
        return String.format("$%,.2f", price);
    }

    private void styleDialog(Dialog<?> dialog){
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
    }

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        styleDialog(alert);
        alert.setTitle("No se pudo completar la acción");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
