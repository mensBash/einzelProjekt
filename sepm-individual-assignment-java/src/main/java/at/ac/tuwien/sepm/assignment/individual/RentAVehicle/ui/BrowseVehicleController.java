package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.InvalidInputException;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

public class BrowseVehicleController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;

    ObservableList<Vehicle> selectedVehicleList;
    List<Vehicle> arrayList;
    List<Vehicle> toRemove;

    @FXML private TableView<Vehicle> tv_vehicles;
        @FXML private TableColumn<Vehicle, String> tc_model;
        @FXML private TableColumn<Vehicle, Integer> tc_year;
        @FXML private TableColumn<Vehicle, Integer> tc_nrOfSeats;
        @FXML private TableColumn<Vehicle, Integer> tc_power;
        @FXML private TableColumn<Vehicle, String> tc_licence;
        @FXML private TableColumn<Vehicle, Double> tc_price;
        @FXML private Button button_close;
        @FXML private Button button_save;
        @FXML private DatePicker dp_dateLicence;
        @FXML private TextField tf_nrOfLicence;

        Alert alert;

    public BrowseVehicleController(RentService rentService, List<Vehicle> arrayList, List<Vehicle> toRemove) {
        this.rentService = rentService;
        this.arrayList = arrayList;
        this.toRemove = toRemove;
    }

    @FXML
    private void initialize(){
        List<Vehicle> nova = new ArrayList<Vehicle>(arrayList);
        if (toRemove != null) {
            nova.removeAll(toRemove);
        }
        tv_vehicles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tc_model.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("label"));
        tc_year.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("year"));
        tc_nrOfSeats.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("seats"));
        tc_power.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("power"));
        tc_licence.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("drivingLicence"));
        tc_price.setCellValueFactory(new PropertyValueFactory<Vehicle, Double>("price"));
        tv_vehicles.setItems(FXCollections.observableArrayList(nova));
    }


    @FXML
    private void onSelectVehicle (ActionEvent actionEvent){
        ObservableList<Vehicle> selectedVehicles = tv_vehicles.getSelectionModel().getSelectedItems();
        try {
            if (dp_dateLicence.getValue() != null && tf_nrOfLicence.getText() != null){
                ObservableList<Vehicle> neka = FXCollections.observableArrayList(new ArrayList<Vehicle>());
                for (Vehicle v :
                    selectedVehicles) {
                    v.setLicenceNr(tf_nrOfLicence.getText());
                    v.setLicenceDate(dp_dateLicence.getValue());
                    neka.add(v);
                }
                selectedVehicleList = neka;
            }else {
                selectedVehicleList = selectedVehicles;
            }
            rentService.checkDrivingLicence(selectedVehicleList, dp_dateLicence.getValue(), tf_nrOfLicence.getText());
            Stage stage = (Stage) button_save.getScene().getWindow();
            stage.close();
        } catch (InvalidInputException e) {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Incorrect data");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onCloseButton(ActionEvent actionEvent){
        LOG.info("Close button clicked");
        selectedVehicleList = FXCollections.observableArrayList(new ArrayList<Vehicle>());
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }
}
