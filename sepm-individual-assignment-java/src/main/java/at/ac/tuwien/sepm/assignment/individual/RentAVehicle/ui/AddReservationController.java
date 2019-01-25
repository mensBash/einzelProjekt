package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.InvalidInputException;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddReservationController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;

    private List<Vehicle> vehicle;

    @FXML private TextField tf_name;
    @FXML private TextField tf_nrOfIBAN;
    @FXML private TableView<Vehicle> tv_vehicles;
        @FXML private TableColumn<Vehicle, String> tc_model;
        @FXML private TableColumn<Vehicle, Integer> tc_year;
        @FXML private TableColumn<Vehicle, String> tc_licenceNr;
        @FXML private TableColumn<Vehicle, LocalDate> tc_licenceDate;
        @FXML private TableColumn<Vehicle, Double> tc_price;
    @FXML private TextField tf_sum;
    @FXML private DatePicker dp_dateFrom;
    @FXML private ChoiceBox<LocalTime> cb_timeFrom;
    @FXML private DatePicker dp_dateTo;
    @FXML private ChoiceBox<LocalTime> cb_timeTo;
    @FXML private Button button_close;
    @FXML private Button butoon_removeVehicle;
    @FXML private Button button_addVehicle;

    private List<Vehicle> vehicleList;
    private boolean check = false;

    private Alert alert;

    ObservableList<LocalTime> time = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        tv_vehicles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (time.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                time.add(LocalTime.of(i, 0));
            }
        }
        cb_timeFrom.setItems(time);
        cb_timeTo.setItems(time);
        if (vehicle != null) {
            tc_model.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("label"));
            tc_year.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("year"));
            tc_licenceNr.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("licenceNr"));
            tc_licenceDate.setCellValueFactory(new PropertyValueFactory<Vehicle, LocalDate>("licenceDate"));
            tc_price.setCellValueFactory(new PropertyValueFactory<Vehicle, Double>("price"));
            Double price = 0.0;
            for (Vehicle v :
                vehicle) {
                price += v.getPrice();
            }
            tv_vehicles.setItems(FXCollections.observableArrayList(vehicle));
            tf_sum.setText(price.toString());
            if (!vehicle.isEmpty()){
                butoon_removeVehicle.setVisible(true);
            }else {
                butoon_removeVehicle.setVisible(false);
            }
        }else {
            vehicle = new ArrayList<>();
        }
    }

    public AddReservationController(RentService rentService) {
        this.rentService = rentService;
    }

    @FXML
    private void onAddVehicle(ActionEvent actionEvent) throws IOException {
        LOG.info("Add vehicle button clicked");
        if (checkDate()){
            LocalDateTime dateFrom = LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem());
            LocalDateTime dateTo = LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem());
            try {
                if (!check) {
                    try {
                        vehicleList = rentService.getVehicles(dateFrom, dateTo);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                        setAlert(e.getMessage());
                    }
                }else {
                    check = false;
                }
                BrowseVehicleController browseVehicleController = new BrowseVehicleController(rentService, vehicleList, vehicle);
                FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/BrowseVehicle.fxml"));
                addVehicleLoader.setControllerFactory(param -> param.isInstance(browseVehicleController) ? browseVehicleController : null);
                Stage appStage = new Stage();
                appStage.initModality(Modality.APPLICATION_MODAL);
                appStage.setTitle("Browse Vehicles");
                appStage.setScene(new Scene(addVehicleLoader.load()));
                appStage.showAndWait();
                if (browseVehicleController.selectedVehicleList != null) {
                    for (Vehicle v : browseVehicleController.selectedVehicleList) {
                        Double oldPrice = v.getPrice();
                        v.setPrice(oldPrice * ChronoUnit.HOURS.between(LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem()), LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem())));
                        vehicle.add(v);
                    }
                    initialize();
                }
                dp_dateFrom.setDisable(true);
                dp_dateTo.setDisable(true);
                cb_timeFrom.setDisable(true);
                cb_timeTo.setDisable(true);
            } catch (InvalidInputException e) {
                LOG.debug("Exception occurred");
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Invalid input");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void onRemoveVehicle(ActionEvent actionEvent){
        LOG.info("Remove Vehicle button clicked");
        ObservableList<Vehicle> selectedVehicles = tv_vehicles.getSelectionModel().getSelectedItems();
        vehicle.removeAll(selectedVehicles);
        initialize();
    }

    @FXML
    private void onSaveButton(ActionEvent actionEvent){
        LOG.info("Save button clicked");
        if (validateInput()){
            LocalDateTime dateFrom = LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem());
            LocalDateTime dateTo = LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem());
            for (Vehicle v: vehicle){
                Double price = v.getPrice();
                price /= ChronoUnit.HOURS.between(LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem()), LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem()));
                v.setPrice(price);
            }
            Reservation reservation = new Reservation(tf_name.getText(), tf_nrOfIBAN.getText(), dateFrom, dateTo, vehicle,Double.parseDouble(tf_sum.getText()));
            try {
                if(ChronoUnit.DAYS.between(LocalDateTime.now(), LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem())) < 7) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm action");
                    alert.setHeaderText("Do you really want to save this reservation");
                    alert.setContentText("It will not be possible to cancel the reservation for free");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        LOG.info("Saving confirmed");
                        rentService.saveReservation(reservation);
                        Stage stage = (Stage) button_close.getScene().getWindow();
                        stage.close();
                    } else {
                        LOG.info("Saving aborted");
                    }
                }else{
                    rentService.saveReservation(reservation);
                    Stage stage = (Stage) button_close.getScene().getWindow();
                    stage.close();
                }
            } catch (InvalidInputException | ServiceException e) {
                setAlert(e.getMessage());
            }
        }
    }
    public void addVehicle(LocalDateTime dateFrom, LocalDateTime dateTo){
            dp_dateFrom.setValue(dateFrom.toLocalDate());
            cb_timeFrom.setValue(dateFrom.toLocalTime());
            dp_dateTo.setValue(dateTo.toLocalDate());
            cb_timeTo.setValue(dateTo.toLocalTime());
            button_addVehicle.fire();
    }

    @FXML
    private void onCloseButton(ActionEvent actionEvent){
        LOG.info("Close button clicked");
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
        this.check = true;
    }

    private boolean checkDate(){
        LOG.info("Checking date ");
        boolean check = true;
        String warning = "";
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Empty field");
        if (dp_dateFrom.getValue() == null){
            LOG.debug("Date from is empty");
            warning += "Date from is empty\n";
            check = false;
        }
        if (cb_timeFrom.getSelectionModel().isEmpty()){
            LOG.debug("Time from is empty");
            warning += "Time from is empty\n";
            check = false;
        }
        if (dp_dateTo.getValue() == null){
            LOG.debug("Date to is empty");
            warning += "Date to is empty\n";
            check = false;
        }
        if (cb_timeTo.getSelectionModel().isEmpty()){
            LOG.debug("Time to is empty");
            warning += "Time to is empty\n";
            check = false;
        }
        if (!check) {
            alert.setContentText(warning);
            alert.showAndWait();
        }
        return check;
    }

    private boolean validateInput(){
        LOG.info("Validating all input fields");
        boolean check = true;
        String warning= "";
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Empty field");
        if (tf_name.getText().trim().isEmpty()){
            LOG.debug("Name of customer is empty");
            warning += "Name of customer is empty\n";
            check = false;
        }
        if (tf_nrOfIBAN.getText().trim().isEmpty()){
            LOG.debug("IBAN/Credit card number is empty");
            warning += "IBAN/Credit card number is empty\n";
            check = false;
        }
        if (vehicle.isEmpty()){
            LOG.debug("Vehicle(s) need to be added for booking");
            warning += "Vehicle(s) need to be added for booking\n";
            check = false;
        }
        if (!check) {
            alert.setContentText(warning);
            alert.showAndWait();
        }
        return check;
    }

    private void setAlert(String message){
        LOG.debug("Exception occurred {}", message);
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Invalid input");
        alert.setContentText(message);
        alert.showAndWait();
    }


}
