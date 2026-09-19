package org.ignia.comprobante.ui.view.clients;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.cliente.ClientDTO;
import org.ignia.comprobante.cliente.IClientService;
import org.ignia.comprobante.exception.ConflictException;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.model.*;
import org.ignia.comprobante.ui.shell.SectionView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientsController implements SectionView {


        private static final int PAGE_SIZE = 12;

        @FXML private StackPane toolbarSlot;
        @FXML private StackPane contentSlot;

        private final IClientService clientService;
        private final NavigationService navigationService;

        private DataCard<ClientDTO> dataCard;
        private String cityFilter;
        private int currentPage;
        private String searchText = "";

        public ClientsController(IClientService clientService, NavigationService navigationService) {
            this.clientService = clientService;
            this.navigationService = navigationService;
        }

    @FXML
    private void initialize() {
            navigationService.registerView(this);
            ActionBar bar = new ActionBar(new ActionBarConfig(null, "Buscar cliente por nombre...",
                List.of(
                        new ButtonDef("Exportar", ButtonDef.Style.GHOST, null),
                        new ButtonDef("+ Nuevo cliente", ButtonDef.Style.PRIMARY, this::openCreate))));
            toolbarSlot.getChildren().setAll(bar);

            dataCard = new DataCard<>("CLIENTES", "Listado de clientes", columns(), rowActions());
            dataCard.setOnPageChange(this::goToPage);
            contentSlot.getChildren().setAll(dataCard);
            bar.setOnSearch(this::onSearch);

            reload();

    }

    private List<ColumnDef<ClientDTO>> columns() {

            return List.of(
                    ColumnDef.of("DNI", c -> c.getDni() == null ? "" : c.getDni(), 150),
                    ColumnDef.of("Nombre", c -> c.getName() == null ? "" : c.getName(), 200),
                    ColumnDef.of("Apellido", c -> c.getLastName() == null ? "" : c.getLastName(), 200),
                    ColumnDef.of("Teléfono", c -> c.getTelephoneNumber() == null ? "" : c.getTelephoneNumber(), 150),
                    ColumnDef.of("Teléfono opcional", c -> c.getSecondTelephoneNumber() == null ? "" : c.getSecondTelephoneNumber(), 150),
                    ColumnDef.of("Email", c -> c.getEmailAddress() == null ? "" : c.getEmailAddress(), 200),
                    ColumnDef.of("Provincia", c -> c.getProvince() == null ? "" : c.getProvince(), 150),
                    ColumnDef.of("Ciudad", c -> c.getCity() == null ? "" : c.getCity(), 150),
                    ColumnDef.of("Código postal", c -> c.getPostalCode() == null ? "" : c.getPostalCode(), 150),
                    ColumnDef.of("Dirección", c -> c.getAddress() == null ? "" : c.getAddress(), 200),
                    ColumnDef.of("CUIT", c -> c.getCuit() == null ? "" : c.getCuit(), 150)

            );
    }

    private List<RowActionDef<ClientDTO>> rowActions() {
            return List.of(
                new RowActionDef<>("Editar", this::openEdit),
                new RowActionDef<>("Eliminar", this::delete),
                new RowActionDef<>("Ver ventas", null)
            );
    }

    private void goToPage(int page) {
            currentPage = page;
            reload();
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {
            String id = item.id();
            if ("all".equals(id)) {
                cityFilter = null;
            } else if (id.startsWith("city-")) {
                cityFilter = id.substring("city-".length());
            }
            currentPage = 0;
            reload();
    }

    private void openCreate() {
            ClientDTO existing = null;
            while(true) {
                Optional<ClientDTO> result = new ClientFormDialog(existing).showAndWait();
                if (result.isEmpty()) return;
                try {
                    clientService.saveClient(result.get());
                    currentPage = 0;
                    reload();
                    navigationService.refreshSidebar();
                    return;
                } catch (ConflictException ex) {
                    showDuplicate(ex.getMessage());
                    existing = result.get();
                } catch (IllegalStateException ex) {
                    showError(ex.getMessage());
                    existing = result.get();
                }
            }
    }

    private void openEdit(ClientDTO dto) {
            ClientDTO existing = dto;
            while(true) {
                Optional<ClientDTO> result = new ClientFormDialog(existing).showAndWait();
                if (result.isEmpty()) return;
                try {
                    clientService.updateClient(dto.getId(), result.get());
                    reload();
                    navigationService.refreshSidebar();
                    return;
                } catch (ConflictException ex) {
                    showDuplicate(ex.getMessage());
                    existing = result.get();
                } catch (IllegalStateException ex) {
                    showError(ex.getMessage());
                    existing = result.get();
                }
            }
    }

    private void delete(ClientDTO dto) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            styleDialog(confirm);
            confirm.setTitle("Eliminar cliente");
            confirm.setHeaderText("¿Eliminar «" + dto.getName() + " " + dto.getSecondName() + " " + dto.getLastName() + "»?");
            confirm.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    clientService.deleteClient(dto.getId());
                    currentPage = 0;
                    reload();
                    navigationService.refreshSidebar();
                } catch (ConflictException ex) {
                    showError(ex.getMessage());
                }
            }

    }

    private void showError(String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
            styleDialog(alert);
            alert.setTitle("No se pudo eliminar");
            alert.setHeaderText(null);
            alert.showAndWait();
    }

    private void styleDialog(Dialog<?> dialog) {
            dialog.getDialogPane().getStyleClass().add("app-dialog");
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
    }

    private void onSearch(String q) {
            searchText = q == null ? "" : q.trim();
            currentPage = 0;
            reload();
    }

    private void reload() {
        Page<ClientDTO> page = clientService.page(searchText, cityFilter, PageRequest.of(currentPage, PAGE_SIZE));
        dataCard.setData(PageData.of(page.getContent(), page.getNumber(),
                page.getTotalPages(), page.getSize(), page.getTotalElements()));
    }

    private void showDuplicate(String message) {
            Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
            styleDialog(alert);
            alert.setTitle("Cliente duplicado");
            alert.setHeaderText("Ya existe un cliente con ese DNI");
            alert.showAndWait();
    }
}

