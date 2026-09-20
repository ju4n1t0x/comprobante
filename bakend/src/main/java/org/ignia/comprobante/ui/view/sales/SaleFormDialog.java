package org.ignia.comprobante.ui.view.sales;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.ignia.comprobante.cliente.IClientService;
import org.ignia.comprobante.itemSales.ItemSaleDTO;
import org.ignia.comprobante.productos.IProductService;
import org.ignia.comprobante.productos.ProductDto;
import org.ignia.comprobante.sales.SaleDTO;
import org.ignia.comprobante.sales.State;

import javafx.beans.binding.Bindings;
import java.math.BigDecimal;
import java.util.List;


public class SaleFormDialog extends Dialog<SaleDTO> {

    private final IClientService clientService;
    private final IProductService productService;

    //seccoin venta
    private final ChoiceBox<String> typeIvaField = new ChoiceBox<>();
    private final DatePicker dateField = new DatePicker();
    private final TextField discountField = new TextField();
    private final ChoiceBox<State> stateField = new ChoiceBox<>();
    //seccion cliente
    private Long selectdClientId = null;

    //seccion productos
    private final ChoiceBox<ProductDto> productChoice = new ChoiceBox<>();
    private final TextField quantityField = new TextField();
    private final ObservableList<ItemSaleDTO> itemsList = FXCollections.observableArrayList();
    private final TableView<ItemSaleDTO> itemsTable = new TableView<>();
    private final Label totalLabel = new Label("0.00");

