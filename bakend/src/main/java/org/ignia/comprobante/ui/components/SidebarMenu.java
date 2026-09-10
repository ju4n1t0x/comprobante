package org.ignia.comprobante.ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.ui.model.SidebarConfig;
import org.ignia.comprobante.ui.model.SidebarItem;
import org.ignia.comprobante.ui.model.SummaryCard;

import java.util.function.Consumer;

public class SidebarMenu extends VBox {

    private final Label titleLabel = new Label();
    private final VBox itemsBox = new VBox();
    private final VBox summaryCard = new VBox();

    private Consumer<SidebarItem> onSelect;
    private HBox activeRow;

    public SidebarMenu() {
        getStyleClass().add("sidebar");
        titleLabel.getStyleClass().add("sidebar-title");
        itemsBox.getStyleClass().add("sidebar-items");
        summaryCard.getStyleClass().add("summary-card");
        getChildren().addAll(titleLabel, itemsBox, summaryCard);
        VBox.setVgrow(itemsBox, Priority.ALWAYS);
    }

    public void setOnSelect(Consumer<SidebarItem> onSelect) {
        this.onSelect = onSelect;
    }

    public void configure(SidebarConfig config) {
        titleLabel.setText(config.title());
        itemsBox.getChildren().clear();
        activeRow = null;

        for (SidebarItem item : config.items()) {
            itemsBox.getChildren().add(buildRow(item));
        }
        if (!itemsBox.getChildren().isEmpty()) {
            setActive((HBox) itemsBox.getChildren().get(0));
        }

        SummaryCard summary = config.summary();
        summaryCard.getChildren().clear();
        Label value = new Label(summary.value());
        value.getStyleClass().add("summary-value");
        Label label = new Label(summary.label());
        label.getStyleClass().add("summary-label");
        Label suffix = new Label(summary.suffix());
        suffix.getStyleClass().add("summary-suffix");
        summaryCard.getChildren().addAll(label, value, suffix);
    }

    private HBox buildRow(SidebarItem item) {
        HBox row = new HBox();
        row.getStyleClass().add("side-item");

        Label label = new Label(item.label());
        label.getStyleClass().add("side-item-label");

        Label count = new Label(String.valueOf(item.count()));
        count.getStyleClass().add("side-count");
        if (item.tone() == SidebarItem.Tone.WARN) {
            count.getStyleClass().add("side-count-warn");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        row.getChildren().addAll(label, spacer, count);

        row.setOnMouseClicked(e -> {
            setActive(row);
            if (onSelect != null) {
                onSelect.accept(item);
            }
        });
        return row;
    }

    private void setActive(HBox row) {
        if (activeRow != null) {
            activeRow.getStyleClass().remove("side-item-active");
        }
        row.getStyleClass().add("side-item-active");
        activeRow = row;
    }
}
