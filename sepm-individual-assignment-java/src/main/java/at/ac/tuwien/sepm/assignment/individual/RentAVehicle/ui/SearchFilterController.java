package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.InvalidInputException;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class SearchFilterController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;

    @FXML private CheckBox cb_licenceA;
    @FXML private CheckBox cb_licenceB;
    @FXML private CheckBox cb_licenceC;
    @FXML private TextField tf_priceLowest;
    @FXML private TextField tf_priceHighest;
    @FXML private DatePicker dp_dateFrom;
    @FXML private DatePicker dp_dateTo;
    @FXML private ChoiceBox<LocalTime> cb_timeFrom;
    @FXML private ChoiceBox<LocalTime> cb_timeTo;
    @FXML private TextField tf_model;
    @FXML private ChoiceBox cb_type;
    @FXML private TextField tf_seats;
    @FXML private Button button_search;

    List<Vehicle> filterVehicle;

    Alert alert;

    ObservableList<LocalTime> time = FXCollections.observableArrayList();
    ObservableList<String> listTypeOfDrive = FXCollections.observableArrayList("motorized", "muscle power");

    public SearchFilterController(RentService rentService) {
        this.rentService = rentService;
    }

    @FXML
    private void initialize(){
        if (time.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                time.add(LocalTime.of(i, 0));
            }
        }
        cb_timeFrom.setItems(time);
        cb_timeTo.setItems(time);
        cb_type.setItems(listTypeOfDrive);
    }

    @FXML
    private void onSearchButton(ActionEvent actionEvent){
        LOG.info("Search button clicked");
        String query = "";
        boolean check = false;
        if (dp_dateFrom.getValue() != null && dp_dateTo.getValue() != null){
            LocalTime timeFrom;
            LocalTime timeTo;
            if (cb_timeFrom.getSelectionModel().getSelectedItem() == null || cb_timeTo.getSelectionModel().getSelectedItem() == null){
                timeFrom = LocalTime.of(0,0);
                timeTo = LocalTime.of(0,0);
            }else {
                timeFrom = cb_timeFrom.getSelectionModel().getSelectedItem();
                timeTo = cb_timeTo.getSelectionModel().getSelectedItem();
            }

            Timestamp dateFrom = Timestamp.valueOf(LocalDateTime.of(dp_dateFrom.getValue(),timeFrom));
            Timestamp dateTo = Timestamp.valueOf(LocalDateTime.of(dp_dateTo.getValue(),timeTo));
            query += " id NOT IN(SELECT b.vehicleId FROM Bill b INNER JOIN Reservation r ON r.id = b.reservationId  WHERE r.dateFrom <= '" + dateFrom + "' AND r.dateTo >= '" + dateTo + "' AND status = 'open') ";
            check = true;
        }
        if (cb_licenceA.isSelected() || cb_licenceB.isSelected() || cb_licenceC.isSelected()){
            if (check){
                query += " AND ";
            }
            query += "drivingLicence LIKE '%";
            if (cb_licenceA.isSelected()){
                query += "A%' ";
            } else if (cb_licenceB.isSelected()) {
                query += "B%' ";
            }else {
                query += "C%' ";
            }
            check = true;
        }
        String message = "";
        boolean warning = false;
        if (!tf_priceLowest.getText().trim().isEmpty()){
            Double price;
            try {
                if (check){
                    query +=" AND ";
                }
                query += "price >= '" + rentService.parseInputs(tf_priceLowest.getText()) + "' ";

            }catch (InvalidInputException e){
                LOG.debug("Exception occurred");
                warning = true;
                message += "Invalid lowest price number\n";
            }
            check = true;
        }
        if (!tf_priceHighest.getText().trim().isEmpty()){
            Double price;
            try {
                if (check){
                    query +=" AND ";
                }
                query += "price <= '" + rentService.parseInputs(tf_priceHighest.getText()) + "' ";

            }catch (InvalidInputException e){
                LOG.debug("Exception occurred");
                warning = true;
                message += "Invalid highest price number\n";
            }
            check = true;
        }

        if (!tf_model.getText().trim().isEmpty()){
            if (check){
                query +=" AND ";
            }
            query += " upper(label) LIKE upper('%" + tf_model.getText() + "%') ";
            check = true;
        }

        if (cb_type.getSelectionModel().getSelectedItem() != null){
            if (check){
                query +=" AND ";
            }
            query += "type = '" + cb_type.getSelectionModel().getSelectedItem().toString() + "' ";
            check = true;
        }

        if (!tf_seats.getText().trim().isEmpty()){
            Integer seats;
            try {
                if (check){
                    query +=" AND ";
                }
                query += "nrOfSeats = '" + rentService.parseInputs(tf_seats.getText()).intValue() + "' ";

            }catch (InvalidInputException e){
                LOG.debug("Exception occurred");
                warning = true;
                message += "Invalid seat number\n";
            }
            check = true;
        }

        if (!warning) {
            if (check) {
                query = "SELECT * FROM Vehicle WHERE " + query + " AND isDeleted = false";
            } else {
                query = "SELECT * FROM Vehicle WHERE isDeleted = false ORDER BY id ASC";
            }
            try {
                filterVehicle = rentService.filterVehicle(query);
            } catch (ServiceException e) {
                e.printStackTrace();
                LOG.debug("Exception occurred");
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Data Access Object Failure");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
            Stage stage = (Stage) button_search.getScene().getWindow();
            stage.close();
        }else {
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid input");
            alert.setContentText(message);
            alert.showAndWait();
        }
    }

    @FXML
    private void onA(ActionEvent actionEvent){
        if (cb_licenceA.isSelected()){
            cb_licenceB.setSelected(false);
            cb_licenceC.setSelected(false);
        }
    }

    @FXML
    private void onB(ActionEvent actionEvent){
        if (cb_licenceB.isSelected()){
            cb_licenceA.setSelected(false);
            cb_licenceC.setSelected(false);
        }
    }

    @FXML
    private void onC(ActionEvent actionEvent){
        if (cb_licenceC.isSelected()){
            cb_licenceB.setSelected(false);
            cb_licenceA.setSelected(false);
        }
    }

    public List<Vehicle> getFilterVehicle() {
        return filterVehicle;
    }
}
