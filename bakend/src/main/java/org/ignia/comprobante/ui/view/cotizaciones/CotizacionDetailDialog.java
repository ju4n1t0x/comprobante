package org.ignia.comprobante.ui.view.cotizaciones;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.cotizacion.CotizacionDto;
import org.ignia.comprobante.itemCotizacion.ItemCotizacionDto;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public class CotizacionDetailDialog extends Dialog<Void> {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm");
    public CotizacionDetailDialog(CotizacionDto cotizacion) {

        setTitle("Detalle de cotizacion numero " + cotizacion.getId());
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().add(ButtonType.CLOSE);
        pane.setPrefSize(1250, 950);

        VBox content = new VBox(10);

        GridPane summary = new GridPane();
        summary.setHgap(10);
        summary.setVgap(10);
        summary.addRow(0, new Label("Cliente"), new Label(fullName(cotizacion)));
        summary.addRow(1, new Label("DNI"), new Label(cotizacion.getClientDni() == null ? "Sin DNI" : cotizacion.getClientDni()));
        summary.addRow(2, new Label("Computo"), new Label(cotizacion.getComputoName() == null ? "Sin computo" : cotizacion.getComputoName()));
        summary.addRow(3, new Label("Metros cuadrados"), new Label(cotizacion.getAreaM2() == null ? "0" : cotizacion.getAreaM2().toPlainString()));
        summary.addRow(4, new Label("Metros lineales"), new Label(cotizacion.getPerimeterML() == null ? "" : cotizacion.getPerimeterML().toPlainString()));
        summary.addRow(5, new Label("Subtotal"), new Label(format(cotizacion.getSubtotal())));
        summary.addRow(6, new Label("IVA"), new Label(format(cotizacion.getIva())));
        summary.addRow(7, new Label("Total"), new Label(format(cotizacion.getTotal())));
        summary.addRow(8, new Label("Fecha"), new Label(cotizacion.getCreatedAt() == null ? "" : cotizacion.getCreatedAt().format(FECHA)));
        summary.addRow(9, new Label("Estado"), new Label(cotizacion.getState() == null ? "" : cotizacion.getState().toString()));

        Label itemsTitle = new Label("Items de la cotizacion");
        itemsTitle.setStyle("-fx-font-weight: bold");

        TableView<ItemCotizacionDto> itemsTable = new TableView<>();
        TableColumn<ItemCotizacionDto, String> colProduct = col("Producto", ItemCotizacionDto::getProductName);
        TableColumn<ItemCotizacionDto, String> colUnit = col("Unidad", i -> i.getUnitType() == null ? "" : i.getUnitType().toString());
        TableColumn<ItemCotizacionDto, String> colQuantity = col("Cantidad", i -> i.getQuantity() == null ? "" : String.format("%." + i.getQuantityDecimals() + "f", i.getQuantity()));
        TableColumn<ItemCotizacionDto, String> colPrice = col("Precio unitario", i -> format(i.getUnitPrice()));
        TableColumn<ItemCotizacionDto, String> colSubTotal = col("Subtotal", i -> format(i.getSubTotal()));

        itemsTable.getColumns().addAll(colProduct, colUnit, colQuantity, colPrice, colSubTotal);
        itemsTable.setItems(FXCollections.observableArrayList(cotizacion.getItems()));
        itemsTable.getStyleClass().add("data-table");
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(itemsTable, Priority.ALWAYS);

        content.getChildren().addAll(summary, itemsTitle, itemsTable);
        pane.setContent(content);
    }

    private <T> TableColumn<T, String> col(String title, Function<T, String> fn) {

        TableColumn<T, String> c = new TableColumn<>(title);
        c.setCellValueFactory(data -> new SimpleStringProperty(fn.apply(data.getValue())));
        return c;
    }

    private String format(BigDecimal value) {
        return value == null ? "" : String.format("$%,.2f", value);
    }

    private static String fullName(CotizacionDto dto) {

        String name = dto.getClientName() == null ? "" : dto.getClientName().trim();
        String lastName = dto.getClientLastName() == null ? "" : dto.getClientLastName().trim();
        String fullName = (name + " " + lastName).trim();
        return fullName.isEmpty() ? "Sin cliente" : fullName;
    }
}
