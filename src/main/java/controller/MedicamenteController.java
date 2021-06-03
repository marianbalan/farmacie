package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Farmacist;
import model.Medic;
import model.Medicament;
import model.validators.ValidationException;
import service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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



public class MedicamenteController implements Observer {

    @FXML
    public TableView<Medicament> medicamenteTable;
    @FXML
    public TableColumn<Medicament, String> numeColumn;
    @FXML
    public TableColumn<Medicament, String> producatorColumn;
    @FXML
    public TableColumn<Medicament, TipMedicament> tipColumn;
    @FXML
    public TableColumn<Medicament, Integer> cantitateColumn;
    @FXML
    public TextField numeTextbox;
    @FXML
    public TextField producatorTextbox;
    @FXML
    public ComboBox<TipMedicament> tipComboList;
    @FXML
    public Spinner<Integer> cantitateSpinner;

    private Service service;
    private Farmacist farmacist;

    private ObservableList<Medicament> medicamenteModel = FXCollections.observableArrayList();

    public void setService(Service service) {
        this.service = service;
        service.addObserver(this);
    }

    public void setFarmacist(Farmacist user) {
        this.farmacist = user;
        loadMedicamenteTable();
        initCantitateSpinner();
        initializeTipComboList();
    }

    private void loadMedicamenteTable() {
        initMedicamenteModel();
        initializeMedicamenteTable();
    }

    private void initCantitateSpinner() {
        SpinnerValueFactory<Integer> value = new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                if (getValue() == null)
                    setValue(0);
                else {
                    Integer value = getValue();
                    if(value>0)
                        setValue(value-steps);
                }
            }

            @Override
            public void increment(int steps) {
                if (this.getValue() == null)
                    setValue(0);
                else {
                    Integer value = getValue();
                    setValue(value+steps);
                }
            }
        };
        cantitateSpinner.setValueFactory(value);
    }

    private void initializeTipComboList() {
        tipComboList.getItems().setAll(TipMedicament.values());
    }

    public void initMedicamenteModel() {
        List<Medicament> medicamente = StreamSupport.stream(service.findAllMedicamente().spliterator(), false).collect(Collectors.toList());
        medicamenteModel.setAll(medicamente);
    }

    public void initializeMedicamenteTable() {
        numeColumn.setCellValueFactory(new PropertyValueFactory<Medicament, String>("Nume"));
        producatorColumn.setCellValueFactory(new PropertyValueFactory<Medicament, String>("Producator"));
        tipColumn.setCellValueFactory(new PropertyValueFactory<Medicament, TipMedicament>("Tip"));
        cantitateColumn.setCellValueFactory(new PropertyValueFactory<Medicament, Integer>("CantitateTotala"));
        medicamenteTable.setItems(medicamenteModel);
    }

    @FXML
    public void handleAdauga(){
        String nume = numeTextbox.getText();
        String producator = producatorTextbox.getText();
        TipMedicament tip = tipComboList.getSelectionModel().getSelectedItem();
        Integer cantitate = cantitateSpinner.getValue();
        Medicament medicament = new Medicament(nume, producator, tip, cantitate);
        try {
            service.adaugaMedicament(medicament);
        } catch (ServicesException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @FXML
    public void handleElimina(){
        Medicament medicament = medicamenteTable.getSelectionModel().getSelectedItem();
        service.stergeMedicament(medicament);
    }

    @FXML
    public void handleActualizeaza(){
        Integer medicamentId = medicamenteTable.getSelectionModel().getSelectedItem().getId();
        Integer cantitate = cantitateSpinner.getValue();
        try {
            service.actualizeazaMedicament(medicamentId, cantitate);
        } catch (ServicesException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @Override
    public void update(Event e) {
        initMedicamenteModel();
    }
}
