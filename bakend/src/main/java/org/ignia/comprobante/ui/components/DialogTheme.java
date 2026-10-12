package org.ignia.comprobante.ui.components;

import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

public class DialogTheme {

    private static final String THEME_CSS = "/css/theme.css";

    private DialogTheme(){
    }

    public static void apply(Dialog<?> dialog){
        DialogPane pane = dialog.getDialogPane();

        boolean alreadyApplied = pane.getStylesheets().stream().anyMatch(url -> url.endsWith(THEME_CSS));

        if (!alreadyApplied){
            pane.getStylesheets().add(
                    DialogTheme.class.getResource(THEME_CSS).toExternalForm()
            );
        }
    }
}
