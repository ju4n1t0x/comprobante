package org.ignia.comprobante.ui.components;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.function.IntConsumer;

public class PaginationBar extends HBox {

    private static final int WINDOW = 5;

    private final Label pageSizeLabel = new Label();
    private final HBox pageButtons = new HBox();

    private IntConsumer onPageChange;
    private int currentPage;
    private int totalPages;

    public PaginationBar() {
        getStyleClass().add("pagination");
        pageSizeLabel.getStyleClass().add("page-size");
        pageButtons.getStyleClass().add("page-buttons");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(pageSizeLabel, spacer, pageButtons);
    }

    public void setOnPageChange(IntConsumer onPageChange) {
        this.onPageChange = onPageChange;
    }

    public void update(int page, int totalPages, int pageSize) {
        this.totalPages = Math.max(1, totalPages);
        this.currentPage = Math.max(0, Math.min(page, this.totalPages - 1));
        pageSizeLabel.setText(pageSize + " por página");
        render();
    }

    private void render() {
        pageButtons.getChildren().clear();
        pageButtons.getChildren().add(buildButton("‹", currentPage - 1, false, currentPage == 0));

        int start = Math.max(0, currentPage - WINDOW / 2);
        int end = Math.min(totalPages, start + WINDOW);
        start = Math.max(0, end - WINDOW);
        for (int i = start; i < end; i++) {
            pageButtons.getChildren().add(buildButton(String.valueOf(i + 1), i, i == currentPage, false));
        }

        pageButtons.getChildren().add(buildButton("›", currentPage + 1, false, currentPage >= totalPages - 1));
    }

    private Button buildButton(String text, int targetPage, boolean active, boolean disabled) {
        Button button = new Button(text);
        button.getStyleClass().add("page-btn");
        if (active) {
            button.getStyleClass().add("page-btn-active");
        }
        if (disabled) {
            button.setDisable(true);
        } else {
            button.setOnAction(e -> {
                if (targetPage != currentPage && onPageChange != null) {
                    onPageChange.accept(targetPage);
                }
            });
        }
        return button;
    }
}
