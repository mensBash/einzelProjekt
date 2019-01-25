package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

public interface BillDAO {
    /**
     *  Put vehicle data and reservation id in inner table
     * @param vehicle that is about to be created
     * @param reservation - using id of reservation to relate with vehicle
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void create(Vehicle vehicle, Reservation reservation) throws DAOException;

    /**
     *  Get list of vehicles related to reservation
     * @param id reservation id
     * @return list of vehicles that are reserved by reservation with id
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    List<Vehicle> getReservedVehicles(Integer id) throws DAOException;

    /**
     *  Delete vehicles related to reservation
     * @param id of reservation, which related vehicles are about to be deleted
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void deleteVehicles(Integer id) throws DAOException;
}
