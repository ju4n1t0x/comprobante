package org.ignia.comprobante.ui.view.sales;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.ignia.comprobante.cliente.ClientDTO;
import org.ignia.comprobante.cliente.IClientService;

import java.util.List;


public class ClientPickerDialog  extends Dialog<ClientDTO> {

    private final TextField dniSearchField = new TextField();
    private final ListView<ClientDTO> resultsList = new ListView<>();

    private final IClientService clientSerivice;



    public ClientPickerDialog(IClientService clientService){

        this.clientSerivice = clientService;

        setTitle("Seleccionar cliente");
        setHeaderText("Buscar por Dni");

        DialogPane pane = getDialogPane();
        pane.getStyleClass().add("app-dialog");
        pane.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        pane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        dniSearchField.setPromptText("Ingresar DNI...");
        dniSearchField.textProperty().addListener((obs, old, text) -> searchClients(text));

        resultsList.setCellFactory(lv -> new ListCell<>(){
            @Override
            protected void updateItem(ClientDTO item, boolean empty){
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + " " + item.getLastName() + " - DNI: " + item.getDni() );
            }
        });

        VBox content = new VBox(10, dniSearchField, resultsList);
        content.setPrefWidth(400);
        pane.setContent(content);

        Node okButton = pane.lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(resultsList.getSelectionModel().selectedItemProperty().isNull());

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK){
                return resultsList.getSelectionModel().getSelectedItem();
            }
            return null;
        });
    }

    private void searchClients(String dni){
        if (dni == null || dni.isBlank()){
            resultsList.setItems(FXCollections.observableArrayList());
            return;
        }
        List<ClientDTO> results = clientSerivice.getAllClients().stream()
                .filter(c -> c.getDni() != null && c.getDni().contains(dni))
                .toList();
        resultsList.setItems(FXCollections.observableArrayList(results));
    }

}
