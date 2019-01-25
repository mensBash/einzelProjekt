package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.ServiceException;
import javafx.application.Platform;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VehicleTableViewController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;

    @FXML private TableView<Vehicle> tv_table;
    @FXML private TableColumn<Vehicle, String> tc_model;
    @FXML private TableColumn<Vehicle, Integer> tc_year;
    @FXML private TableColumn<Vehicle, Integer> tc_drivingLicence;
    @FXML private TableColumn<Vehicle, String> tc_type;
    @FXML private TableColumn<Vehicle, Integer> tc_power;
    @FXML private TableColumn<Vehicle, Double> tc_price;
    @FXML private Button button_delete;
    @FXML private Button button_deleteFilter;

    Alert alert;

    List<Vehicle> filterVehicle;

    public VehicleTableViewController(RentService rentService) {
        this.rentService = rentService;
    }
    @FXML
    private void initialize(){
        // Tab Vehicle
        tv_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tv_table.setRowFactory(tv -> {
            TableRow<Vehicle> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!tableRow.isEmpty())){
                    DetailVehicleViewController detailVehicleViewController = new DetailVehicleViewController(rentService, tableRow.getItem());
                    FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/DetailVehicleView.fxml"));
                    addVehicleLoader.setControllerFactory(param -> param.isInstance(detailVehicleViewController) ? detailVehicleViewController : null);
                    Stage appStage = new Stage();
                    appStage.initModality(Modality.APPLICATION_MODAL);
                    appStage.setTitle("Detail View");
                    try {
                        appStage.setScene(new Scene(addVehicleLoader.load()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appStage.showAndWait();
                    initialize();
                }
            });
             return tableRow;
        });

        tc_model.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("label"));
        tc_year.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("year"));
        tc_drivingLicence.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("drivingLicence"));
        tc_type.setCellValueFactory(new PropertyValueFactory<Vehicle, String>("type"));
        tc_power.setCellValueFactory(new PropertyValueFactory<Vehicle, Integer>("power"));
        tc_price.setCellValueFactory(new PropertyValueFactory<Vehicle, Double>("price"));
        if (filterVehicle == null) {
            try {
                tv_table.setItems(FXCollections.observableArrayList(rentService.getList()));
                button_deleteFilter.setVisible(false);
            } catch (ServiceException e) {
                e.printStackTrace();
                setAlert(e.getMessage());
            }
        }else {
            tv_table.setItems(FXCollections.observableArrayList(filterVehicle));
            button_deleteFilter.setVisible(true);
        }

        //*******************************************************************************************

        //Tab Reservations

        tv_table1.setRowFactory(tv -> {
            TableRow<Reservation> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!tableRow.isEmpty())){
                    LOG.debug("Reservation {}", tableRow.getItem());
                    DetailReservationViewController detailReservationViewController = new DetailReservationViewController(rentService, tableRow.getItem());
                    FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/DetailReservationView.fxml"));
                    addVehicleLoader.setControllerFactory(param -> param.isInstance(detailReservationViewController) ? detailReservationViewController : null);
                    Stage appStage = new Stage();
                    appStage.initModality(Modality.APPLICATION_MODAL);
                    appStage.setTitle("Detail View");
                    try {
                        appStage.setScene(new Scene(addVehicleLoader.load()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    appStage.showAndWait();
                    initialize();
                }
            });
            return tableRow;
        });

        tc_name.setCellValueFactory(new PropertyValueFactory<Reservation, String>("name"));
        tc_IBAN.setCellValueFactory(new PropertyValueFactory<Reservation, String>("bankOrCreditCard"));
        tc_From.setCellValueFactory(new PropertyValueFactory<Reservation, LocalDateTime>("dateFrom"));
        tc_To.setCellValueFactory(new PropertyValueFactory<Reservation, LocalDateTime>("dateTo"));
        tc_Sum.setCellValueFactory(new PropertyValueFactory<Reservation, Double>("totalPrice"));
        tc_Status.setCellValueFactory(new PropertyValueFactory<Reservation, String>("status"));
        try {
            tv_table1.setItems(FXCollections.observableArrayList(rentService.getReservations(button_all.isDisable())));
        } catch (ServiceException e) {
            e.printStackTrace();
            setAlert(e.getMessage());
        }

    }

    //Tab Vehicle

    public void onDelete(ActionEvent actionEvent){
        LOG.info("Delete button clicked");
        ObservableList<Vehicle> deleteList = tv_table.getSelectionModel().getSelectedItems();
        if (!deleteList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm deletion");
            alert.setHeaderText("Do you really want to delete this vehicle(s)");
            alert.setContentText("It will not be possible to retrieve it");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                LOG.info("Deleting confirmed");
                for (Vehicle vehicle :
                    deleteList) {
                    try {
                        rentService.deleteVehicle(vehicle);
                    } catch (ServiceException e) {
                        e.printStackTrace();
                        setAlert(e.getMessage());
                    }
                }
                initialize();
            } else {
                LOG.info("Deleting aborted");
            }
        }
    }


    public void onAddNewVehicle(ActionEvent actionEvent) throws IOException {
        LOG.info("Add new vehicle button clicked");

        AddVehicleController addVehicleController = new AddVehicleController(rentService);

        FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/AddVehicle.fxml"));
        addVehicleLoader.setControllerFactory(param -> param.isInstance(addVehicleController) ? addVehicleController : null);
        Stage appStage = new Stage(); //(Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.initModality(Modality.APPLICATION_MODAL);
        appStage.setTitle("Add new Vehicle");
        appStage.setScene(new Scene(addVehicleLoader.load()));
        appStage.showAndWait();
        initialize();
    }

    public void onCreateFilter(ActionEvent actionEvent) throws IOException {
        LOG.info("Create filter button clicked");

        SearchFilterController searchFilterController = new SearchFilterController(rentService);

        FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/SearchFilter.fxml"));
        addVehicleLoader.setControllerFactory(param -> param.isInstance(searchFilterController) ? searchFilterController : null);
        Stage appStage = new Stage();
        appStage.initModality(Modality.APPLICATION_MODAL);
        appStage.setTitle("Create filter");
        appStage.setScene(new Scene(addVehicleLoader.load()));
        appStage.showAndWait();
        filterVehicle = searchFilterController.getFilterVehicle();
        initialize();
    }

    public void onDeleteFilter(ActionEvent actionEvent){
        LOG.info("Delete filter button clicked");
        filterVehicle = null;
        initialize();
    }

    public void onAddToReservation(ActionEvent actionEvent) throws IOException {
        LOG.info("Add to reservation button clicked");
        ObservableList<Vehicle> addList = tv_table.getSelectionModel().getSelectedItems();
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

    public void onCloseButton(ActionEvent actionEvent) {
        LOG.info("Close button clicked");
        Platform.exit();
    }

    //***********************************************************************************

    //Tab reservations

    @FXML private TableView<Reservation> tv_table1;
    @FXML private TableColumn<Reservation, String> tc_name;
    @FXML private TableColumn<Reservation, String> tc_IBAN;
    @FXML private TableColumn<Reservation, LocalDateTime> tc_From;
    @FXML private TableColumn<Reservation, LocalDateTime> tc_To;
    @FXML private TableColumn<Reservation, Double> tc_Sum;
    @FXML private TableColumn<Reservation, String> tc_Status;
    @FXML private Button button_close2;
    @FXML private Button button_bills;
    @FXML private Button button_all;

    @FXML
    private void onAllReservations(ActionEvent actionEvent){
        LOG.info("All reservations button clicked");
        if (!button_all.isDisable()){
            button_all.setDisable(true);
            button_bills.setDisable(false);
            initialize();
        }
    }

    @FXML
    private void onBills(ActionEvent actionEvent){
        LOG.info("Bills button clicked");
        if (!button_bills.isDisable()){
            button_bills.setDisable(true);
            button_all.setDisable(false);
            initialize();
        }
    }

    @FXML
    private void onAddReservation(ActionEvent actionEvent) throws IOException {
        LOG.info("Add new reservation button clicked");

        AddReservationController addReservationController = new AddReservationController(rentService);

        FXMLLoader addVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/AddReservation.fxml"));
        addVehicleLoader.setControllerFactory(param -> param.isInstance(addReservationController) ? addReservationController : null);
        Stage appStage = new Stage();
        appStage.initModality(Modality.APPLICATION_MODAL);
        appStage.setTitle("Add new Reservation");
        appStage.setScene(new Scene(addVehicleLoader.load()));
        appStage.showAndWait();
        initialize();
    }

    public void onShowStatistics(ActionEvent actionEvent) throws IOException {
        LOG.info("Show statistics button clicked");

        StatisticsController statisticsController = new StatisticsController(rentService);

        FXMLLoader showStatistics = new FXMLLoader(getClass().getResource("/fxml/Statistics.fxml"));
        showStatistics.setControllerFactory(param -> param.isInstance(statisticsController) ? statisticsController : null);
        Stage appStage = new Stage();
        appStage.initModality(Modality.APPLICATION_MODAL);
        appStage.setTitle("Statistics");
        appStage.setScene(new Scene(showStatistics.load()));
        appStage.showAndWait();
    }

    private void setAlert(String message){
        LOG.debug("Exception occurred {}", message);
        alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Data Access Object Failure");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
