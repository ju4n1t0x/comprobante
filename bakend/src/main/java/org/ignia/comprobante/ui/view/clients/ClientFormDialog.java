package org.ignia.comprobante.ui.view.clients;

import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.ignia.comprobante.cliente.ClientDTO;

import java.util.List;
import java.util.function.Predicate;


public class ClientFormDialog extends Dialog<ClientDTO> {

    private final TextField dniField = new TextField();
    private final TextField nameField = new TextField();
    private final TextField secondNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField telephoneNumberField = new TextField();
    private final TextField secondTelephoneNumberField = new TextField();
    private final TextField emailAddressField = new TextField();
    private final TextField provinceField = new TextField();
    private final TextField postalCodeField = new TextField();
    private final TextField cityField = new TextField();
    private final TextField addressField = new TextField();
    private final TextField cuitField = new TextField();

    private final Label dniError = new Label();
    private final Label nameError = new Label();
    private final Label secondNameError = new Label();
    private final Label lastNameError = new Label();
    private final Label telephoneNumberError = new Label();
    private final Label secondTelephoneNumberError = new Label();
    private final Label emailAddressError = new Label();
    private final Label provinceError = new Label();
    private final Label postalCodeError = new Label();
    private final Label cityError = new Label();
    private final Label addressError = new Label();
    private final Label cuitError = new Label();

    private static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

    private static boolean ok(String v, String regex) {
        return v != null && v.trim().matches(regex);
    }

    private void wire(TextField field, Label error, Predicate<String> rule, String msg) {
        field.textProperty().addListener((o, a, b) -> {
            error.setText(rule.test(b) ? "" : msg);
            field.pseudoClassStateChanged(ERROR, !rule.test(b));
        });
    }


