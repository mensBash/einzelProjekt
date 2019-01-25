package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.service;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The <code>RentService</code> is capable to calculate the answer to
 * <blockquote>Ultimate Question of Life, the Universe, and Everything</blockquote>.
 * Depending on the implementation it might take a while.
 */
public interface RentService {
    /**
     * Creates a vehicle
     * @param vehicle that is about to be created
     * @throws InvalidInputException if the input values of vehicle are not according to task
     * @throws ServiceException if DAO cannot create a vehicle
     */
    void saveVehicle(Vehicle vehicle) throws InvalidInputException, ServiceException;

    /**
     *  Updates a vehicle
     * @param vehicle that is about to be updated
     * @throws InvalidInputException if the input values of vehicle are not according to task
     * @throws ServiceException if DAO cannot update a vehicle
     */
    void updateVehicle(Vehicle vehicle) throws InvalidInputException, ServiceException;

    /**
     *  Deletes a vehicle
     * @param vehicle that is about to be deleted
     * @throws ServiceException if DAO cannot delete a vehicle
     */
    void deleteVehicle(Vehicle vehicle) throws ServiceException;

    /**
     *  Getting list of available vehicles
     * @return list of available vehicles
     * @throws ServiceException if DAO cannot make a list of available vehicles
     */
    List<Vehicle> getList() throws ServiceException;


    /**
     *  Getting vehicles that are available on certain time interval
     * @param dateFrom - date from which vehicles should be available
     * @param dateTo - date until vehicles should be available
     * @return list of vehicles that are available in time period from @dateFrom to @dateTo
     * @throws InvalidInputException if the input values are not according to task
     * @throws ServiceException if DAO cannot make list of available vehicles
     */
    ArrayList<Vehicle> getVehicles(LocalDateTime dateFrom, LocalDateTime dateTo) throws InvalidInputException, ServiceException;

    /**
     *  Getting list of reservations according to status
     * @param status switcher between opened and not opened status of reservation
     * @return list of reservation
     * @throws ServiceException if DAO cannot make list of reservations
     */
    List<Reservation> getReservations(boolean status) throws ServiceException;

    /**
     * Getting list of vehicles of one reservation
     * @param id of reservation
     * @return list of vehicles related to reservation @id
     * @throws ServiceException if DAO cannot make list of vehicles
     */
    List<Vehicle> getReservedVehicles(Integer id) throws ServiceException;

    /**
     *  Updates a reservation
     * @param reservation that is about to be updated
     * @throws InvalidInputException if the input values are not according to task
     * @throws ServiceException if DAO cannot update a reservation
     */
    void updateReservation(Reservation reservation) throws InvalidInputException, ServiceException;

    /**
     *  Pays a reservation (set status to paid)
     * @param reservation that is about to be paid
     * @throws ServiceException if DAO cannot change status of reservation
     */
    void payReservation(Reservation reservation) throws ServiceException;

    /**
     *  Cancels a reservation (set status to cancelled)
     * @param reservation that is about to be cancelled
     * @throws ServiceException if DAO cannot change status of reservation
     */
    void cancelReservation(Reservation reservation) throws ServiceException;

    /**
     *  Parse input to Double value
     * @param input that should be parsed
     * @return Double value of @input
     * @throws InvalidInputException if the input values are not correct(input can not be parsed to Double)
     */
    Double parseInputs(String input) throws InvalidInputException;

    /**
     *  Checks if all parameters for adding vehicles to reservation are correct
     * @param vehicles list of vehicles that should be added to reservation
     * @param licenceDate issue date of licence of vehicles that should be added to reservation
     * @param licenceNr licence number of vehicles that should be added to reservation
     * @throws InvalidInputException if the input values are not according to task
     */
    void checkDrivingLicence(List<Vehicle> vehicles, LocalDate licenceDate, String licenceNr) throws InvalidInputException;

    /**
     * Creates a reservation
     * @param reservation that is about to be created
     * @throws InvalidInputException if the input values of reservation are not according to task
     * @throws ServiceException if DAO cannot create a reservation
     */
    void saveReservation(Reservation reservation) throws InvalidInputException, ServiceException;

    /**
     *  Getting list of vehicles according to filter query
     * @param query that should return filtered list - for prepared statement
     * @return list of vehicles according to filter
     * @throws ServiceException if DAO cannot return list of vehicles according to filter
     */
    List<Vehicle> filterVehicle(String query) throws ServiceException;

    /**
     *  Getting list of vehicles for a single day (there are 24 hours between dateFrom and dateTo, usually)
     * @param dateFrom - start date for query
     * @param dateTo - end date for query
     * @return list of vehicles in time period of dateFrom to dateTo (a single day)
     * @throws ServiceException if DAO cannot return list of vehicles
     */
    List<Vehicle> vehicleStatistics(LocalDateTime dateFrom, LocalDateTime dateTo) throws ServiceException;

}
