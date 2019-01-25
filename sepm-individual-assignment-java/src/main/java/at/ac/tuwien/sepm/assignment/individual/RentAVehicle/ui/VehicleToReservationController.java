package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class VehicleToReservationController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML private DatePicker dp_dateFrom;
    @FXML private ChoiceBox<LocalTime> cb_timeFrom;
    @FXML private DatePicker dp_dateTo;
    @FXML private ChoiceBox<LocalTime> cb_timeTo;
    @FXML private Button button_proceed;
    @FXML private Button button_close;

    List<Vehicle> vehicles;

    private RentService rentService;
    private Alert alert;

    ObservableList<LocalTime> time = FXCollections.observableArrayList();

    public VehicleToReservationController(RentService rentService, List<Vehicle> vehicles) {
        this.rentService = rentService;
        this.vehicles = vehicles;
    }

    @FXML
    private void initialize() {
        if (time.isEmpty()) {
            for (int i = 0; i < 24; i++) {
                time.add(LocalTime.of(i, 0));
            }
        }
        cb_timeFrom.setItems(time);
        cb_timeFrom.setValue(LocalTime.of(0,0));
        cb_timeTo.setItems(time);
        cb_timeTo.setValue(LocalTime.of(0,0));
    }

    @FXML
    private void onProceedButton(ActionEvent actionEvent) throws IOException {
        LOG.info("Proceed button clicked");
        LocalDateTime dateFrom = LocalDateTime.of(dp_dateFrom.getValue(),cb_timeFrom.getSelectionModel().getSelectedItem());
        LocalDateTime dateTo = LocalDateTime.of(dp_dateTo.getValue(),cb_timeTo.getSelectionModel().getSelectedItem());
        try {
            if (rentService.getVehicles(dateFrom, dateTo).containsAll(vehicles)){
                AddReservationController addReservationController = new AddReservationController(rentService);

                FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/AddReservation.fxml"));
                addVehicleLoader.setControllerFactory(param -> param.isInstance(addReservationController) ? addReservationController : null);
                Stage appStage = new Stage();
                appStage.initModality(Modality.APPLICATION_MODAL);
                appStage.setTitle("Add new Reservation");
                appStage.setScene(new Scene(addVehicleLoader.load()));
                appStage.show();
                addReservationController.setVehicleList(vehicles);
                addReservationController.addVehicle(dateFrom, dateTo);
                button_close.fire();
            }else {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Vehicles are not available");
                alert.setContentText("Sorry, vehicles that you have chosen are not available in that period of time");
                alert.showAndWait();
            }
        } catch (InvalidInputException | ServiceException e) {
            LOG.debug("Exception occurred");
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Invalid input");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void onCloseButton(ActionEvent actionEvent){
        LOG.info("Close button clicked");
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }

}
