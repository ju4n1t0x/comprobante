package org.ignia.comprobante.ui.view.cotizarCubierta;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.ItemComputo.ItemComputoDTO;
import org.ignia.comprobante.computos.ComputoDTO;
import org.ignia.comprobante.computos.IComputoService;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.shell.SectionView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CotizarController implements SectionView {

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final IComputoService computoService;
    private final NavigationService navigationService;

    private ComboBox<ComputoDTO> computoBox;
    private TextField alturaField;
    private TextField largoField;
    private TextField desperdicioField;
    private VBox resultBox;

    public CotizarController(IComputoService computoService, NavigationService navigationService) {
        this.computoService = computoService;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {
        navigationService.registerView(this);
        computoBox = new ComboBox<>();
        alturaField = new TextField();
        largoField = new TextField();
        desperdicioField = new TextField("0");
        resultBox = new VBox(8);

        List<ComputoDTO> computos = computoService.findAll();
        computoBox.setItems(FXCollections.observableArrayList(computos));

        computoBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ComputoDTO dto) {
                return dto == null ? "" : dto.getName();
            }

            @Override
            public ComputoDTO fromString(String string) {
                return null;
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        form.addRow(0, new Label("Tipo de computo"), computoBox);
        form.addRow(1, new Label("Altura (m)"), alturaField);
        form.addRow(2, new Label("Largo (m)"), largoField);
        form.addRow(3, new Label("Desperdicio (%)"), desperdicioField);

        Button calculate = new Button("Calcular");
        calculate.setOnAction(e -> calculate());

        VBox content = new VBox(
                15,
                form,
                calculate,
                new Separator(),
                resultBox
        );
        content.setPadding(new Insets(20));

        contentSlot.getChildren().setAll(new ScrollPane(content));
    }

    private void calculate() {
        try {
            ComputoDTO computo = computoBox.getValue();

            if (computo == null) {
                throw new IllegalArgumentException("Seleccioná un computo");
            }

            BigDecimal altura = decimal(alturaField);
            BigDecimal largo = decimal(largoField);
            BigDecimal desperdicio = decimal(desperdicioField);

            BigDecimal superficie = altura.multiply(largo);
            BigDecimal factor = BigDecimal.ONE.add(
                    desperdicio.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
            );

            BigDecimal superficieConDesperdicio = superficie.multiply(factor);

            resultBox.getChildren().clear();
            resultBox.getChildren().add(new Label(
                    "Superficie: " + format(superficie) + " m²"
            ));
            resultBox.getChildren().add(new Label(
                    "Con desperdicio: " + format(superficieConDesperdicio) + " m²"
            ));

            BigDecimal subtotal = BigDecimal.ZERO;

            for (ItemComputoDTO item : computo.getItems()) {
                BigDecimal quantity = switch (item.getUnitType()) {
                    case M2 -> item.getQuantity().multiply(superficieConDesperdicio);
                    case LINEAL -> item.getQuantity().multiply(largo).multiply(factor);
                    case UNIDAD -> item.getQuantity();
                };

                BigDecimal itemSubtotal = quantity.multiply(
                        item.getUnitPrice() == null
                                ? BigDecimal.ZERO
                                : item.getUnitPrice()
                );

                subtotal = subtotal.add(itemSubtotal);

                resultBox.getChildren().add(new Label(
                        item.getProductName()
                                + " | Cantidad: " + format(quantity)
                                + " | Subtotal: $" + format(itemSubtotal)
                ));
            }

            BigDecimal iva = subtotal.multiply(new BigDecimal("0.21"));
            BigDecimal total = subtotal.add(iva);

            resultBox.getChildren().add(new Separator());
            resultBox.getChildren().add(new Label("Subtotal: $" + format(subtotal)));
            resultBox.getChildren().add(new Label("IVA 21%: $" + format(iva)));
            resultBox.getChildren().add(new Label("TOTAL: $" + format(total)));

        } catch (RuntimeException ex) {
            new Alert(
                    Alert.AlertType.ERROR,
                    ex.getMessage(),
                    ButtonType.OK
            ).showAndWait();
        }
    }

    private BigDecimal decimal(TextField field) {
        if (field.getText() == null || field.getText().isBlank()) {
            throw new IllegalArgumentException("Completá todos los valores");
        }

        BigDecimal value = new BigDecimal(
                field.getText().trim().replace(",", ".")
        );

        if (value.signum() < 0) {
            throw new IllegalArgumentException("Los valores no pueden ser negativos");
        }

        return value;
    }

    private String format(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {

    }
}
