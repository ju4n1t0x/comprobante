package org.ignia.comprobante.ui.view.categories;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.categories.CategoryDto;
import org.ignia.comprobante.categories.ICategoryService;
import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.model.ActionBarConfig;
import org.ignia.comprobante.ui.model.ButtonDef;
import org.ignia.comprobante.ui.model.ColumnDef;
import org.ignia.comprobante.ui.model.PageData;
import org.ignia.comprobante.ui.model.RowActionDef;
import org.ignia.comprobante.ui.model.SidebarItem;
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
public class CategoriesController implements SectionView {

    private static final int PAGE_SIZE = 8;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final ICategoryService categoryService;
    private final NavigationService navigation;

    private DataCard<CategoryDto> dataCard;
    private Long filter;
    private int currentPage;

    public CategoriesController(ICategoryService categoryService, NavigationService navigation) {
        this.categoryService = categoryService;
        this.navigation = navigation;
    }

    @FXML
    private void initialize() {
        navigation.registerView(this);

        ActionBar bar = new ActionBar(new ActionBarConfig("CATÁLOGO · RUBROS", null,
                List.of(new ButtonDef("+ Crear categoría", ButtonDef.Style.PRIMARY, this::openCreate))));
        toolbarSlot.getChildren().setAll(bar);

        dataCard = new DataCard<>("CATEGORÍAS", "Estructura de catálogo", columns(), rowActions());
        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().setAll(dataCard);

        reload();
    }

    private List<ColumnDef<CategoryDto>> columns() {
        return List.of(
                ColumnDef.of("CÓDIGO", CategoryDto::getCategoryUID),
                ColumnDef.of("NOMBRE", CategoryDto::getName),
                ColumnDef.of("DESCRIPCIÓN", c -> c.getDescription() == null ? "" : c.getDescription()),
                ColumnDef.of("PRODUCTOS", c -> String.valueOf(c.getProductCount()), ColumnDef.Align.RIGHT)
        );
    }

    private List<RowActionDef<CategoryDto>> rowActions() {
        return List.of(
                new RowActionDef<>("Editar", this::openEdit),
                new RowActionDef<>("Eliminar", this::delete)
        );
    }

    private void reload() {
        Page<CategoryDto> page = categoryService.page(filter, PageRequest.of(currentPage, PAGE_SIZE));
        dataCard.setData(PageData.of(page.getContent(), page.getNumber(),
                page.getTotalPages(), page.getSize(), page.getTotalElements()));
    }

    private void goToPage(int page) {
        currentPage = page;
        reload();
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {
        filter = "all".equals(item.id()) ? null : Long.valueOf(item.id());
        currentPage = 0;
        reload();
    }

    private void openCreate() {
        new CategoryFormDialog(null).showAndWait().ifPresent(dto -> {
            categoryService.saveCategory(dto);
            currentPage = 0;
            reload();
            navigation.refreshSidebar();
        });
    }

    private void openEdit(CategoryDto dto) {
        new CategoryFormDialog(dto).showAndWait().ifPresent(updated -> {
            categoryService.updateCategory(dto.getId(), updated);
            reload();
            navigation.refreshSidebar();
        });
    }

    private void delete(CategoryDto dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        styleDialog(confirm);
        confirm.setTitle("Eliminar categoría");
        confirm.setHeaderText("¿Eliminar «" + dto.getName() + "»?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                categoryService.deleteCategory(dto.getId());
                currentPage = 0;
                reload();
                navigation.refreshSidebar();
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
}
