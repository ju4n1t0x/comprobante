package org.ignia.comprobante.ui.view.categories;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.ignia.comprobante.categories.CategoryDto;

public class CategoryFormDialog extends Dialog<CategoryDto> {

    private final TextField nameField = new TextField();
    private final TextField descriptionField = new TextField();

    public CategoryFormDialog(CategoryDto existing) {
        setTitle(existing == null ? "Nueva categoría" : "Editar categoría");
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nombre"), nameField);
        grid.addRow(1, new Label("Descripción"), descriptionField);
        descriptionField.setPromptText("Opcional");
        pane.setContent(grid);

        if (existing != null) {
            nameField.setText(existing.getName());
            descriptionField.setText(existing.getDescription() == null ? "" : existing.getDescription());
        }

        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> nameField.getText() == null || nameField.getText().isBlank(),
                nameField.textProperty()));

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return CategoryDto.builder()
                        .id(existing == null ? null : existing.getId())
                        .name(nameField.getText().trim())
                        .description(descriptionField.getText())
                        .build();
            }
            return null;
        });
    }
}
