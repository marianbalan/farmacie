package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.*;
import service.Service;
import service.ServicesException;
import utils.events.Event;
import utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class MedicController implements RolController, Observer {

    public MedicController() {
    }

    @FXML
    private TextField nameTextField;
    @FXML
    private TableView<Comanda> tableComenzi;
    @FXML
    private TableColumn<Comanda, Integer> columnId;
    @FXML
    private TableColumn<Comanda, LocalDate> columnData;
    @FXML
    private TableColumn<Comanda, TipStatus> columnStatus;
    @FXML
    private TableView<MedicamentComanda> tableMedicamente;
    @FXML
    private TableColumn<MedicamentComanda, String> columnNume;
    @FXML
    private TableColumn<MedicamentComanda, String> columnProducator;
    @FXML
    private TableColumn<MedicamentComanda, TipMedicament> columnTip;
    @FXML
    private TableColumn<MedicamentComanda, Integer> columnCantitate;

    ObservableList<Comanda> comenziModel = FXCollections.observableArrayList();
    ObservableList<MedicamentComanda> medicamenteModel = FXCollections.observableArrayList();
    private Service service;
    private Medic medic;

    @Override
    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    @Override
    public void setUser(User user) {
        this.medic = new Medic(user);
        nameTextField.setText(user.getName());
        loadComenzi();
    }

    @FXML
    public void handleLogout() throws IOException {
        List<Window> open = Stage.getWindows().stream().filter(Window::isShowing).collect(Collectors.toList());
        open.forEach(Window::hide);
        loadLoginStage();
    }

    public void loadComenzi() {
        initComenziModel();
        initializeComenziTable();
    }

    public void initComenziModel() {
        try {
            comenziModel.clear();
            List<Comanda> comenzi = StreamSupport.stream(service.findMyOrders(medic).spliterator(), false).collect(Collectors.toList());
            comenziModel.setAll(comenzi);
        } catch (ServicesException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void initMedicamenteModel(Comanda comanda) {
        medicamenteModel.clear();
        List<MedicamentComanda> medicamente = service.findMedicamenteByComanda(comanda);
        medicamenteModel.setAll(medicamente);
    }

    public void initializaMedicamenteTable() {
        columnNume.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, String>("Nume"));
        columnProducator.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, String>("Producator"));
        columnTip.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, TipMedicament>("Tip"));
        columnCantitate.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, Integer>("Cantitate"));
        tableMedicamente.setItems(medicamenteModel);
    }

    public void initializeComenziTable() {
        columnId.setCellValueFactory(new PropertyValueFactory<Comanda, Integer>("Id"));
        columnData.setCellValueFactory(new PropertyValueFactory<Comanda, LocalDate>("Data"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<Comanda, TipStatus>("Status"));
        tableComenzi.setItems(comenziModel);
    }

    @FXML
    public void initMedicamente() {
            Comanda comanda = tableComenzi.getSelectionModel().getSelectedItem();
            initMedicamenteModel(comanda);
            initializaMedicamenteTable();
    }

    @FXML
    public void handleAnuleaza() {
        Comanda comanda = tableComenzi.getSelectionModel().getSelectedItem();
        if (comanda == null) {
            MessageAlert.showMessage(null, Alert.AlertType.WARNING, "", "Trebuie sa selectati o comanda!");
            return;
        }
        try {
            service.anuleazaComanda(comanda);
        } catch(ServicesException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @FXML
    public void handleEfectueaza() {
        try {
            loadComenziStage();
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    private void loadComenziStage() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/medicComandaView.fxml"));
        AnchorPane rootLayout = loader.load();
        stage.setScene(new Scene(rootLayout));
        ComandaController controller = loader.getController();
        controller.setService(service);
        controller.setMedic(medic);
        stage.show();
    }

    private void loadLoginStage() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/loginView.fxml"));
        AnchorPane rootLayout = loader.load();
        stage.setScene(new Scene(rootLayout));
        LoginController controller = loader.getController();
        controller.setService(service);
        stage.show();
    }

    @Override
    public void update(Event e) {
        initComenziModel();
    }
}
