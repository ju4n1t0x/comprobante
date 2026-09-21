package org.ignia.comprobante.ui.view.security;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.user.IUserService;
import org.ignia.comprobante.user.UserModel;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginDialog extends Dialog<UserModel> {

    private final IUserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginDialog(IUserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;

        setTitle("Iniciar sesión");
        setHeaderText(null);

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("login-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().add(ButtonType.OK);
        pane.getButtonTypes().remove(ButtonType.CANCEL);

        TextField userField = new TextField();
        userField.setPromptText("Email usuario");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Contraseña");

        VBox content = new VBox(10, new Label("User Email"), userField, new Label("Contraseña"), passField);
        content.setPadding(new Insets(20));
        pane.setContent(content);

        //desabilitar OK si campos vacios
        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(
                userField.textProperty().isEmpty().or(passField.textProperty().isEmpty()));

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK){
                try{
                    return userService.authenticate(
                            userField.getText().trim(),
                            passField.getText()
                    );
                } catch (RuntimeException ex){
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
                    alert.showAndWait();
                }
            }
            return null;
        });
    }
}
