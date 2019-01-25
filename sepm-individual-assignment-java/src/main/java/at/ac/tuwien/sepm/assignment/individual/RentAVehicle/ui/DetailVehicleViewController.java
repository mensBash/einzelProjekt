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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

public class DetailVehicleViewController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;
    private Vehicle vehicle;
    @FXML private TextField tf_model;
    @FXML private TextField tf_year;
    @FXML private TextField tf_description;
    @FXML private TextField tf_seatNr;
    @FXML private TextField tf_licenceNr;
    @FXML private ChoiceBox cb_typeOfDrive;
    @FXML private TextField tf_power;
    @FXML private TextField tf_price;
    @FXML private CheckBox cb_licenceA;
    @FXML private CheckBox cb_licenceB;
    @FXML private CheckBox cb_licenceC;
    @FXML private TextField tf_addPicture;
    @FXML private TextField tf_dateCreate;
    @FXML private TextField tf_dateUpdate;
    @FXML private ImageView iv_image;
    @FXML private Button button_close;
    @FXML private Button button_update;
    @FXML private Button button_saveChanges;
    @FXML private Button button_ChangePicture;
    @FXML private Button button_delete;
    @FXML private Label label_noPicture;
    @FXML private Button button_addReservation;

    private Alert alert;
    Integer year;
    Integer seats;
    Integer power;
    Double price;
    String drivingLicence;

    ObservableList<String> listTypeOfDrive = FXCollections.observableArrayList("motorized", "muscle power");

    @FXML

    private void initialize(){
        tf_model.setText(vehicle.getLabel());
        tf_year.setText(vehicle.getYear().toString());
        tf_description.setText(vehicle.getDescription());
        tf_seatNr.setText(vehicle.getSeats().toString());
        tf_licenceNr.setText(vehicle.getRegistrationNr());
        cb_typeOfDrive.setValue(vehicle.getType());
        cb_typeOfDrive.setItems(listTypeOfDrive);
        tf_power.setText(vehicle.getPower().toString());
        tf_price.setText(vehicle.getPrice().toString());
        tf_addPicture.setText(vehicle.getPicture());
        tf_dateCreate.setText(vehicle.getDate().toString());
        if (vehicle.getDateChanged() != null) {
            tf_dateUpdate.setText(vehicle.getDateChanged().toString());
        }
        selectDrivingLicence();
        if (vehicle.getPicture() != null) {
            iv_image.setImage(new Image("file:" + vehicle.getPicture()));
        }else {
            label_noPicture.setVisible(true);
        }
    }
    public DetailVehicleViewController(RentService rentService, Vehicle vehicle) {
        this.rentService = rentService;
        this.vehicle = vehicle;
    }

    private void selectDrivingLicence(){
        int i;
        for (i = 0; i < vehicle.getDrivingLicence().length(); i++){
            if(vehicle.getDrivingLicence().charAt(i) == 'A'){
                cb_licenceA.setIndeterminate(false);
                cb_licenceA.setSelected(true);
            }
            if (vehicle.getDrivingLicence().charAt(i) == 'B'){
                cb_licenceB.setIndeterminate(false);
                cb_licenceB.setSelected(true);
            }
            if (vehicle.getDrivingLicence().charAt(i) == 'C'){
                cb_licenceC.setIndeterminate(false);
                cb_licenceC.setSelected(true);
            }
        }
    }
    @FXML
    public void onUpdateButton(ActionEvent actionEvent){
        LOG.info("Updating values of vehicle");
        tf_model.setEditable(true);
        tf_year.setEditable(true);
        tf_description.setEditable(true);
        tf_seatNr.setEditable(true);
        tf_licenceNr.setEditable(true);
        cb_typeOfDrive.setDisable(false);
        tf_power.setEditable(true);
        tf_price.setEditable(true);
        cb_licenceA.setDisable(false);
        cb_licenceB.setDisable(false);
        cb_licenceC.setDisable(false);
        tf_addPicture.setEditable(true);
        button_update.setVisible(false);
        button_delete.setVisible(false);
        button_saveChanges.setVisible(true);
        button_ChangePicture.setVisible(true);
        button_addReservation.setVisible(false);
        initialize();
    }

    @FXML void onChangePicture(ActionEvent actionEvent){
        LOG.info("Change picture button clicked");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Pictures (*.jpeg;*.png)", "*.jpeg","*.png");
        fileChooser.getExtensionFilters().addAll(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            LOG.info("Picture selected");
            iv_image.setImage(new Image("file:" + vehicle.getPicture()));
            tf_addPicture.setText(file.getAbsolutePath());
        }else {
            LOG.info("Picture not selected!");
        }
    }

    @FXML
    public void onSaveChanges(ActionEvent actionEvent){
        LOG.info("Save changes button clicked");
        checked(cb_licenceA, cb_licenceB, cb_licenceC);
        if(parseIntegers(tf_year,tf_seatNr,tf_power, tf_price) && validateInput(tf_model, cb_typeOfDrive)) {
            Vehicle vehicle = new Vehicle(tf_model.getText(), year, tf_description.getText(), seats, tf_licenceNr.getText(), cb_typeOfDrive.getSelectionModel().getSelectedItem().toString(), power, price, tf_addPicture.getText(), drivingLicence);
            vehicle.setId(this.vehicle.getId());
            vehicle.setDate(LocalDate.parse(tf_dateCreate.getText()));
            vehicle.setDateChanged(LocalDate.now());
            try {
                rentService.updateVehicle(vehicle);
                Stage stage = (Stage) button_saveChanges.getScene().getWindow();
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

    @FXML
    public void onDelete(ActionEvent actionEvent){
        LOG.info("Delete button clicked");
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm deletion");
        alert.setHeaderText("Do you really want to delete this vehicle");
        alert.setContentText("It will not be possible to retrieve it");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            LOG.info("Deleting confirmed");
            try {
                rentService.deleteVehicle(vehicle);
            } catch (ServiceException e) {
                e.printStackTrace();
                LOG.debug("Exception occurred");
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText("Data access object failure");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            LOG.info("Deleting aborted");
        }
        Stage stage = (Stage) button_delete.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onCloseButton(ActionEvent actionEvent){
        LOG.info("Close button clicked");
        Stage stage = (Stage) button_close.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput(TextField tf_model, ChoiceBox cb_typeOfDrive){
        String model = tf_model.getText();
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
        String warning = "";
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

    public void onAddToReservation(ActionEvent actionEvent) throws IOException {
        LOG.info("Add to reservation button clicked");
        ObservableList<Vehicle> addList = FXCollections.observableArrayList(new ArrayList<Vehicle>());
        addList.add(vehicle);
        if (!addList.isEmpty()){
            VehicleToReservationController vehicleToReservationController = new VehicleToReservationController(rentService, addList);
            FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/VehicleToReservation.fxml"));
            addVehicleLoader.setControllerFactory(param -> param.isInstance(vehicleToReservationController) ? vehicleToReservationController : null);
            Stage appStage = new Stage();
            appStage.initModality(Modality.APPLICATION_MODAL);
            appStage.setTitle("Add to reservation");
            appStage.setScene(new Scene(addVehicleLoader.load()));
            appStage.showAndWait();
        }
    }

}
