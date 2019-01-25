package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface VehicleDAO {
    /**
     * Creating a vehicle
     * @param vehicle that is about to be created
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void create(Vehicle vehicle) throws DAOException;

    /**
     * Updating a vehicle
     * @param vehicle that is about to be updated
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void update(Vehicle vehicle) throws DAOException;

    /**
     * Deleting a vehicle
     * @param vehicle that is about to be deleted
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void delete(Vehicle vehicle) throws DAOException;

    /**
     * Getting list of available vehicles
     * @return list of all non deleted vehicles
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    List<Vehicle> populateTableVehicle() throws DAOException;

    /**
     * Getting list of vehicles available in certain time period
     * @param dateFrom - start date, from which vehicle should be available
     * @param dateTo - end date, until which vehicle should be available
     * @return list of available vehicles from dateFrom to dateTo
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    ArrayList<Vehicle> getVehicleList(LocalDateTime dateFrom, LocalDateTime dateTo) throws DAOException;

    /**
     *  Getting list of vehicles filtered by query
     * @param query for prepared statement, built according to filter
     * @return list of vehicles that are filtered according to conditions in query
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    List<Vehicle> filterVehicle(String query) throws DAOException;

    /**
     *  Getting reserved vehicles for a single day (it is normally 24 hours between @dateFrom and @dateTo)
     * @param dateFrom date and time of starting day
     * @param dateTo date and time of end day
     * @return list of reserved vehicles with parameters that are needed for statistics
     * @throws DAOException
     */
    List<Vehicle> getVehiclesStatistics(LocalDateTime dateFrom, LocalDateTime dateTo) throws DAOException;
}
