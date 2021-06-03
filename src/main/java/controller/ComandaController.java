package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.*;
import service.Service;
import service.ServicesException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ComandaController {

    @FXML
    private TableView<Medicament> tableMedicamenteD;
    @FXML
    private TableColumn<Medicament, String> columnNumeD;
    @FXML
    private TableColumn<Medicament, String> columnProducatorD;
    @FXML
    private TableColumn<Medicament, TipMedicament> columnTipD;
    @FXML
    private TableColumn<Medicament, Integer> columnCantitateD;
    @FXML
    private TableView<MedicamentComanda> tableMedicamenteA;
    @FXML
    private TableColumn<MedicamentComanda, String> columnNumeA;
    @FXML
    private TableColumn<MedicamentComanda, String> columnProducatorA;
    @FXML
    private TableColumn<MedicamentComanda, TipMedicament> columnTipA;
    @FXML
    private TableColumn<MedicamentComanda, Integer> columnCantitateA;


    @FXML
    private TextField numeTextbox;
    @FXML
    private Spinner<Integer> cantitateSpinner;

    private Service service;
    private Medic medic;
    ObservableList<Medicament> medicamenteDModel = FXCollections.observableArrayList();
    ObservableList<MedicamentComanda> medicamenteAModel = FXCollections.observableArrayList();

    public void setService(Service service) {
        this.service = service;
    }

    public void setMedic(Medic user) {
        this.medic = user;
        initMedicamenteTable();
        initCantitateSpinner();
    }

    private void initCantitateSpinner() {
        SpinnerValueFactory<Integer> value = new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                if (getValue() == null)
                    setValue(1);
                else {
                    Integer value = getValue();
                    if(value>1)
                        setValue(value-steps);
                }
            }

            @Override
            public void increment(int steps) {
                if (this.getValue() == null)
                    setValue(1);
                else {
                    Integer value = getValue();
                    setValue(value+steps);
                }
            }
        };
        cantitateSpinner.setValueFactory(value);
    }

    public void initMedicamenteTable() {
        initMedicamenteModel();
        initializeMedicamenteTable();
    }

    public void initMedicamenteModel() {
        medicamenteDModel.clear();
        List<Medicament> medicamente = StreamSupport.stream(service.findAllMedicamente().spliterator(), false).collect(Collectors.toList());
        medicamenteDModel.setAll(medicamente);
    }


    public void initializeMedicamenteTable() {
        columnNumeD.setCellValueFactory(new PropertyValueFactory<Medicament, String>("Nume"));
        columnProducatorD.setCellValueFactory(new PropertyValueFactory<Medicament, String>("Producator"));
        columnTipD.setCellValueFactory(new PropertyValueFactory<Medicament, TipMedicament>("Tip"));
        columnCantitateD.setCellValueFactory(new PropertyValueFactory<Medicament, Integer>("CantitateTotala"));
        tableMedicamenteD.setItems(medicamenteDModel);
    }


    public void initializeMedicamentComandaTable() {
        columnNumeA.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, String>("Nume"));
        columnProducatorA.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, String>("Producator"));
        columnTipA.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, TipMedicament>("Tip"));
        columnCantitateA.setCellValueFactory(new PropertyValueFactory<MedicamentComanda, Integer>("Cantitate"));
        tableMedicamenteA.setItems(medicamenteAModel);
    }

    @FXML
    public void handleCauta() {
        String nume = numeTextbox.getText();
        List<Medicament> medicamente = nume.equals("") ? StreamSupport.stream(service.findAllMedicamente().spliterator(), false).collect(Collectors.toList())
                    : service.findMedicamenteByNume(nume);
        medicamenteDModel.clear();
        medicamenteDModel.setAll(medicamente);
    }

    @FXML
    public void handleAdauga() {
        Medicament medicament = tableMedicamenteD.getSelectionModel().getSelectedItem();
        if (cantitateSpinner.getValue() != null && medicament != null
                && cantitateSpinner.getValue() <= medicament.getCantitateTotala() ) {
            MedicamentComanda medicamentComanda = new MedicamentComanda(cantitateSpinner.getValue());
            Tuple<Integer, Integer> id = new Tuple<Integer, Integer>(-1, medicament.getId());
            medicamentComanda.setId(id);
            medicamentComanda.setMedicament(medicament);
            if (!medicamenteAModel.contains(medicamentComanda)) {
                medicamenteAModel.add(medicamentComanda);
                initializeMedicamentComandaTable();
            }
            return;
        }
        MessageAlert.showMessage(null, Alert.AlertType.WARNING, "Warning", "Datele sunt invalide!");
    }

    @FXML
    public void handleActualizeaza() {
        MedicamentComanda medicament = tableMedicamenteA.getSelectionModel().getSelectedItem();
        medicament.setCantitate(cantitateSpinner.getValue());
        int index = medicamenteAModel.indexOf(medicament);
        if (index >= 0) {
            medicamenteAModel.set(index, medicament);
            tableMedicamenteA.setItems(medicamenteAModel);
        }

    }

    @FXML
    public void handleElimina() {
        MedicamentComanda medicament = tableMedicamenteA.getSelectionModel().getSelectedItem();
        medicamenteAModel.remove(medicament);
        initializeMedicamentComandaTable();
    }

    @FXML
    public void handleTrimite() {
        try {
            service.addComanda(LocalDate.now(), TipStatus.pending, medic.getUsername(), medicamenteAModel);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Success", "Comanda a fost adaugata");
        } catch (ServicesException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}
