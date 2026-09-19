package org.ignia.comprobante.ui.view.products;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.categories.CategoryDto;
import org.ignia.comprobante.categories.ICategoryService;
import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.productos.ProductDto;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductsController implements SectionView {

    private static final int PAGE_SIZE = 12;
    private static final int LOW_STOCK = 20;


    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final NavigationService navigationService;

    private DataCard<ProductDto> dataCard;
    private Long filter;
    private int currentPage;
    private String searchText = "";

    public ProductsController(IProductService productService, NavigationService navigationService, ICategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {
        navigationService.registerView(this);
        ActionBar bar = new ActionBar(new ActionBarConfig(null, "Buscar producto por nombre...",
                List.of(
                        new ButtonDef("Exportar", ButtonDef.Style.GHOST, null),
                        new ButtonDef("+ Alta de producto", ButtonDef.Style.PRIMARY, this::openCreate))));
        toolbarSlot.getChildren().setAll(bar);

        dataCard = new DataCard<>("PRODUCTOS", "Inventario maestro", columns(), rowActions());
        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);
        bar.setOnSearch(this::onSearch);

        reload();
    }

    private List<ColumnDef<ProductDto>> columns() {
        return List.of(
                ColumnDef.of("SKU", p -> p.getProductUID() == null ? "" : String.valueOf(p.getProductUID()),150),
                ColumnDef.of("NOMBRE", ProductDto::getNameProduct, 400),
                ColumnDef.of("CATEGORÍA", p -> p.getCategoryName() == null ? "" : p.getCategoryName(), 200),
                ColumnDef.of("PRECIO", p -> formatPrice(p.getUnitPrice()), ColumnDef.Align.RIGHT, 110),
                ColumnDef.of("PORCENTAJE DE GANANCIA", p -> p.getProfitPercentage() == null ? "" : String.format("%.2f%%", p.getProfitPercentage()), ColumnDef.Align.RIGHT, 110),
                ColumnDef.of("PRECIO TOTAL", p -> formatPrice(p.getTotalPrice()), ColumnDef.Align.RIGHT, 130),
                ColumnDef.of("STOCK", p -> p.getStock() == null ? "" : String.valueOf(p.getStock()), ColumnDef.Align.RIGHT, p -> p.getStock() != null && p.getStock() <= LOW_STOCK ? "cell-danger" : "", 80),
                ColumnDef.of("ACTIVO", p -> p.isActive() ? "Si" : "No", 70));
    }

    private List<RowActionDef<ProductDto>> rowActions() {
        return List.of(
                new RowActionDef<>("Editar", this::openEdit),
                new RowActionDef<>("Eliminar", this::delete)
        );
    }

    private void goToPage(int page) {
        currentPage = page;
        reload();
    }

    private String formatPrice(BigDecimal price) {
        if (price == null) {
            return "";
        }
        return String.format("$%,d", price.setScale(0, RoundingMode.HALF_UP).longValue());
    }

    @Override
    public void onSidebarSelection(SidebarItem item){
        String id = item.id();
        if ("all".equals(id)) {
            filter = null;
        } else if (id.startsWith("cat-")) {
            filter = Long.valueOf(id.substring(4));
        } else if ("nostock".equals(id)) {
            filter = null;
        }
        currentPage = 0;
        reload();
    }

    private void openCreate() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        new ProductFormDialog(null, categories).showAndWait().ifPresent(dto -> {
            productService.saveProduct(dto);
            currentPage = 0;
            reload();
            navigationService.refreshSidebar();
        });
    }

    private void openEdit(ProductDto dto){
        List<CategoryDto> categories = categoryService.getAllCategories();
        new ProductFormDialog(dto, categories).showAndWait().ifPresent(updated -> {
            productService.updateProduct(dto.getId(), updated);
            reload();
            navigationService.refreshSidebar();
        });
    }

    private void delete(ProductDto dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        styleDialog(confirm);
        confirm.setTitle("Eliminar producto");
        confirm.setHeaderText("¿Eliminar «" + dto.getNameProduct() + "»?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            try {
                productService.deleteProduct(dto.getId());
                currentPage = 0;
                reload();
                navigationService.refreshSidebar();
            } catch (ConflictException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        styleDialog(alert);
        alert.setTitle("No se pudo completar");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
    }

    private void onSearch(String q) {
        searchText = q == null ? "" : q.trim();
        currentPage = 0;
        reload();
    }

    private void reload() {
        Page<ProductDto> page = productService.page(searchText, filter, PageRequest.of(currentPage, PAGE_SIZE));
        dataCard.setData(PageData.of(page.getContent(), page.getNumber(),
                page.getTotalPages(), page.getSize(), page.getTotalElements()));
    }
}
