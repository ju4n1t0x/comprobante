package org.ignia.comprobante.ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.ui.model.SectionId;
import org.ignia.comprobante.ui.model.TabDef;
import org.ignia.comprobante.ui.model.UserChip;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TopNavBar extends BorderPane {

    private final Consumer<SectionId> onSelect;
    private final Map<SectionId, Label> tabNodes = new LinkedHashMap<>();

    public TopNavBar(List<TabDef> defs, UserChip user, Consumer<SectionId> onSelect) {
        this.onSelect = onSelect;
        getStyleClass().add("top-navbar");

        HBox brand = new HBox();
        brand.getStyleClass().add("brand");
        Label logo = new Label("F");
        logo.getStyleClass().add("brand-logo");
        VBox brandText = new VBox(new Label("FERRAL"), new Label("MATERIALES · V2.4"));
        brandText.getStyleClass().add("brand-text");
        brand.getChildren().addAll(logo, brandText);
        setLeft(brand);

        HBox tabs = new HBox();
        tabs.getStyleClass().add("nav-tabs");
        for (TabDef def : defs) {
            Label tab = new Label(def.label());
            tab.getStyleClass().add("nav-tab");
            tab.setOnMouseClicked(e -> select(def.sectionId()));
            tabNodes.put(def.sectionId(), tab);
            tabs.getChildren().add(tab);
        }
        setCenter(tabs);

        HBox chip = new HBox();
        chip.getStyleClass().add("user-chip");
        VBox name = new VBox(new Label(user.name()), new Label(user.role()));
        Label avatar = new Label(user.initials());
        avatar.getStyleClass().add("user-avatar");
        chip.getChildren().addAll(name, avatar);
        setRight(chip);
    }

    public void select(SectionId id) {
        setActive(id);
        onSelect.accept(id);
    }

    public void setActive(SectionId id) {
        tabNodes.values().forEach(t -> t.getStyleClass().remove("nav-tab-active"));
        Label tab = tabNodes.get(id);
        if (tab != null) {
            tab.getStyleClass().add("nav-tab-active");
        }
    }
}
