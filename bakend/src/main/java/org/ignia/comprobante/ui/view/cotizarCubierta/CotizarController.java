package org.ignia.comprobante.ui.view.cotizarCubierta;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.ignia.comprobante.computos.IComputoCatalogService;
import org.ignia.comprobante.computos.config.ComputoConfig;
import org.ignia.comprobante.computos.config.ParameterConfig;
import org.ignia.comprobante.computos.result.ComputedItem;
import org.ignia.comprobante.computos.result.ComputedQuote;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DialogTheme;
import org.ignia.comprobante.ui.model.ActionBarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.shell.SectionView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class CotizarController implements SectionView {

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    private final IComputoCatalogService catalogService;
    private final NavigationService navigationService;

    private ComboBox<ComputoConfig> computoBox;
    private GridPane parameterForm;
    private VBox resultBox;

    private final Map<String, TextField> parameterFields =
            new LinkedHashMap<>();

    public CotizarController(
            IComputoCatalogService catalogService,
            NavigationService navigationService
    ) {
        this.catalogService = catalogService;
        this.navigationService = navigationService;
    }

    @FXML
    private void initialize() {
        navigationService.registerView(this);

        ActionBar bar = new ActionBar(
                new ActionBarConfig(
                        "COTIZAR CUBIERTA",
                        null,
                        List.of()
                )
        );

        toolbarSlot.getChildren().setAll(bar);

        computoBox = new ComboBox<>(
                FXCollections.observableArrayList(
                        catalogService.findAll()
                )
        );
        computoBox.getStyleClass().add("quote-combo");
        computoBox.setMaxWidth(Double.MAX_VALUE);

        computoBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ComputoConfig config) {
                return config == null ? "" : config.name();
            }

            @Override
            public ComputoConfig fromString(String value) {
                return null;
            }
        });

        parameterForm = new GridPane();
        parameterForm.getStyleClass().add("quote-parameters");
        parameterForm.setHgap(12);
        parameterForm.setVgap(12);

        resultBox = new VBox(0);
        resultBox.getStyleClass().add("quote-result-list");

        computoBox.valueProperty().addListener(
                (observable, oldValue, newValue) ->
                        renderParameters(newValue)
        );

        VBox inputCard = buildInputCard();
        VBox resultCard = buildResultCard();

        VBox content = new VBox(
                18,
                inputCard,
                resultCard
        );
        content.getStyleClass().add("quote-content");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("quote-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        contentSlot.getChildren().setAll(scrollPane);
    }

    private VBox buildInputCard() {
        Label title = new Label("DATOS DE COTIZACIÓN");
        title.getStyleClass().add("quote-card-title");

        Label subtitle = new Label(
                "Seleccioná el tipo de cubierta e ingresá las medidas"
        );
        subtitle.getStyleClass().add("quote-card-subtitle");

        GridPane form = new GridPane();
        form.getStyleClass().add("quote-form");
        form.setHgap(14);
        form.setVgap(12);

        Label computoLabel = new Label("Tipo de computo");
        computoLabel.getStyleClass().add("quote-label");

        form.add(computoLabel, 0, 0);
        form.add(computoBox, 1, 0);

        form.add(parameterForm, 0, 1, 2, 1);

        Button calculate = new Button("Calcular cotización");
        calculate.getStyleClass().add("btn-primary");
        calculate.setOnAction(event -> calculate());

        VBox card = new VBox(
                14,
                title,
                subtitle,
                form,
                calculate
        );

        card.getStyleClass().addAll(
                "card",
                "quote-input-card"
        );

        return card;
    }

    private VBox buildResultCard() {
        Label title = new Label("RESULTADO DE COTIZACIÓN");
        title.getStyleClass().add("quote-card-title");

        Label subtitle = new Label(
                "Detalle de materiales y costos"
        );
        subtitle.getStyleClass().add("quote-card-subtitle");

        showEmptyResult();

        VBox card = new VBox(
                14,
                title,
                subtitle,
                resultBox
        );

        card.getStyleClass().addAll(
                "card",
                "quote-result-card"
        );

        return card;
    }

    private void renderParameters(ComputoConfig computo) {
        parameterFields.clear();
        parameterForm.getChildren().clear();
        resultBox.getChildren().clear();

        showEmptyResult();

        if (computo == null) {
            return;
        }

        int row = 0;

        for (ParameterConfig parameter : computo.parameters()) {
            Label label = new Label(
                    parameter.label()
                            + " ("
                            + parameter.unit()
                            + ")"
            );
            label.getStyleClass().add("quote-label");

            TextField field = new TextField();
            field.getStyleClass().add("quote-field");
            field.setPromptText(parameter.label());
            field.setPrefWidth(220);

            parameterFields.put(parameter.key(), field);

            parameterForm.add(label, 0, row);
            parameterForm.add(field, 1, row);

            GridPane.setHgrow(field, Priority.ALWAYS);

            row++;
        }
    }

    private void calculate() {
        try {
            ComputoConfig computo = computoBox.getValue();

            if (computo == null) {
                throw new IllegalArgumentException(
                        "Seleccioná un computo"
                );
            }

            Map<String, BigDecimal> parameters =
                    new LinkedHashMap<>();

            for (ParameterConfig parameter : computo.parameters()) {
                TextField field = parameterFields.get(parameter.key());

                if (field == null) {
                    throw new IllegalArgumentException(
                            "No existe el campo: "
                                    + parameter.key()
                    );
                }

                parameters.put(
                        parameter.key(),
                        decimal(field)
                );
            }

            ComputedQuote quote = catalogService.calculate(
                    computo,
                    parameters
            );

            renderQuote(quote);

        } catch (RuntimeException ex) {
            Alert alert = new Alert(
                    Alert.AlertType.ERROR,
                    ex.getMessage(),
                    ButtonType.OK
            );

            DialogTheme.apply(alert);
            alert.showAndWait();
        }
    }

    private void renderQuote(ComputedQuote quote) {
        resultBox.getChildren().clear();

        resultBox.getChildren().add(
                createResultHeader()
        );

        for (ComputedItem item : quote.items()) {
            resultBox.getChildren().add(
                    createResultRow(item)
            );
        }

        resultBox.getChildren().add(
                createSummary(quote)
        );
    }

    private GridPane createResultHeader() {
        GridPane header = createResultGrid();
        header.getStyleClass().add("quote-result-header");

        header.add(label("PRODUCTO"), 0, 0);
        header.add(label("CANTIDAD"), 1, 0);
        header.add(label("UNIDAD"), 2, 0);
        header.add(label("SUBTOTAL"), 3, 0);

        return header;
    }

    private GridPane createResultRow(ComputedItem item) {
        GridPane row = createResultGrid();
        row.getStyleClass().add("quote-result-row");

        Label product = label(item.productName());
        product.getStyleClass().add("quote-result-product");

        Label quantity = label(
                formatQuantity(
                        item.quantity(),
                        item.quantityDecimals()
                )
        );
        quantity.getStyleClass().add("quote-result-number");

        Label unit = label(item.unitType().name());
        unit.getStyleClass().add("quote-result-unit");

        Label subtotal = label(
                "$" + format(item.subTotal())
        );
        subtotal.getStyleClass().add("quote-result-number");

        row.add(product, 0, 0);
        row.add(quantity, 1, 0);
        row.add(unit, 2, 0);
        row.add(subtotal, 3, 0);

        return row;
    }

    private VBox createSummary(ComputedQuote quote) {
        Label subtotal = new Label(
                "Subtotal: $" + format(quote.subtotal())
        );
        subtotal.getStyleClass().add("quote-summary-value");

        Label iva = new Label(
                "IVA 21%: $" + format(quote.iva())
        );
        iva.getStyleClass().add("quote-summary-value");

        Label total = new Label(
                "TOTAL: $" + format(quote.total())
        );
        total.getStyleClass().add("quote-total");

        VBox summary = new VBox(
                6,
                subtotal,
                iva,
                new Separator(),
                total
        );

        summary.getStyleClass().add("quote-summary");

        return summary;
    }

    private GridPane createResultGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setPadding(new Insets(10, 0, 10, 0));

        ColumnConstraints productColumn =
                new ColumnConstraints();
        productColumn.setHgrow(Priority.ALWAYS);

        ColumnConstraints quantityColumn =
                new ColumnConstraints(95);

        ColumnConstraints unitColumn =
                new ColumnConstraints(90);

        ColumnConstraints subtotalColumn =
                new ColumnConstraints(125);

        grid.getColumnConstraints().addAll(
                productColumn,
                quantityColumn,
                unitColumn,
                subtotalColumn
        );

        return grid;
    }

    private Label label(String text) {
        return new Label(text);
    }

    private void showEmptyResult() {
        Label empty = new Label(
                "Seleccioná un computo y completá sus medidas"
        );
        empty.getStyleClass().add("quote-empty");
        resultBox.getChildren().add(empty);
    }

    private BigDecimal decimal(TextField field) {
        if (field.getText() == null
                || field.getText().isBlank()) {
            throw new IllegalArgumentException(
                    "Completá todos los valores"
            );
        }

        BigDecimal value = new BigDecimal(
                field.getText()
                        .trim()
                        .replace(",", ".")
        );

        if (value.signum() < 0) {
            throw new IllegalArgumentException(
                    "Los valores no pueden ser negativos"
            );
        }

        return value;
    }

    private String format(BigDecimal value) {
        return value
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }

    private String formatQuantity(
            BigDecimal value,
            int decimals
    ) {
        return value
                .setScale(decimals, RoundingMode.HALF_UP)
                .toString();
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {
    }



}
