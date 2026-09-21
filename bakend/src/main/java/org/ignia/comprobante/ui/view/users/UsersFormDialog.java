package org.ignia.comprobante.ui.view.users;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.ignia.comprobante.user.Role;
import org.ignia.comprobante.user.UserDTO;

public class UsersFormDialog extends Dialog<UserDTO> {

    public UsersFormDialog(UserDTO existing) {
        setTitle(existing == null ? "Nuevo usuario" : "Editar usuario");
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField userNameField = new TextField();
        userNameField.setPromptText("Nombre de usuario");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Contraseña");

        TextField emailField = new TextField();
        emailField.setPromptText("Correo electrónico");

        ComboBox<Role> roleCombo = new ComboBox<>(FXCollections.observableArrayList(Role.values()));
        roleCombo.setPromptText("Rol");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Teléfono");

        TextField phoneTwoField = new TextField();
        phoneTwoField.setPromptText("Teléfono secundario");

        TextField provinceField = new TextField();
        provinceField.setPromptText("Provincia");

        TextField cityField = new TextField();
        cityField.setPromptText("Ciudad");

        TextField postalCodeField = new TextField();
        postalCodeField.setPromptText("Código postal");

        TextField addressField = new TextField();
        addressField.setPromptText("Dirección");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Nombre"), 0,0);
        grid.add(userNameField, 1,0);
        grid.add(new Label("Contraseña"), 0,1);
        grid.add(passwordField, 1,1);
        grid.add(new Label("Correo electrónico"), 0,2);
        grid.add(emailField, 1,2);
        grid.add(new Label("Rol"), 0,3);
        grid.add(roleCombo, 1,3);
        grid.add(new Label("Teléfono"), 0,4);
        grid.add(phoneField, 1,4);
        grid.add(new Label("Teléfono secundario"), 0,5);
        grid.add(phoneTwoField, 1,5);
        grid.add(new Label("Provincia"), 0,6);
        grid.add(provinceField, 1,6);
        grid.add(new Label("Ciudad"), 0,7);
        grid.add(cityField, 1,7);
        grid.add(new Label("Código postal"), 0,8);
        grid.add(postalCodeField, 1,8);
        grid.add(new Label("Dirección"), 0,9);
        grid.add(addressField, 1,9);

        GridPane.setHgrow(userNameField, Priority.ALWAYS);
        GridPane.setHgrow(emailField, Priority.ALWAYS);

        pane.setContent(grid);

        //pre-fill si es edicion
        if (existing != null){
            userNameField.setText(existing.getUserName());
            passwordField.setText(existing.getPassword());
            emailField.setText(existing.getEmail());
            roleCombo.setValue(existing.getRole());
            phoneField.setText(existing.getTelephoneNumber());
            phoneTwoField.setText(existing.getSecondTelephoneNumber());
            provinceField.setText(existing.getProvince());
            cityField.setText(existing.getCity());
            postalCodeField.setText(existing.getPostalCode());
            addressField.setText(existing.getAddress());
        }

        //validacion: OK habilitado solo si los campos obligatorios estan completos
        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                userNameField.textProperty().isEmpty()
                        .or(emailField.textProperty().isEmpty())
                        .or(roleCombo.valueProperty().isNull())
                        .or(passwordField.textProperty().isEmpty().and(new SimpleBooleanProperty(existing == null)
                                .or(new SimpleBooleanProperty(true))))
        );

        //para edicion: password no es obligatorio
        if (existing != null){
            okButton.disableProperty().bind(
                    userNameField.textProperty().isEmpty()
                            .or(emailField.textProperty().isEmpty())
                            .or(roleCombo.valueProperty().isNull())
            );
        }

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK){
                String password = passwordField.getText();
                if (existing != null && password.isBlank()){
                    password = existing.getPassword() != null ? existing.getPassword() : "";
                }

                return UserDTO.builder()
                        .id(existing == null ? null : existing.getId())
                        .userName(userNameField.getText().trim())
                        .password(password)
                        .email(emailField.getText().trim())
                        .role(roleCombo.getValue())
                        .telephoneNumber(phoneField.getText().trim())
                        .secondTelephoneNumber(phoneTwoField.getText().trim())
                        .province(provinceField.getText().trim())
                        .city(cityField.getText().trim())
                        .postalCode(postalCodeField.getText().trim())
                        .address(addressField.getText().trim())
                        .build();
            }
            return null;
        });
    }
}
