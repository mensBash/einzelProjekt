package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.ui;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.RentService;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service.ServiceException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StatisticsController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RentService rentService;

    @FXML private DatePicker dp_dateFrom;
    @FXML private DatePicker dp_dateTo;
    @FXML private CheckBox cb_none;
    @FXML private CheckBox cb_licenceA;
    @FXML private CheckBox cb_licenceB;
    @FXML private CheckBox cb_licenceC;
    @FXML private Button button_line;
    @FXML private Button button_bar;
    @FXML private LineChart<?, ?> lineChart;
    @FXML private CategoryAxis line_Xaxis;
    @FXML private NumberAxis line_Yaxis;
    @FXML private BarChart<?, ?> barChart;
    @FXML private CategoryAxis bar_Xaxis;
    @FXML private NumberAxis bar_Yaxis;


    private Alert alert;

    public StatisticsController(RentService rentService) {
        this.rentService = rentService;
    }

    @FXML
    private void initialize(){
    }

    @FXML
    void onGenerateBar(ActionEvent event) {
        LOG.info("Generate Bar Chart button clicked");
        if (!check()){
            int[] licenceNone = new int[7];
            int[] licenceA = new int[7];
            int[] licenceB = new int[7];
            int[] licenceC = new int[7];
            barChart.getData().clear();
            lineChart.setVisible(false);
            barChart.setVisible(true);
            LocalDate dateTo = dp_dateTo.getValue();
            for (LocalDate dateFrom = dp_dateFrom.getValue(); dateFrom.isBefore(dateTo); dateFrom = dateFrom.plusDays(1)) {
                List<Vehicle> vehicleList = null;
                try {
                    vehicleList = rentService.vehicleStatistics(LocalDateTime.of(dateFrom, LocalTime.of(0,0)), LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0,0)));
                    for (Vehicle v:vehicleList){
                        if (v.getDrivingLicence().length() == 0 || v.getDrivingLicence() == null){
                            daysOfWeek(dateFrom, licenceNone);
                        }

                        if (v.getDrivingLicence().contains("A")){
                            daysOfWeek(dateFrom, licenceA);
                        }

                        if (v.getDrivingLicence().contains("B")){
                            daysOfWeek(dateFrom, licenceB);
                        }

                        if (v.getDrivingLicence().contains("C")){
                            daysOfWeek(dateFrom, licenceC);
                        }
                    }
                } catch (ServiceException e) {
                    e.printStackTrace();
                    setAlert(e.getMessage());
                }
            }
            bar_Xaxis.setCategories(FXCollections.observableArrayList("Monday", "Tuesday", "Wednesday","Thursday", "Friday", "Saturday", "Sunday"));
            if (cb_none.isSelected()) {
                XYChart.Series none = setSeries(licenceNone);
                none.setName("No licence");
                barChart.getData().add(none);
            }
            if (cb_licenceA.isSelected()){
                XYChart.Series a = setSeries(licenceA);
                a.setName("Licence A");
                barChart.getData().add(a);
            }
            if (cb_licenceB.isSelected()){
                XYChart.Series b = setSeries(licenceB);
                b.setName("Licence B");
                barChart.getData().add(b);
            }

            if (cb_licenceC.isSelected()){
                XYChart.Series c = setSeries(licenceC);
                c.setName("Licence C");
                barChart.getData().add(c);
            }
        }

    }

    @FXML
    void onGenerateLine(ActionEvent event) {
        LOG.info("Generate Line Chart button clicked");
        if (!check()){
            barChart.setVisible(false);
            lineChart.setVisible(true);
            lineChart.getData().clear();
            double[] daysNone = new double[(int)ChronoUnit.DAYS.between(dp_dateFrom.getValue(), dp_dateTo.getValue())];
            double[] daysA = new double[(int)ChronoUnit.DAYS.between(dp_dateFrom.getValue(), dp_dateTo.getValue())];
            double[] daysB = new double[(int)ChronoUnit.DAYS.between(dp_dateFrom.getValue(), dp_dateTo.getValue())];
            double[] daysC = new double[(int)ChronoUnit.DAYS.between(dp_dateFrom.getValue(), dp_dateTo.getValue())];
            List<String> localDateList = new ArrayList<>();
            LocalDate dateTo = dp_dateTo.getValue();
            int i = 0;
            for (LocalDate dateFrom = dp_dateFrom.getValue(); dateFrom.isBefore(dateTo); dateFrom = dateFrom.plusDays(1)) {
                List<Vehicle> vehicleList = null;
                try {
                    vehicleList = rentService.vehicleStatistics(LocalDateTime.of(dateFrom, LocalTime.of(0, 0)), LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0)));
                    for (Vehicle v: vehicleList){
                        if (v.getDrivingLicence().length() == 0 || v.getDrivingLicence() == null){
                            daysNone[i] += v.getPrice()*ChronoUnit.HOURS.between((LocalDateTime.of(dateFrom, LocalTime.of(0, 0)).isBefore(v.getStatisticsDateFrom()) ? v.getStatisticsDateFrom() : LocalDateTime.of(dateFrom, LocalTime.of(0, 0))), (LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0)).isAfter(v.getStatisticsDateTo()) ? v.getStatisticsDateTo() : LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0))));
                        }

                        if (v.getDrivingLicence().contains("A")){
                            daysA[i] += v.getPrice()*ChronoUnit.HOURS.between((LocalDateTime.of(dateFrom, LocalTime.of(0, 0)).isBefore(v.getStatisticsDateFrom()) ? v.getStatisticsDateFrom() : LocalDateTime.of(dateFrom, LocalTime.of(0, 0))), (LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0)).isAfter(v.getStatisticsDateTo()) ? v.getStatisticsDateTo() : LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0))));
                        }

                        if (v.getDrivingLicence().contains("B")){
                            daysB[i] += v.getPrice()*ChronoUnit.HOURS.between((LocalDateTime.of(dateFrom, LocalTime.of(0, 0)).isBefore(v.getStatisticsDateFrom()) ? v.getStatisticsDateFrom() : LocalDateTime.of(dateFrom, LocalTime.of(0, 0))), (LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0)).isAfter(v.getStatisticsDateTo()) ? v.getStatisticsDateTo() : LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0))));
                        }

                        if (v.getDrivingLicence().contains("C")){
                            daysC[i] += v.getPrice()*ChronoUnit.HOURS.between((LocalDateTime.of(dateFrom, LocalTime.of(0, 0)).isBefore(v.getStatisticsDateFrom()) ? v.getStatisticsDateFrom() : LocalDateTime.of(dateFrom, LocalTime.of(0, 0))), (LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0)).isAfter(v.getStatisticsDateTo()) ? v.getStatisticsDateTo() : LocalDateTime.of(dateFrom.plusDays(1), LocalTime.of(0, 0))));
                        }
                    }
                    localDateList.add(dateFrom.toString());
                    i++;
                } catch (ServiceException e) {
                    e.printStackTrace();
                    setAlert(e.getMessage());
                }

            }
            line_Xaxis.setCategories(FXCollections.observableArrayList(localDateList));
            if (cb_none.isSelected()){
                XYChart.Series none = setSeries(localDateList, daysNone);
                none.setName("No licence");
                lineChart.getData().add(none);
            }
            if (cb_licenceA.isSelected()){
                XYChart.Series a = setSeries(localDateList, daysA);
                a.setName("Licence A");
                lineChart.getData().add(a);
            }
            if (cb_licenceB.isSelected()){
                XYChart.Series b = setSeries(localDateList, daysB);
                b.setName("Licence B");
                lineChart.getData().add(b);
            }
            if (cb_licenceC.isSelected()){
                XYChart.Series c = setSeries(localDateList, daysC);
                c.setName("Licence C");
                lineChart.getData().add(c);
            }
        }

    }

    private void daysOfWeek(LocalDate dateFrom, int[] array){
        switch (dateFrom.getDayOfWeek()){
            case MONDAY:
                array[0]++;
                break;
            case TUESDAY:
                array[1]++;
                break;
            case WEDNESDAY:
                array[2]++;
                break;
            case THURSDAY:
                array[3]++;
                break;
            case FRIDAY:
                array[4]++;
                break;
            case SATURDAY:
                array[5]++;
                break;
            case SUNDAY:
                array[6]++;
                break;
        }
    }

    private XYChart.Series setSeries(int[] array){
        XYChart.Series none = new XYChart.Series();
        none.getData().add(new XYChart.Data("Monday", array[0]));
        none.getData().add(new XYChart.Data("Tuesday", array[1]));
        none.getData().add(new XYChart.Data("Wednesday", array[2]));
        none.getData().add(new XYChart.Data("Thursday", array[3]));
        none.getData().add(new XYChart.Data("Friday", array[4]));
        none.getData().add(new XYChart.Data("Saturday", array[5]));
        none.getData().add(new XYChart.Data("Sunday", array[6]));
        return none;
    }

    private XYChart.Series setSeries(List<String> dates, double[] array){
        XYChart.Series none = new XYChart.Series();
        int i = 0;
        for (String s : dates){
            none.getData().add(new XYChart.Data(s, array[i]));
            i++;
        }
        return none;
    }

    private boolean check(){
        boolean check = false;
        String warning = "";
        if (dp_dateFrom.getValue() == null){
            warning += "Date from is empty\n";
            check = true;
        }
        if (dp_dateTo.getValue() == null){
            warning += "Date to is empty\n";
            check = true;
        }
        if (!cb_none.isSelected() && !cb_licenceA.isSelected() && !cb_licenceB.isSelected() && !cb_licenceC.isSelected()){
            warning += "At least one driving licence choice needs to be chosen\n";
            check = true;
        }
        if (!check) {
            if (dp_dateFrom.getValue().isAfter(dp_dateTo.getValue())) {
                warning += "Time interval must be set properly\n";
                check = true;
            }
        }
        if (check){
            LOG.debug("Exception occurred");
            alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Empty fields");
            alert.setContentText(warning);
            alert.showAndWait();
        }
        return check;
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