    public SaleFormDialog(SaleDTO existing, IClientService clientService, IProductService productService) {
        this.clientService = clientService;
        this.productService = productService;

        setTitle(existing == null ? "Nueva venta" : "Editar venta");
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        // --- campos de la venta ---
        //selector para tipo de iva
        typeIvaField.setItems(FXCollections.observableArrayList("Consumidor final", "Excento", "Responsable inscripto"));
        stateField.setItems(FXCollections.observableArrayList(State.values()));

        //boton para seleccionar clientes
        Button selectClientBtn = new Button("Seleccionar cliente");
        Label selectClientLabel = new Label("No seleccionado");
        selectClientBtn.setOnAction(e -> {
            new ClientPickerDialog(clientService).showAndWait().ifPresent(client -> {
                selectdClientId = client.getId();
                selectClientLabel.setText(client.getName() + " " + client.getLastName() + " (DNI: " + client.getDni() + ")");
            });
        });

        discountField.setPromptText("0.00%");

        // --- seccion productos ---
        List<ProductDto> allProducts = productService.getAllProducts();
        productChoice.setItems(FXCollections.observableArrayList(allProducts));
        productChoice.setConverter(new StringConverter<>() {
            @Override
            public String toString(ProductDto p) {
                return p == null ? "" : p.getNameProduct() + " - $" + p.getUnitPrice();
            }
            @Override
            public ProductDto fromString(String s){
                return null;
            }
        });

        quantityField.setPromptText("Cantidad");
        quantityField.setPrefWidth(60);

        Button addProdBtn = new Button("+ Agregar");
        addProdBtn.setOnAction(e -> addProduct());

        HBox addRow = new HBox(10, productChoice, quantityField, addProdBtn);
        addRow.setAlignment(Pos.CENTER_LEFT);

        // --tabla de items --
        TableColumn<ItemSaleDTO, String> colProduct = new TableColumn<>("Producto");
        colProduct.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNameItem()));
        colProduct.setPrefWidth(200);

        TableColumn<ItemSaleDTO, String> colQuantity = new TableColumn<>("Cantidad");
        colQuantity.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getQuantity())));
        colQuantity.setPrefWidth(80);

        TableColumn<ItemSaleDTO, String> colPrice = new TableColumn<>("Precio unit.");
        colPrice.setCellValueFactory(data -> new SimpleStringProperty(formatPrice(data.getValue().getUnitPrice())));
        colPrice.setPrefWidth(100);

        TableColumn<ItemSaleDTO, String> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(data -> new SimpleStringProperty(formatPrice(data.getValue().getSubTotal())));
        colSubtotal.setPrefWidth(100);

        TableColumn<ItemSaleDTO, Void> colRemove = new TableColumn<>();
        colRemove.setPrefWidth(40);
        colRemove.setCellFactory(col -> new TableCell<>() {
            private final Button dellBtn = new Button("X");
            {
                dellBtn.setOnAction(e -> {
                    ItemSaleDTO item = getTableView().getItems().get(getIndex());
                    itemsList.remove(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : dellBtn);
            }
        });


        itemsTable.getColumns().addAll(colProduct, colQuantity, colPrice, colSubtotal, colRemove);
        itemsTable.setItems(itemsList);
        itemsTable.setPrefHeight(200);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        BooleanProperty itemsEmpty = new SimpleBooleanProperty(itemsList.isEmpty());
        itemsList.addListener((ListChangeListener<ItemSaleDTO>) c -> itemsEmpty.set(itemsList.isEmpty()));

        // -- Layout --
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(1, new Label("Tipo de IVA:"), typeIvaField);
        grid.addRow(2, new Label("Fecha:"), dateField);
        grid.addRow(3, new Label("Cliente:"), selectClientBtn, selectClientLabel);
        grid.addRow(5, new Label("Descuento:"), discountField);
        grid.addRow(8, new Label("Estado:"), stateField);
        VBox content = new VBox(10, grid, new Separator(), new Label("Productos"), addRow, itemsTable, totalLabel);
        pane.setContent(content);

        //prellenar si es edicion
        if (existing != null) {
            typeIvaField.setValue(existing.getTypeIva());
            dateField.setValue(existing.getDate());
            discountField.setText(existing.getDiscount() == null ? "" : existing.getDiscount().toPlainString());
            stateField.setValue(existing.getState());
            selectdClientId = existing.getClientId();
            selectClientLabel.setText(existing.getNameClient() + " (DNI: " + existing.getDniClient() + ")");

            if (existing.getItemsSales() != null) {
                itemsList.addAll(existing.getItemsSales());
                updateTotal();
            }
        }

        // -- validacion --
        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        (existing == null && selectdClientId == null)
                                || typeIvaField.getValue() == null
                                || dateField.getValue() == null
                                || itemsList.isEmpty(),
                typeIvaField.valueProperty(), dateField.valueProperty(), itemsEmpty));

        // -- resultado --
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return SaleDTO.builder()
                        .id(existing == null ? null : existing.getId())
                        .typeIva(typeIvaField.getValue())
                        .date(dateField.getValue())
                        .clientId(selectdClientId)
                        .discount(discountField.getText().isBlank() ? null : new BigDecimal(discountField.getText().trim()))
                        .state(stateField.getValue())
                        .itemsSales(List.copyOf(itemsList))
                        .build();
            }
            return null;
        });
    }
        private void addProduct() {
        ProductDto product = productChoice.getValue();
        if (product == null) return;

        String quantityText = quantityField.getText();
        if (quantityText == null || quantityText.isBlank()) return;

        int quantity;
        try{
            quantity = Integer.parseInt(quantityText.trim());
            if (quantity <= 0) return;
        } catch (NumberFormatException e) {
            return;
        }

        ItemSaleDTO item = ItemSaleDTO.builder()
                .productId(product.getId())
                .nameItem(product.getNameProduct())
                .unitPrice(product.getUnitPrice())
                .quantity(quantity)
                .subTotal(product.getUnitPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();

        itemsList.add(item);
        productChoice.setValue(null);
        quantityField.clear();

        }

        private void updateTotal(){
        BigDecimal baseimponible = itemsList.stream()
                .map(ItemSaleDTO::getSubTotal)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal iva = BigDecimal.ZERO;
        String typeIva = typeIvaField.getValue();
        if (typeIva != null && !"Exento".equals(typeIva)){
            iva = baseimponible.multiply(new BigDecimal("0.21"));
        }

        //base imponible + iva
        BigDecimal subTotal = baseimponible.add(iva);

        //descuento porcentual
        BigDecimal discountPercentaje = BigDecimal.ZERO;

        if(!discountField.getText().isBlank()){
            try{
                discountPercentaje = new BigDecimal(discountField.getText().trim());
            } catch(NumberFormatException e){
                discountPercentaje = BigDecimal.ZERO;
            }
        }

        //descuento sobre base immponible + iva
            BigDecimal discountAmount = subTotal
                    .multiply(discountPercentaje)
                    .divide(new BigDecimal("100"));

        BigDecimal total = subTotal.subtract(discountAmount);
        totalLabel.setText("Total: " + formatPrice(total));
        }

        private String formatPrice(BigDecimal price){
        if (price == null) return "$0.00";
        return String.format("$%.2f", price);
        }



    }
