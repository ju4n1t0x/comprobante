package org.ignia.comprobante.ui.view.products;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.ignia.comprobante.categories.CategoryDto;
import org.ignia.comprobante.categories.CategoryModel;
import org.ignia.comprobante.productos.ProductDto;

import java.math.BigDecimal;
import java.util.List;


public class ProductFormDialog extends Dialog<ProductDto> {

    private final TextField nameField = new TextField();
    private TextField unitPriceField = new TextField();
    private TextField profitPercentage = new TextField();
    private TextField stockField = new TextField();
    private CheckBox active = new CheckBox("Activo");
    private ChoiceBox<CategoryDto> categoryName = new ChoiceBox<>();

    public ProductFormDialog(ProductDto existing, List<CategoryDto> categories){
        categoryName.setItems(FXCollections.observableList(categories));
        categoryName.setConverter(new StringConverter<CategoryDto>(){
            public String toString(CategoryDto c){
                return c == null ? "" : c.getName();
            }
            public CategoryDto fromString(String s){
                return null;
            }
        });

        setTitle(existing == null ? "Nuevo producto" : "Editar producto");
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nombre"), nameField);
        grid.addRow(1, new Label("Precio unitario"), unitPriceField);
        grid.addRow(2, new Label("Porcentaje de ganancia"), profitPercentage);
        grid.addRow(3, new Label("Stock"), stockField);
        grid.addRow(4, new Label("Categoría"), categoryName);
        grid.addRow(5, active);
        pane.setContent(grid);

        if (existing != null){
            nameField.setText(existing.getNameProduct());
            unitPriceField.setText(String.valueOf(existing.getUnitPrice() == null ? "" : existing.getUnitPrice()));
            profitPercentage.setText(String.valueOf(existing.getProfitPercentage() == null ? "" : existing.getProfitPercentage()));
            stockField.setText(String.valueOf(existing.getStock() == null ? "" : existing.getStock()));
            active.setSelected(existing.isActive());
            if (existing.getCategoryId() != null) {
                categoryName.setValue(categories.stream().filter(
                        c->c.getId().equals(existing.getCategoryId())).findFirst().orElse(null));
            }
        }

        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> nameField.getText() == null || nameField.getText().isBlank(),
                nameField.textProperty()));
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK){
                return ProductDto.builder()
                        .id(existing == null ? null : existing.getId())
                        .productUID(existing == null ? null : existing.getProductUID())
                        .nameProduct(nameField.getText().trim())
                        .unitPrice(unitPriceField.getText().isBlank() ? null : new BigDecimal(unitPriceField.getText().trim()))
                        .profitPercentage(profitPercentage.getText().isBlank() ? null : new BigDecimal(profitPercentage.getText().trim()))
                        .stock(stockField.getText().isBlank() ? null : Integer.valueOf(stockField.getText().trim()))
                        .active(active.isSelected())
                        .categoryId(categoryName.getValue() == null ? null : categoryName.getValue().getId())
                        .categoryName(categoryName.getValue() == null ? null : categoryName.getValue().getName())
                        .build();
            }
            return null;
        });
    }
}
