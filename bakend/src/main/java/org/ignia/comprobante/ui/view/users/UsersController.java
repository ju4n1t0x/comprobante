package org.ignia.comprobante.ui.view.users;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import org.ignia.comprobante.security.SessionService;
import org.ignia.comprobante.ui.NavigationService;
import org.ignia.comprobante.ui.components.ActionBar;
import org.ignia.comprobante.ui.components.DataCard;
import org.ignia.comprobante.ui.model.*;
import org.ignia.comprobante.ui.shell.SectionView;
import org.ignia.comprobante.user.IUserService;
import org.ignia.comprobante.user.Role;
import org.ignia.comprobante.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UsersController implements SectionView {

    private static final int PAGE_SIZE = 12;

    @FXML private StackPane toolbarSlot;
    @FXML private StackPane contentSlot;

    @Autowired
    private IUserService userService;

    @Autowired
    private NavigationService navigationService;

    @Autowired
    private SessionService sessionService;

    private DataCard<UserDTO> dataCard;
    private int currentPage;
    private String searchText = "";

    @FXML
    private void initialize(){
        navigationService.registerView(this);

        List<ButtonDef> buttons = new ArrayList<>();
        if (sessionService.getCurrentUserRole() == Role.SUPER_ADMIN){
            buttons.add(new ButtonDef("+ Nuevo usuario", ButtonDef.Style.PRIMARY, this::openCreate));
        }

        ActionBar bar = new ActionBar(new ActionBarConfig(null, "Buscar usuario por nombre o email", buttons));
        toolbarSlot.getChildren().add(bar);

        dataCard = new DataCard<>("Usuarios", "Listado de usuarios", columns(), rowActions());
        dataCard.setOnPageChange(this::goToPage);
        contentSlot.getChildren().add(dataCard);
        bar.setOnSearch(this::onSearch);

        reload();
    }

    private List<ColumnDef<UserDTO>> columns(){
        return List.of(
            ColumnDef.of("Nombre", UserDTO::getUserName, 150),
            ColumnDef.of("Email", UserDTO::getEmail, 200),
            ColumnDef.of("Rol", u -> u.getRole() == null ? "" : u.getRole().toString(), 120),
            ColumnDef.of("Teléfono", UserDTO::getTelephoneNumber, 120),
            ColumnDef.of("Provincia", UserDTO::getProvince, 120),
            ColumnDef.of("Ciudad", UserDTO::getCity, 120)
        );
    }

    private List<RowActionDef<UserDTO>> rowActions(){
        List<RowActionDef<UserDTO>> actions = new ArrayList<>();
        actions.add(new RowActionDef<>("Editar", this::openEdit));
        if (sessionService.getCurrentUserRole() == Role.SUPER_ADMIN){
            actions.add(new RowActionDef<>("Eliminar", this::delete));
        }
        return actions;
    }

    private void openCreate(){
        new UsersFormDialog(null).showAndWait().ifPresent(dto -> {
           try{
               userService.save(dto);
               currentPage = 0;
               reload();
               navigationService.refreshSidebar();
           } catch (RuntimeException ex){
               showError(ex.getMessage());
           }
        });
    }

    private void openEdit(UserDTO dto){
     new UsersFormDialog(dto).showAndWait().ifPresent(updatedDto -> {
         try{
             userService.updateUser(updatedDto, dto.getId());
             reload();
             navigationService.refreshSidebar();
         } catch (RuntimeException e) {
             showError(e.getMessage());
         }
     });
    }

    private void delete(UserDTO dto) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        styleDialog(confirm);
        confirm.setTitle("Eliminar usuario");
        confirm.setHeaderText("¿Eliminar «" + dto.getUserName() + "»?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            userService.delete(dto.getId());
            currentPage = 0;
            reload();
            navigationService.refreshSidebar();
        }
    }

    @Override
    public void onSidebarSelection(SidebarItem item) {

        currentPage = 0;
        reload();
    }

    private void goToPage(int page){
        currentPage = page;
        reload();
    }

    private void onSearch(String q){
        searchText = q == null ? "" : q.trim();
        currentPage = 0;
        reload();
    }

    private void reload() {
        Page<UserDTO> page = userService.page(searchText, PageRequest.of(currentPage, PAGE_SIZE));
        if (page != null) {
            dataCard.setData(PageData.of(page.getContent(), page.getNumber(),
                    page.getTotalPages(), page.getSize(), page.getTotalElements()));
        }
    }
    private void styleDialog(Dialog<?> dialog){
        dialog.getDialogPane().getStyleClass().add("app-dialog");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
    }

    private void showError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        styleDialog(alert);
        alert.setTitle("No se pudo completar la acción");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
    }

