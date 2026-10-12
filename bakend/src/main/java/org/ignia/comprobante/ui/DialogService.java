package org.ignia.comprobante.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.ignia.comprobante.ui.components.DialogTheme;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/*
*Servicio de dialogos de la UI.
* Cada dialogo es una ventana aparte con su propio DialogPane, no hereda los stylesheets de la Scene principal.
* */
@Component
public class DialogService {


    // Para dialogos a mano (form, pickers): devolvemos ya estilados
    public <T> Dialog<T> styled(Dialog<T> dialog){
        DialogTheme.apply(dialog);
        return dialog;
    }

    public void error(String messaje){
        error(null, messaje);
    }

    public void error(String headerText, String message){
        Alert alert = new Alert(
                Alert.AlertType.ERROR,
                message == null || message.isBlank() ? "Error desconocido" : message,
                ButtonType.OK
        );
        alert.setTitle("No se pudo completar");
        alert.setHeaderText(headerText);
        DialogTheme.apply(alert);
        alert.showAndWait();
    }

    public boolean confirm(String title, String headerText, String contentText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        DialogTheme.apply(alert);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

}
