package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;

public interface ReservationDAO {
    /**
     *  Creating a reservation
     * @param reservation with all parameters, that is about to be created
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void create(Reservation reservation) throws DAOException;

    /**
     *  Gives the id of the reservation that is last created
     * @return maximal id of table reservation
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    Integer getId() throws DAOException;

    /**
     *  Gives list of reservation depending on status
     * @param status switcher between reservations that are opened and the one that are not
     * @return  list of all available reservations by status chosen
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    List<Reservation> getReservations(boolean status) throws DAOException;

    /**
     *  Update reservation with new values from reservation
     * @param reservation with new values for update
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void updateReservation(Reservation reservation) throws DAOException;

    /**
     *  Getting bill id of reservations that are not opened
     * @return last bill number/id
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    Integer getBillId() throws DAOException;

    /**
     *  Set status of reservation on paid
     * @param reservation that is about to be paid
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void payReservation(Reservation reservation) throws DAOException;

    /**
     *  Set status of reservation on cancelled
     * @param reservation that is about to be cancelled
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void cancelReservation(Reservation reservation) throws DAOException;

    /**
     *  Delete reservation
     * @param reservation that is abput to be deleted
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
    void delete(Reservation reservation) throws DAOException;

    /**
     *
     * @param resultSet
     * @return
     * @throws DAOException occurs if parameters are null or because of invalid data in them
     */
}
