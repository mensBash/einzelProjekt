package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.InvalidInputException;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.ServiceException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class AddVehicleController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;
    @FXML private TextField tf_model;
    @FXML private TextField tf_description;
    @FXML private TextField tf_seatNr;
    @FXML private TextField tf_licenceNr;
    @FXML private TextField tf_power;
    @FXML private TextField tf_price;
    @FXML private ChoiceBox cb_typeOfDrive;
    @FXML private TextField tf_year;
    @FXML private CheckBox cb_licenceA;
    @FXML private CheckBox cb_licenceB;
    @FXML private CheckBox cb_licenceC;
    @FXML private TextField tf_addPicture;
    @FXML private Button button_close;
    private Alert alert;
    Integer year;
    Integer seats;
    Integer power;
    Double price;
    String drivingLicence;

    ObservableList<String> listTypeOfDrive = FXCollections.observableArrayList("motorized", "muscle power");

    @FXML
    private void initialize(){
        cb_typeOfDrive.setValue("motorized");
        cb_typeOfDrive.setItems(listTypeOfDrive);
    }

    public AddVehicleController(RentService rentService) {
        this.rentService = rentService;
    }

    public void onAddButton(ActionEvent actionEvent) {
        LOG.info("Add button clicked");
        checked(cb_licenceA, cb_licenceB, cb_licenceC);
        if(parseIntegers(tf_year,tf_seatNr,tf_power, tf_price) && validateInput(tf_model, cb_typeOfDrive)) {
            Vehicle vehicle = new Vehicle(tf_model.getText(), year, tf_description.getText(), seats, tf_licenceNr.getText(), cb_typeOfDrive.getSelectionModel().getSelectedItem().toString(), power, price, tf_addPicture.getText(), drivingLicence);
            try {
                rentService.saveVehicle(vehicle);
                Stage stage = (Stage) button_close.getScene().getWindow();
                stage.close();
            } catch (InvalidInputException | ServiceException e) {
                LOG.debug("Exception occurred");
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Invalid input");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private boolean validateInput(TextField tf_model, ChoiceBox cb_typeOfDrive){
        String model;
        model = tf_model.getText();
        String type = cb_typeOfDrive.getSelectionModel().getSelectedItem().toString();
        String warning = "";
        LOG.info("Validating all input fields");
        boolean check = true;
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Empty field");
        if (model.trim().isEmpty()){
            LOG.debug("Model of vehicle is empty");
            warning += "Model of vehicle is empty\n";
            check = false;
        }
        if (year == null){
            LOG.debug("Year of vehicle is empty");
            warning += "Year of vehicle is empty\n";
            check = false;
        }
        if (type.trim().isEmpty()){
            LOG.debug("Type of vehicle is empty");
            warning += "Type of vehicle is empty\n";
            check = false;
        }
        if (price == null){
            LOG.debug("Price of vehicle is empty");
            warning += "Price of vehicle is empty\n";
            check = false;
        }
        if (!check) {
            alert.setContentText(warning);
            alert.showAndWait();
        }
        return check;
    }

    public void onCloseButton(ActionEvent actionEvent) {
        LOG.info("Close button clicked");
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }

    public void onAddPictureButton(ActionEvent actionEvent){
        LOG.info("Add picture button clicked");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pictures (*.jpeg;*.png)", "*.jpeg","*.png");
        fileChooser.getExtensionFilters().addAll(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            LOG.info("Picture selected");
            tf_addPicture.setText(file.getAbsolutePath());
        }else {
            LOG.info("Picture not selected!");
        }

    }

    private void checked(CheckBox cb_licenceA, CheckBox cb_licenceB, CheckBox cb_licenceC){
        drivingLicence = "";
        if (cb_licenceA.isSelected()){
            drivingLicence += "A";
        }
        if (cb_licenceB.isSelected()){
            drivingLicence += "B";
        }
        if (cb_licenceC.isSelected()){
            drivingLicence += "C";
        }
    }

    private boolean parseIntegers(TextField tf_year, TextField tf_seatNr, TextField tf_power, TextField tf_price) {
        LOG.info("Parsing integers");
        String warning;
        warning = "";
        boolean check = true;
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Invalid number in field");
        if (tf_year.getText().trim().isEmpty()) {
            year = null;
        } else {
            try {
                year = Integer.parseInt(tf_year.getText());
            }catch (Exception e) {
                warning += "Invalid year number\n";
                check = false;
            }
        }
        if (tf_seatNr.getText().trim().isEmpty()) {
            seats = 0;
        } else {
            try {
                seats = Integer.parseInt(tf_seatNr.getText());
            }catch (Exception e){
                warning += "Invalid seat number\n";
                check = false;
            }
        }
        if (tf_power.getText().trim().isEmpty()) {
            power = 0;
        } else {
            try {
                power = Integer.parseInt(tf_power.getText());
            }catch (Exception e){
                warning += "Invalid power number\n";
                check = false;
            }
        }
        if (tf_price.getText().trim().isEmpty()) {
            price = null;
        } else {
            try {
                price = Double.parseDouble(tf_price.getText());
            }catch (Exception e){
                warning += "Invalid price number\n";
                check = false;
            }
        }
        if (!check) {
            alert.setContentText(warning);
            alert.showAndWait();
        }
        return check;
    }
}