    public ClientFormDialog(ClientDTO existing){
            setTitle(existing == null ? "Nuevo cliente" : "Editar cliente");
            setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(140);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setPrefWidth(250);
        ColumnConstraints errorCol = new ColumnConstraints();
        errorCol.setPrefWidth(260);
        grid.getColumnConstraints().addAll(labelCol, fieldCol, errorCol);
        grid.addRow(0, new Label("DNI"), dniField, dniError);
        grid.addRow(1, new Label("Nombre"), nameField, nameError);
        grid.addRow(2, new Label("Segundo nombre"), secondNameField, secondNameError);
        grid.addRow(3, new Label("Apellido"), lastNameField, lastNameError);
        grid.addRow(4, new Label("Teléfono"), telephoneNumberField, telephoneNumberError);
        grid.addRow(5, new Label("Segundo teléfono"), secondTelephoneNumberField, secondTelephoneNumberError);
        grid.addRow(6, new Label("Correo electrónico"), emailAddressField, emailAddressError);
        grid.addRow(7, new Label("Provincia"), provinceField, provinceError);
        grid.addRow(8, new Label("Código postal"), postalCodeField, postalCodeError);
        grid.addRow(9, new Label("Ciudad"), cityField, cityError);
        grid.addRow(10, new Label("Dirección"), addressField, addressError);
        grid.addRow(11, new Label("CUIT"), cuitField, cuitError);
        pane.setContent(grid);
        pane.setPrefWidth(740);;
        pane.setMinWidth(700);

        if (existing != null) {
            dniField.setText(existing.getDni() == null ? "" : existing.getDni());
            nameField.setText(existing.getName() == null ? "" : existing.getName());
            secondNameField.setText(existing.getSecondName() == null ? "" : existing.getSecondName());
            lastNameField.setText(existing.getLastName() == null ? "" : existing.getLastName());
            telephoneNumberField.setText(existing.getTelephoneNumber() == null ? "" : existing.getTelephoneNumber());
            secondTelephoneNumberField.setText(existing.getSecondTelephoneNumber() == null ? "" : existing.getSecondTelephoneNumber());
            emailAddressField.setText(existing.getEmailAddress() == null ? "" : existing.getEmailAddress());
            provinceField.setText(existing.getProvince() == null ? "" : existing.getProvince());
            postalCodeField.setText(existing.getPostalCode() == null ? "" : existing.getPostalCode());
            cityField.setText(existing.getCity() == null ? "" : existing.getCity());
            addressField.textProperty().addListener((o, a, b) -> {
                boolean ok = ok(b, "[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,'\\-°º]{3,100}");
                addressError.setText(ok ? "" : "Letras, números y espacios (3-100)");
            });
            cuitField.setText(existing.getCuit() == null ? "" : existing.getCuit());
        }

        for (Label e: List.of(dniError,
                                nameError,
                                secondNameError,
                                telephoneNumberError,
                                secondTelephoneNumberError,
                                emailAddressError,
                                provinceError,
                                postalCodeError,
                                cityError,
                                addressError,
                                cuitError)) {
                            e.setWrapText(true);
                            e.setMinWidth(200);
                            e.getStyleClass().add("field-error");
        }

        //validacion viva
        Predicate<String> dniOk = v -> ok(v, "\\d{7,8}");
        wire(dniField, dniError, dniOk, "7 u 8 dígitos");

        Predicate<String> nameOk = v -> ok(v, "[A-Za-záéíóúÁÉÍÓÚñÑ]{2,50}+");
        wire(nameField, nameError, nameOk, "Solo letras (2-50)");

        Predicate<String> secondNameOk = v -> ok(v, "[A-Za-záéíóúÁÉÍÓÚñÑ]{2,50}+");
        wire(secondNameField, secondNameError, secondNameOk, "Solo letras (2-50)");

        Predicate<String> lastNameOk = v -> ok(v, "[A-Za-záéíóúÁÉÍÓÚñÑ]{2,50}+");
        wire(lastNameField, lastNameError, lastNameOk, "Solo letras (2-50)");

        Predicate<String> telephoneOk = v -> ok(v, "\\d{10}");
        wire(telephoneNumberField, telephoneNumberError, telephoneOk, " 10 dígitos");

        Predicate<String> secondTelephoneOk = v -> ok(v, "\\d{6,15}");
        wire(secondTelephoneNumberField, secondTelephoneNumberError, secondTelephoneOk, "6-15 dígitos");

        Predicate<String> emailOk = v -> ok(v, "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        wire(emailAddressField, emailAddressError, emailOk, "Email debe ser válido");

        Predicate<String> provinceOk = v -> ok(v, ".+");
        wire(provinceField, provinceError, provinceOk, "Oblitgatoria");

        Predicate<String> postalCodeOk = v -> ok(v, "\\d{4}");
        wire(postalCodeField, postalCodeError, postalCodeOk, "4 dígitos");

        Predicate<String> cityOk = v -> ok(v, ".+");
        wire(cityField, cityError, cityOk, "Ciudad es obligatoria");

        Predicate<String> addressOk = v -> ok(v, "[A-Za-zÁÉÍÓÚáéíóúÑñ0-9 .,'\\-°º]{3,100}");
        wire(addressField, addressError, addressOk, "Dirección inválida");

        Predicate<String> cuitOk = v -> ok(v, "\\d{2}-\\d{8}-\\d");
        wire(cuitField, cuitError, cuitOk, "xx-xxxxxxxx-x");

        Node okButton = pane.lookupButton(ButtonType.OK);
            okButton.disableProperty().bind(Bindings.createBooleanBinding(
                    () -> !dniOk.test(dniField.getText()) || !nameOk.test(nameField.getText())
                            || !secondNameOk.test(secondNameField.getText()) || !lastNameOk.test(lastNameField.getText())
                            || !telephoneOk.test(telephoneNumberField.getText()) || !secondTelephoneOk.test(secondTelephoneNumberField.getText())
                            || !emailOk.test(emailAddressField.getText()) || !provinceOk.test(provinceField.getText())
                            || !postalCodeOk.test(postalCodeField.getText()) || !cityOk.test(cityField.getText())
                            || !addressOk.test(addressField.getText()) || !cuitOk.test(cuitField.getText()),
                    dniField.textProperty(), nameField.textProperty(), secondNameField.textProperty(), lastNameField.textProperty(), telephoneNumberField.textProperty(), secondTelephoneNumberField.textProperty(),
                    emailAddressField.textProperty(), provinceField.textProperty(), cityField.textProperty(), addressField.textProperty(),
                    postalCodeField.textProperty(), cuitField.textProperty()));

            setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    return ClientDTO.builder()
                            .id(existing == null ? null : existing.getId())
                            .dni(dniField.getText().trim())
                            .name(nameField.getText().trim())
                            .secondName(secondNameField.getText().trim())
                            .lastName(lastNameField.getText().trim())
                            .telephoneNumber(telephoneNumberField.getText().trim())
                            .secondTelephoneNumber(secondTelephoneNumberField.getText().trim())
                            .emailAddress(emailAddressField.getText().trim())
                            .province(provinceField.getText().trim())
                            .postalCode(postalCodeField.getText().trim())
                            .city(cityField.getText().trim())
                            .address(addressField.getText().trim())
                            .cuit(cuitField.getText().trim())
                            .build();
                }
                return null;
            });
        }
    }

