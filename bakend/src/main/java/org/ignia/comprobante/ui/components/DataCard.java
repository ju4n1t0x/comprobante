package org.ignia.comprobante.ui.components;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.ui.model.ColumnDef;
import org.ignia.comprobante.ui.model.PageData;
import org.ignia.comprobante.ui.model.RowActionDef;

import java.util.List;
import java.util.function.IntConsumer;

public class DataCard<T> extends VBox {

    private final Label title = new Label();
    private final Label subtitle = new Label();
    private final Label meta = new Label();
    private final TableView<T> table = new TableView<>();
    private final PaginationBar pagination = new PaginationBar();
    private final List<ColumnDef<T>> columns;
    private final List<RowActionDef<T>> rowActions;

    public DataCard(String titleText, String subtitleText,
                    List<ColumnDef<T>> columns, List<RowActionDef<T>> rowActions) {
        this.columns = columns;
        this.rowActions = rowActions;
        getStyleClass().add("card");

        HBox header = new HBox();
        header.getStyleClass().add("card-header");
        title.getStyleClass().add("card-title");
        subtitle.getStyleClass().add("card-subtitle");
        meta.getStyleClass().add("card-meta");
        VBox titleBox = new VBox(title, subtitle);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleBox, spacer, meta);
        getChildren().add(header);

        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        buildColumns();
        VBox.setVgrow(table, Priority.ALWAYS);
        getChildren().add(table);

        getChildren().add(pagination);

        title.setText(titleText);
        subtitle.setText(subtitleText);
    }

    private void buildColumns() {
        table.getColumns().clear();
        for (ColumnDef<T> def : columns) {
            TableColumn<T, String> col = new TableColumn<>(def.header());
            col.getStyleClass().add("table-col");
            if (def.align() == ColumnDef.Align.RIGHT) {
                col.getStyleClass().add("col-right");
            }
            col.setCellValueFactory(cd -> new ReadOnlyStringWrapper(def.text().apply(cd.getValue())));
            col.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(String value, boolean empty) {
                    super.updateItem(value, empty);
                    getStyleClass().removeAll("cell", "cell-mono", "cell-right", "cell-danger");
                    if (empty || value == null) {
                        setText(null);
                    } else {
                        setText(value);
                        getStyleClass().add("cell");
                        getStyleClass().add("cell-mono");
                        if (def.align() == ColumnDef.Align.RIGHT) {
                            getStyleClass().add("cell-right");
                        }
                        T row = getTableRow() == null ? null : getTableRow().getItem();
                        if (row != null) {
                            String extra = def.styleClass().apply(row);
                            if (extra != null && !extra.isBlank()) {
                                getStyleClass().add(extra);
                            }
                        }
                    }
                }
            });
            table.getColumns().add(col);
        }

        if (!rowActions.isEmpty()) {
            TableColumn<T, Void> actions = new TableColumn<>("ACCIONES");
            actions.getStyleClass().addAll("table-col", "col-actions");
            actions.setCellFactory(tc -> new TableCell<>() {
                @Override
                protected void updateItem(Void value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                        setGraphic(null);
                    } else {
                        T row = getTableView().getItems().get(getIndex());
                        HBox box = new HBox();
                        box.getStyleClass().add("row-actions");
                        box.setMaxWidth(Double.MAX_VALUE);
                        for (RowActionDef<T> action : rowActions) {
                            Label link = new Label(action.label());
                            link.getStyleClass().add("row-action");
                            if (action.onAction() != null) {
                                link.setOnMouseClicked(e -> action.onAction().accept(row));
                            }
                            box.getChildren().add(link);
                        }
                        setGraphic(box);
                    }
                }
            });
            table.getColumns().add(actions);
        }
    }

    public void setData(PageData<T> page) {
        table.getItems().setAll(page.items());

        int from = page.totalElements() == 0 ? 0 : page.page() * page.pageSize() + 1;
        long to = Math.min((long) (page.page() + 1) * page.pageSize(), page.totalElements());
        meta.setText(String.format("PÁG. %02d / %d    %d-%d de %d",
                page.page() + 1, Math.max(1, page.totalPages()), from, to, page.totalElements()));

        pagination.update(page.page(), page.totalPages(), page.pageSize());
    }

    public void setOnPageChange(IntConsumer onPageChange) {
        pagination.setOnPageChange(onPageChange);
    }
}
