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
import java.util.List;

public class DetailReservationViewController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;
    private Reservation reservation;

    private List<Vehicle> vehicle;

    @FXML
    private TextField tf_name;
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
    @FXML private TextField tf_status;
    @FXML private TextField tf_creationOrBillDate;
    @FXML private TextField tf_billNr;

    @FXML private Label label_creation;
    @FXML private Label label_billNr;
    @FXML private Label label_billDate;
    @FXML private Label label_status;

    @FXML private Button button_close;
    @FXML private Button button_addVehicle;
    @FXML private Button butoon_removeVehicle;
    @FXML private Button button_update;
    @FXML private Button button_save;
    @FXML private Button button_pay;
    @FXML private Button button_cancel;

    List<Vehicle> initialList;

    boolean firstTime = true;

    boolean check = false;

    private Alert alert;
    ObservableList<LocalTime> time = FXCollections.observableArrayList();

    public DetailReservationViewController(RentService rentService, Reservation reservation) {
        this.rentService = rentService;
        this.reservation = reservation;
        try {
            this.initialList = rentService.getReservedVehicles(reservation.getId());
        } catch (ServiceException e) {
            e.printStackTrace();
            setAlert(e.getMessage());
        }
        LOG.debug("{}", reservation);
    }

    @FXML
    private void initialize(){
        tv_vehicles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (time.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                time.add(LocalTime.of(i, 0));
            }
        }
        tf_name.setText(reservation.getName());
        tf_nrOfIBAN.setText(reservation.getBankOrCreditCard());
        tf_sum.setText(reservation.getTotalPrice().toString());
        tf_status.setText(reservation.getStatus());
        if (reservation.getStatus().equals("open")){
            tf_creationOrBillDate.setText(reservation.getDate().toString());
            tf_billNr.setVisible(false);
            label_billDate.setVisible(false);
            label_billNr.setVisible(false);
            label_creation.setVisible(true);
            if (reservation.getDateFrom().isBefore(LocalDateTime.now())){
                System.out.println("" + LocalDateTime.now());
                button_pay.setVisible(true);
                button_cancel.setVisible(false);
                button_update.setDisable(true);
            }else {
                button_pay.setVisible(false);
                button_cancel.setVisible(true);
                button_update.setDisable(false);
            }
        }else {
            tf_creationOrBillDate.setText(reservation.getDateBill().toString());
            tf_billNr.setText(reservation.getNumberBill().toString());
            tf_billNr.setVisible(true);
            label_billDate.setVisible(true);
            label_billNr.setVisible(true);
            label_creation.setVisible(false);
            button_update.setVisible(false);
            button_cancel.setVisible(false);
            button_pay.setVisible(false);
        }

        dp_dateFrom.setValue(reservation.getDateFrom().toLocalDate());
        dp_dateTo.setValue(reservation.getDateTo().toLocalDate());
        cb_timeFrom.setValue(reservation.getDateFrom().toLocalTime());
        cb_timeFrom.setItems(time);
        cb_timeTo.setValue(reservation.getDateTo().toLocalTime());
        cb_timeTo.setItems(time);

        tc_model.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("label"));
        tc_year.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("year"));
        tc_licenceNr.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("licenceNr"));
        tc_licenceDate.setCellValueFactory(new PropertyValueFactory<Vehicle, LocalDate>("licenceDate"));
        tc_price.setCellValueFactory(new PropertyValueFactory<Vehicle, Double>("price"));
        if (check){
            label_creation.setVisible(false);
            button_cancel.setVisible(false);
            Double price = 0.0;
            for (Vehicle v: vehicle){
                price += v.getPrice();
            }
            tf_sum.setText(price.toString());
            if (!vehicle.isEmpty()){
                butoon_removeVehicle.setVisible(true);
            }else {
                butoon_removeVehicle.setVisible(false);
            }
        }else {
            try {
                vehicle = rentService.getReservedVehicles(reservation.getId());
                for (Vehicle v : vehicle) {
                    Double oldPrice = v.getPrice();
                    oldPrice *= ChronoUnit.HOURS.between(LocalDateTime.of(dp_dateFrom.getValue(), cb_timeFrom.getSelectionModel().getSelectedItem()), LocalDateTime.of(dp_dateTo.getValue(), cb_timeTo.getSelectionModel().getSelectedItem()));
                    v.setPrice(oldPrice);
                }
            } catch (ServiceException e) {
                e.printStackTrace();
                setAlert(e.getMessage());
            }
        }
        tv_vehicles.setItems(FXCollections.observableArrayList(vehicle));
        if (ChronoUnit.HOURS.between(LocalDateTime.now(), LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem())) < 24){
            button_cancel.setDisable(true);
        }
    }

    @FXML
    private void onAddVehicle(ActionEvent actionEvent) throws IOException {
        LOG.info("Add vehicle button clicked");
        if (checkDate()){
            LocalDateTime dateFrom = LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem());
            LocalDateTime dateTo = LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem());
            try {
                List<Vehicle> helpList = rentService.getVehicles(dateFrom, dateTo);
                helpList.addAll(initialList);
                BrowseVehicleController browseVehicleController = new BrowseVehicleController(rentService, helpList, vehicle);
                LOG.debug("date from {}", dateFrom);
                LOG.debug("date to {}", dateTo);
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
            } catch (InvalidInputException | ServiceException e) {
                setAlert(e.getMessage());
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
    private void onUpdateButton(ActionEvent actionEvent){
        LOG.info("Update button clicked");
        tf_name.setEditable(true);
        tf_nrOfIBAN.setEditable(true);
        tf_status.setVisible(false);
        label_status.setVisible(false);
        tf_billNr.setVisible(false);
        label_billNr.setVisible(false);
        label_billDate.setVisible(false);
        label_creation.setVisible(false);
        button_cancel.setVisible(false);
        tf_creationOrBillDate.setVisible(false);
        button_pay.setVisible(false);
        button_update.setVisible(false);

        button_addVehicle.setVisible(true);
        butoon_removeVehicle.setVisible(true);
        button_save.setVisible(true);
        check = true;
    }

    @FXML
    private void onCloseButton(ActionEvent actionEvent){
        LOG.info("Close button clicked");
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onSaveButton(ActionEvent actionEvent){
        LOG.info("Save button clicked");
        if (validateInput()){
            LocalDateTime dateFrom = LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem());
            LocalDateTime dateTo = LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem());
            for (Vehicle v: vehicle){
                Double price = v.getPrice();
                v.setPrice(price / ChronoUnit.HOURS.between(LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem()), LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem())));
            }
            Reservation reservation = new Reservation(tf_name.getText(), tf_nrOfIBAN.getText(), dateFrom, dateTo, vehicle,Double.parseDouble(tf_sum.getText()));
            reservation.setId(this.reservation.getId());
            reservation.setDate(this.reservation.getDate());
            try {
                rentService.updateReservation(reservation);
                Stage stage = (Stage) button_save.getScene().getWindow();
                stage.close();
            } catch (InvalidInputException | ServiceException e) {
                setAlert(e.getMessage());
            }
        }

    }

    @FXML
    private void onPayButton(ActionEvent actionEvent){
        try {
            rentService.payReservation(reservation);
        } catch (ServiceException e) {
            e.printStackTrace();
            setAlert(e.getMessage());
        }
        Stage stage = (Stage) button_pay.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCancelButton(ActionEvent actionEvent){
        try {
            rentService.cancelReservation(reservation);
        } catch (ServiceException e) {
            e.printStackTrace();
            setAlert(e.getMessage());
        }
        Stage stage = (Stage) button_cancel.getScene().getWindow();
        stage.close();
    }

    private boolean checkDate(){
        LOG.info("Checking if date ");
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
