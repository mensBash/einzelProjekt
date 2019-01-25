package at.ac.tuwien.sepm.assignment.individual.application;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.SimpleRentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui.AddVehicleController;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui.VehicleTableViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public final class MainApplication extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void start(Stage primaryStage) throws Exception {
        // setup application
        primaryStage.setTitle("Rent-a-Vehicle");
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(event -> LOG.debug("Application shutdown initiated"));

        // initiate service and controller
        RentService rentService = new SimpleRentService();

        VehicleTableViewController vehicleTableViewController = new VehicleTableViewController(rentService);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/VehicleTableView.fxml"));
        fxmlLoader.setControllerFactory(param -> param.isInstance(vehicleTableViewController) ? vehicleTableViewController : null);
        primaryStage.setScene(new Scene(fxmlLoader.load()));

        // show application
        primaryStage.show();
        primaryStage.toFront();
        LOG.debug("Application startup complete");
    }

    public static void main(String[] args) {
        LOG.debug("Application starting with arguments={}", (Object) args);
        Application.launch(MainApplication.class, args);
    }

}
