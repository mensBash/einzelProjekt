package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao.*;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleRentService implements RentService {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private VehicleDAO vehicleDAO = new VehicleDAOJDBC();

    @Override
    public void saveVehicle(Vehicle vehicle) throws InvalidInputException, ServiceException {
        validateInput(vehicle);
        LOG.info("Saving vehicle {}", vehicle);
        try {
            vehicleDAO.create(vehicle);
            LOG.info("All values checked");
            LOG.info("Vehicle saved {}", vehicle);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void updateVehicle(Vehicle vehicle) throws InvalidInputException, ServiceException {
        validateInput(vehicle);
        LOG.info("Updating vehicle {}", vehicle);
        try {
            vehicleDAO.update(vehicle);
            LOG.info("All values checked");
            LOG.info("Vehicle updated {}", vehicle);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void deleteVehicle(Vehicle vehicle) throws ServiceException {
        LOG.info("Deleting vehicle {}", vehicle);
        try {
            vehicleDAO.delete(vehicle);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Vehicle> getList() throws ServiceException {
        try {
            return vehicleDAO.populateTableVehicle();
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    private void validateInput(Vehicle vehicle) throws InvalidInputException, ServiceException {
        LOG.info("Checking for incorrect values");
        if(vehicle.getYear() == null || String.valueOf(vehicle.getYear()).length() != 4 || vehicle.getYear() <= 0 || vehicle.getYear() > 2018){
            throw new InvalidInputException("Invalid year value\n");
        }
        if (vehicle.getDrivingLicence().length() > 0 && vehicle.getRegistrationNr().trim().isEmpty()){
            throw new InvalidInputException("Licence number must be given\n");
        }
        if  (vehicle.getSeats() < 0){
            throw new InvalidInputException("Invalid number of seats\n");
        }
        if (vehicle.getPower() < 0){
            throw new InvalidInputException("Invalid power of vehicle\n");
        }
        if (vehicle.getType().equals("motorized") && (vehicle.getPower() == null || vehicle.getPower() == 0)){
            throw new InvalidInputException("Power must be given\n");
        }
        if (vehicle.getPrice() == null || vehicle.getPrice() <= 0.0){
            throw new InvalidInputException("Price must be given correctly\n");
        }

        if (vehicle.getPicture() != null){
            if(!vehicle.getPicture().isEmpty()) {
                File picture = new File(vehicle.getPicture());
                if (picture.exists()) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(picture);
                        if (((picture.length() / 625000) > 5) || bufferedImage.getHeight() < 500 || bufferedImage.getWidth() < 500) {
                            throw new InvalidInputException("Picture format is not correct\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new ServiceException(e.getMessage());
                    }
                } else {
                    throw new InvalidInputException("Picture does not exists\n");
                }
            }
        }
    }

    //*******************************************************************************

    private boolean licenceA;
    private boolean licenceB;
    private boolean licenceC;

    ReservationDAO reservationDAO = new ReservationDAOJDBC();
    BillDAO billDAO = new BillDAOJDBC();
    @Override
    public ArrayList<Vehicle> getVehicles(LocalDateTime dateFrom, LocalDateTime dateTo) throws InvalidInputException, ServiceException {
        if (dateFrom.isAfter(dateTo) || dateFrom.isBefore(LocalDateTime.now()) || dateTo.isBefore(LocalDateTime.now())){
            throw new InvalidInputException("Time interval must be set correctly");
        }else {
            try {
                return vehicleDAO.getVehicleList(dateFrom, dateTo);
            } catch (DAOException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }

    @Override
    public List<Reservation> getReservations(boolean status) throws ServiceException {
        LOG.info("Getting reservations");
        try {
            return reservationDAO.getReservations(status);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public List<Vehicle> getReservedVehicles(Integer id) throws ServiceException {
        LOG.info("Getting vehicles");
        try {
            return billDAO.getReservedVehicles(id);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void saveReservation(Reservation reservation) throws InvalidInputException, ServiceException {
        LOG.info("Saving reservation {}", reservation);
        validateInput(reservation);
        LOG.info("All values checked");
        try {
            reservationDAO.create(reservation);reservation.setId(reservationDAO.getId());
            for (Vehicle v :
                reservation.getBookedVehicles()) {
                billDAO.create(v, reservation);
            }
            LOG.info("Reservation saved {}", reservation);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    public List<Vehicle> filterVehicle(String query) throws ServiceException {
        LOG.info("Filtering vehicles");
        try {
            return vehicleDAO.filterVehicle(query);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void updateReservation(Reservation reservation) throws InvalidInputException, ServiceException {
        LOG.info("Updating reservation {}", reservation);
        validateInput(reservation);
        LOG.info("All values checked");
        try {
            billDAO.deleteVehicles(reservation.getId());
            for (Vehicle v :
                reservation.getBookedVehicles()) {
                billDAO.create(v, reservation);
            }
            reservationDAO.updateReservation(reservation);
            LOG.info("Reservation saved {}", reservation);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }

    }

    @Override
    public void payReservation(Reservation reservation) throws ServiceException {
        LOG.info("Paying reservation");
        Integer integer = null;
        try {
            integer = reservationDAO.getBillId();
            if (integer == null){
                reservation.setNumberBill(1);
            }else{
                reservation.setNumberBill(integer + 1);
            }
            reservation.setStatus("paid");
            reservation.setDateBill(LocalDateTime.now());
            reservationDAO.payReservation(reservation);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

    @Override
    public void cancelReservation(Reservation reservation) throws ServiceException {
        LOG.info("Canceling reservation");
        if (ChronoUnit.DAYS.between(LocalDateTime.now(), reservation.getDateFrom()) >= 7){
            try {
                reservationDAO.delete(reservation);
                billDAO.deleteVehicles(reservation.getId());
            } catch (DAOException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }else if (ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getDateFrom()) >= 72){
            Double price = reservation.getTotalPrice() * 0.40;
            reservation.setTotalPrice(price);
            Integer integer = null;
            try {
                integer = reservationDAO.getBillId();
                if (integer == null){
                    reservation.setNumberBill(1);
                }else{
                    reservation.setNumberBill(integer + 1);
                }
                reservation.setStatus("canceled");
                reservation.setDateBill(LocalDateTime.now());
                reservationDAO.cancelReservation(reservation);
            } catch (DAOException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }else if (ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getDateFrom()) >= 24){
            Double price = reservation.getTotalPrice() * 0.75;
            reservation.setTotalPrice(price);
            Integer integer = null;
            try {
                integer = reservationDAO.getBillId();
                if (integer == null){
                    reservation.setNumberBill(1);
                }else{
                    reservation.setNumberBill(integer + 1);
                }
                reservation.setStatus("canceled");
                reservation.setDateBill(LocalDateTime.now());
                reservationDAO.cancelReservation(reservation);
            } catch (DAOException e) {
                e.printStackTrace();
                throw new ServiceException(e.getMessage());
            }
        }
    }
    @Override
    public Double parseInputs(String input) throws InvalidInputException {
        try {
            return Double.parseDouble(input);
        }catch (Exception e){
            throw new InvalidInputException("Invalid number\n");
        }
    }

    @Override
    public void checkDrivingLicence(List<Vehicle> vehicles, LocalDate licenceDate, String licenceNr) throws InvalidInputException {
        if (licenceDate != null){
            if (licenceDate.isAfter(LocalDate.now())) {
                throw new InvalidInputException("Driving licence date of issue is incorrect");
            }
        }
        for (Vehicle v :
            vehicles) {
            if (v.getDrivingLicence().length() > 0) {
                selectDrivingLicence(v);
                    if ((licenceA || licenceB || licenceC) && (licenceNr.isEmpty() || licenceDate == null)) {
                            throw new InvalidInputException("To choose vehicles that require driving licence, licence number and date of issue needs to be filled");

                    } else if ((licenceA || licenceC) && licenceDate != null) {
                        if (ChronoUnit.YEARS.between(licenceDate, LocalDate.now()) < 3) {
                            throw new InvalidInputException("To book vehicle " + v.getLabel() + " " + v.getYear() + " you need to have driving licence for at least 3 years");
                        }
                    }
                }

        }
    }

    private void validateInput(Reservation reservation) throws InvalidInputException{
        LOG.info("Checking for incorrect values");
        if (!validateLetters(reservation.getName())){
            throw new InvalidInputException("Invalid name of customer\n");
        }
        IBANCheckDigit ibanCheckDigit = new IBANCheckDigit();
        CreditCardValidator creditCardValidator = new CreditCardValidator();

        if(!ibanCheckDigit.isValid(reservation.getBankOrCreditCard()) && !creditCardValidator.isValid(reservation.getBankOrCreditCard())){
            throw new InvalidInputException("Invalid IBAN/Credit card numer");
        }
    }



    private void selectDrivingLicence(Vehicle vehicle){
        licenceA = false;
        licenceB = false;
        licenceC = false;
        int i;
        for (i = 0; i < vehicle.getDrivingLicence().length(); i++){
            if(vehicle.getDrivingLicence().charAt(i) == 'A'){
                licenceA = true;
            }
            if (vehicle.getDrivingLicence().charAt(i) == 'B'){
                licenceB = true;
            }
            if (vehicle.getDrivingLicence().charAt(i) == 'C'){
                licenceC = true;
            }
        }
    }

    private static boolean validateLetters(String txt) {
        String regx = "^[a-zA-Z -]*$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();
    }

    /************************************************************************************/
    //Statistics

    @Override
    public List<Vehicle> vehicleStatistics(LocalDateTime dateFrom, LocalDateTime dateTo) throws ServiceException {
        try {
            return vehicleDAO.getVehiclesStatistics(dateFrom, dateTo);
        } catch (DAOException e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
    }

}
