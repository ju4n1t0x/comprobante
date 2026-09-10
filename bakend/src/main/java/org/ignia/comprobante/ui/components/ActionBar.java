package org.ignia.comprobante.ui.components;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.ignia.comprobante.ui.model.ActionBarConfig;
import org.ignia.comprobante.ui.model.ButtonDef;

public class ActionBar extends HBox {

    public ActionBar(ActionBarConfig config) {
        getStyleClass().add("action-bar");

        Node left;
        if (config.searchPlaceholder() != null) {
            left = buildSearch(config.searchPlaceholder());
        } else if (config.breadcrumb() != null) {
            Label crumb = new Label(config.breadcrumb());
            crumb.getStyleClass().add("breadcrumb");
            left = crumb;
        } else {
            left = new Region();
        }
        HBox.setHgrow(left, Priority.ALWAYS);
        getChildren().add(left);

        for (ButtonDef def : config.buttons()) {
            Button button = new Button(def.label());
            button.getStyleClass().add(def.style() == ButtonDef.Style.PRIMARY ? "btn-primary" : "btn-ghost");
            if (def.onAction() != null) {
                button.setOnAction(e -> def.onAction().run());
            }
            getChildren().add(button);
        }
    }

    private Node buildSearch(String placeholder) {
        TextField field = new TextField();
        field.setPromptText(placeholder);
        field.getStyleClass().add("search-field");
        return field;
    }
}
