package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class Vehicle {
    private Integer id;
    private String label;
    private Integer year;
    private String description;
    private Integer seats;
    private String registrationNr;
    private String type;
    private Integer power;
    private Double price;
    private LocalDate date;
    private String picture;
    private String drivingLicence;
    private LocalDate dateChanged;

    private String licenceNr;
    private LocalDate licenceDate;

    private LocalDateTime statisticsDateFrom;
    private LocalDateTime statisticsDateTo;

    public Vehicle(){
        this.date = LocalDate.now();
        this.dateChanged = LocalDate.now();
    }

    public Vehicle(String label, Integer year, String description, Integer seats, String registrationNr, String type, Integer power, Double price, String picture, String drivingLicence) {
        this.label = label;
        this.year = year;
        this.description = description;
        this.seats = seats;
        this.registrationNr = registrationNr;
        this.type = type;
        this.power = power;
        this.price = price;
        this.date = LocalDate.now();
        this.dateChanged = LocalDate.now();
        this.picture = picture;
        this.drivingLicence = drivingLicence;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getRegistrationNr() {
        return registrationNr;
    }

    public void setRegistrationNr(String registrationNr) {
        this.registrationNr = registrationNr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDrivingLicence() {
        return drivingLicence;
    }

    public void setDrivingLicence(String drivingLicence) {
        this.drivingLicence = drivingLicence;
    }

    public LocalDate getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(LocalDate dateChanged) {
        this.dateChanged = dateChanged;
    }

    public String getLicenceNr() {
        return licenceNr;
    }

    public void setLicenceNr(String licenceNr) {
        this.licenceNr = licenceNr;
    }

    public LocalDate getLicenceDate() {
        return licenceDate;
    }

    public void setLicenceDate(LocalDate licenceDate) {
        this.licenceDate = licenceDate;
    }

    //******************************************************************************
    //for statistics

    public LocalDateTime getStatisticsDateFrom() {
        return statisticsDateFrom;
    }

    public void setStatisticsDateFrom(LocalDateTime statisticsDateFrom) {
        this.statisticsDateFrom = statisticsDateFrom;
    }

    public LocalDateTime getStatisticsDateTo() {
        return statisticsDateTo;
    }

    public void setStatisticsDateTo(LocalDateTime statisticsDateTo) {
        this.statisticsDateTo = statisticsDateTo;
    }
    /*************************************************************************/

    @Override
    public String toString() {
        return "Vehicle{" +
            "id=" + id +
            ", label='" + label + '\'' +
            ", year=" + year +
            ", description='" + description + '\'' +
            ", seats=" + seats +
            ", registrationNr='" + registrationNr + '\'' +
            ", type='" + type + '\'' +
            ", power=" + power +
            ", price=" + price +
            ", date=" + date +
            ", picture='" + picture + '\'' +
            ", drivingLicence='" + drivingLicence + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
