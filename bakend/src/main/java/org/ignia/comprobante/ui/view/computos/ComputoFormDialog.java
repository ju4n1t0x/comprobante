package org.ignia.comprobante.ui.view.computos;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.ItemComputo.ItemComputoDTO;
import org.ignia.comprobante.ItemComputo.UnitType;
import org.ignia.comprobante.computos.ComputoDTO;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.productos.ProductDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ComputoFormDialog extends Dialog<ComputoDTO> {

    private final TextField nameField = new TextField();
    private final TextField descriptionField = new TextField();
    private final VBox itemsBox = new VBox(8);
    private final List<ItemRow> rows = new ArrayList<>();

    private final List<ProductDto> products;

    public ComputoFormDialog(ComputoDTO existing, IProductService productService) {
        this.products = productService.getAllProducts();


        setTitle(existing == null ? "Nuevo computo" : "Editar computo");
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane header = new GridPane();
        header.setHgap(10);
        header.setVgap(10);
        header.addRow(0, new Label("Nombre"), nameField);
        header.addRow(1, new Label("Descripción"), descriptionField);

        Button addButton = new Button("+ Agregar producto");
        addButton.setOnAction(e -> addRow(null));

        VBox content = new VBox(
                12,
                header,
                new Label("Productos"),
                itemsBox,
                addButton
        );
        content.setPadding(new Insets(20));

        pane.setContent(new ScrollPane(content));

        if (existing != null) {
            nameField.setText(existing.getName());
            descriptionField.setText(existing.getDescription());

            existing.getItems().forEach(this::addRow);
        }

        if (rows.isEmpty()) {
            addRow(null);
        }

        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                nameField.textProperty().isEmpty()
        );

        setResultConverter(buttonType -> {
            if (buttonType != ButtonType.OK) {
                return null;
            }

            List<ItemComputoDTO> items = rows.stream()
                    .map(ItemRow::toDto)
                    .toList();

            return ComputoDTO.builder()
                    .id(existing == null ? null : existing.getId())
                    .name(nameField.getText().trim())
                    .description(descriptionField.getText().trim())
                    .items(items)
                    .build();
        });
    }

    private void addRow(ItemComputoDTO existing) {
        ItemRow row = new ItemRow(existing);
        rows.add(row);
        itemsBox.getChildren().add(row.view);
    }

    private final class ItemRow {

        private final ComboBox<ProductDto> productBox = new ComboBox<>();
        private final TextField quantityField = new TextField();
        private final ComboBox<UnitType> unitBox =
                new ComboBox<>(FXCollections.observableArrayList(UnitType.values()));
        private final HBox view;

        private ItemRow(ItemComputoDTO existing) {
            productBox.setItems(FXCollections.observableArrayList(products));
            productBox.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(ProductDto product) {
                    return product == null
                            ? ""
                            : product.getProductUID() + " - " + product.getNameProduct();
                }

                @Override
                public ProductDto fromString(String string) {
                    return null;
                }
            });

            if (existing != null) {
                productBox.setValue(products.stream()
                        .filter(p -> p.getId().equals(existing.getProductId()))
                        .findFirst()
                        .orElse(null));

                quantityField.setText(existing.getQuantity().toPlainString());
                unitBox.setValue(existing.getUnitType());
            }

            if (unitBox.getValue() == null) {
                unitBox.setValue(UnitType.M2);
            }

            Button remove = new Button("X");
            view = new HBox(
                    8,
                    productBox,
                    quantityField,
                    unitBox,
                    remove
            );
            remove.setOnAction(e -> {
                rows.remove(this);
                itemsBox.getChildren().remove(view);
            });

        }

        private ItemComputoDTO toDto() {
            if (productBox.getValue() == null) {
                throw new IllegalArgumentException("Seleccioná un producto");
            }

            BigDecimal quantity = new BigDecimal(quantityField.getText().trim());

            return ItemComputoDTO.builder()
                    .productId(productBox.getValue().getId())
                    .productName(productBox.getValue().getNameProduct())
                    .unitPrice(productBox.getValue().getUnitPrice())
                    .quantity(quantity)
                    .unitType(unitBox.getValue())
                    .build();
        }
    }
}
