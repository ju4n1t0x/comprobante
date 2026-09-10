package org.ignia.comprobante.ui.view.products;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.productos.ProductDto;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.model.ActionBarConfig;
import org.ignia.comprobante.ui.model.ButtonDef;
import org.ignia.comprobante.ui.model.ColumnDef;
import org.ignia.comprobante.ui.model.PageData;
import org.ignia.comprobante.ui.model.RowActionDef;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductsController {

    private static final int PAGE_SIZE = 12;
    private static final int LOW_STOCK = 20;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final IProductService productService;

    private DataCard<ProductDto> dataCard;
    private int currentPage;

    public ProductsController(IProductService productService) {
        this.productService = productService;
    }

    @FXML
    private void initialize() {
        ActionBar bar = new ActionBar(new ActionBarConfig(null, "Buscar producto por nombre...",
                List.of(
                        new ButtonDef("Exportar", ButtonDef.Style.GHOST, null),
                        new ButtonDef("+ Alta de producto", ButtonDef.Style.PRIMARY, null))));
        toolbarSlot.getChildren().setAll(bar);

        dataCard = new DataCard<>("PRODUCTOS", "Inventario maestro", columns(), rowActions());
        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);

        reload();
    }

    private List<ColumnDef<ProductDto>> columns() {
        return List.of(
                ColumnDef.of("SKU", p -> p.getProductUID() == null ? "" : String.valueOf(p.getProductUID())),
                ColumnDef.of("NOMBRE", ProductDto::getNameProduct),
                ColumnDef.of("CATEGORÍA", p -> p.getCategoryName() == null ? "" : p.getCategoryName()),
                ColumnDef.of("PRECIO", p -> formatPrice(p.getUnitPrice()), ColumnDef.Align.RIGHT),
                ColumnDef.of("STOCK", p -> p.getStock() == null ? "" : String.valueOf(p.getStock()),
                        ColumnDef.Align.RIGHT, p -> p.getStock() != null && p.getStock() <= LOW_STOCK ? "cell-danger" : "")
        );
    }

    private List<RowActionDef<ProductDto>> rowActions() {
        return List.of(
                new RowActionDef<>("Editar", null),
                new RowActionDef<>("Eliminar", null)
        );
    }

    private void reload() {
        Page<ProductDto> page = productService.page(PageRequest.of(currentPage, PAGE_SIZE));
        dataCard.setData(PageData.of(page.getContent(), page.getNumber(),
                page.getTotalPages(), page.getSize(), page.getTotalElements()));
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
}
