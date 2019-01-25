package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Reservation {

    private Integer id;
    private String name;
    private String bankOrCreditCard;
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;
    private List<Vehicle> bookedVehicles;
    private Double totalPrice;
    private LocalDate date;
    private LocalDateTime dateBill;
    private Integer numberBill;
    private String status;

    public Reservation() {
        this.date = LocalDate.now();
        this.status = "open";
    }

    public Reservation(String name, String bankOrCreditCard, LocalDateTime dateFrom, LocalDateTime dateTo, List<Vehicle> bookedVehicles, Double totalPrice) {
        this.name = name;
        this.bankOrCreditCard = bankOrCreditCard;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.bookedVehicles = bookedVehicles;
        this.date = LocalDate.now();
        this.status = "open";
        this.totalPrice = totalPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankOrCreditCard() {
        return bankOrCreditCard;
    }

    public void setBankOrCreditCard(String bankOrCreditCard) {
        this.bankOrCreditCard = bankOrCreditCard;
    }

    public LocalDateTime getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDateTime dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDateTime getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDateTime dateTo) {
        this.dateTo = dateTo;
    }

    public List<Vehicle> getBookedVehicles() {
        return bookedVehicles;
    }

    public void setBookedVehicles(List<Vehicle> bookedVehicles) {
        this.bookedVehicles = bookedVehicles;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getDateBill() {
        return dateBill;
    }

    public void setDateBill(LocalDateTime dateBill) {
        this.dateBill = dateBill;
    }

    public Integer getNumberBill() {
        return numberBill;
    }

    public void setNumberBill(Integer numberBill) {
        this.numberBill = numberBill;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", bankOrCreditCard='" + bankOrCreditCard + '\'' +
            ", dateFrom=" + dateFrom +
            ", dateTo=" + dateTo +
            ", totalPrice=" + totalPrice +
            ", date=" + date +
            ", dateBill=" + dateBill +
            ", numberBill=" + numberBill +
            ", status='" + status + '\'' +
            '}';
    }
}
