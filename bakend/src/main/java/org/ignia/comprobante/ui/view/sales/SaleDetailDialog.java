package org.ignia.comprobante.ui.view.sales;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.cliente.IClientService;
import org.ignia.comprobante.itemSales.ItemSaleDTO;
import org.ignia.comprobante.sales.SaleDTO;

import java.math.BigDecimal;
import java.util.function.Function;


public class SaleDetailDialog extends Dialog<Void> {

    private final IClientService clientService;

    public SaleDetailDialog(SaleDTO sale, IClientService clientService){
        this.clientService = clientService;

        setTitle("Detalle de venta N " + sale.getId());
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(10);

        GridPane summary = new GridPane();
        summary.setHgap(10);
        summary.setVgap(10);
        summary.addRow(0, new Label("Fecha", new Label(sale.getDate() == null ? "" : sale.getDate().toString())));
        summary.addRow(1, new Label("Cliente"), new Label(sale.getNameClient() == null ? "" : sale.getNameClient()));
        summary.addRow(2, new Label("DNI"), new Label(sale.getDniClient() == null ? "" : sale.getDniClient()));
        summary.addRow(3, new Label("IVA:"), new Label(sale.getTypeIva() == null ? "" : sale.getTypeIva()));
        summary.addRow(4, new Label("Base imponible"), new Label(String.format("%.2f", sale.getBaseImponible())));
        summary.addRow(6, new Label("IVA"), new Label(String.format("%.2f", sale.getIva())));
        summary.addRow(5, new Label("Descuento"), new Label(String.format("%.2f", sale.getDiscount())));
        summary.addRow(7, new Label("Total"), new Label(String.format("%.2f", sale.getTotalPrice())));


        //tabla de items
        Label itemsTitle = new Label ("Items de la venta");
        itemsTitle.setStyle("-fx-font-weight: bold;");

        TableView<ItemSaleDTO> itemsTable = new TableView<>();
        TableColumn<ItemSaleDTO, String> colProduct = col("Producto", ItemSaleDTO::getNameItem);
        TableColumn<ItemSaleDTO, String> colQuantity = col("Cantidad", i -> String.valueOf(i.getQuantity()));
        TableColumn<ItemSaleDTO, String> colPrice = col("Precio unitario", i -> String.format("%.2f", i.getUnitPrice()));
        TableColumn<ItemSaleDTO, String> colSubTotal = col("Subtotal", i -> String.format("%.2f", i.getSubTotal()));

        itemsTable.getColumns().addAll(colProduct, colQuantity, colPrice, colSubTotal);
        itemsTable.setItems(FXCollections.observableArrayList(sale.getItemsSales()));
        itemsTable.setPrefHeight(200);
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        content.getChildren().addAll(summary, itemsTitle, itemsTable);
        pane.setContent(content);
    }

    private Label label(String text){
        return new Label(text);
    }

    private Label text(String text){
        Label l = new Label(text);
        l.setWrapText(true);
        return l;
    }

    private<T> TableColumn<T, String> col(String title, Function<T, String> fn){
        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(data -> new SimpleStringProperty(fn.apply(data.getValue())));
        return c;
    }

    private String formatPrice(BigDecimal price){
        if (price == null) return "";
        return String.format("%.2f", price);
    }
}
