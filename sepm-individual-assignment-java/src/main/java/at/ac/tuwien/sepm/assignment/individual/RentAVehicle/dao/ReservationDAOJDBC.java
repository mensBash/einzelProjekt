package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOJDBC implements ReservationDAO {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private String reservationInsert = "INSERT INTO Reservation (name, cardNr, dateFrom, dateTo, sum, date, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private String reservationUpdate = "UPDATE Reservation SET name = ?, cardNr = ?, dateFrom = ?, dateTo = ?, sum = ?, date = ?, status = ? WHERE id = ?";
    private String payReservation = "UPDATE Reservation SET dateBill = ?, billNr = ?, status = ? WHERE id = ?";
    private String cancelReservation = "UPDATE Reservation SET sum = ?, dateBill = ?, billNr = ?, status = ? WHERE id = ?";
    private String reservationDelete = "DELETE FROM Reservation WHERE id = ?";

    @Override
    public void create(Reservation reservation) throws DAOException {
        LOG.info("create a Reservation: {}", reservation);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(reservationInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, reservation.getName());
            preparedStatement.setString(2, reservation.getBankOrCreditCard());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reservation.getDateFrom()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(reservation.getDateTo()));
            preparedStatement.setDouble(5, reservation.getTotalPrice());
            preparedStatement.setDate(6, Date.valueOf(reservation.getDate()));
            preparedStatement.setString(7, reservation.getStatus());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public Integer getId() throws DAOException {
        LOG.info("Getting id of reservation");
        try {
            Statement statement = DBUtil.getConnection().createStatement();
            String query = "SELECT MAX(id) FROM Reservation ";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return 0;
    }

    @Override
    public List<Reservation> getReservations(boolean status) throws DAOException {
        List<Reservation> reservationList = new ArrayList<>();
        LOG.info("Populating table reservation");
        try {
            Statement statement = DBUtil.getConnection().createStatement();
            String query;
            if (status) {
                 query = "SELECT * FROM Reservation ORDER BY dateFrom ASC";
            }else {
                query = "SELECT * FROM Reservation WHERE status != 'open' ORDER BY dateFrom ASC";
            }
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                reservationList.add(resultSetReservation(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        LOG.info("Populating done");
        return reservationList;
    }

    @Override
    public void updateReservation(Reservation reservation) throws DAOException {
        LOG.info("update a reservation {}", reservation);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(reservationUpdate, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, reservation.getName());
            preparedStatement.setString(2, reservation.getBankOrCreditCard());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reservation.getDateFrom()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(reservation.getDateTo()));
            preparedStatement.setDouble(5, reservation.getTotalPrice());
            preparedStatement.setDate(6, Date.valueOf(reservation.getDate()));
            preparedStatement.setString(7, reservation.getStatus());
            preparedStatement.setInt(8, reservation.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public Integer getBillId() throws DAOException {
        LOG.info("Getting id of bill");
        try {
            Statement statement = DBUtil.getConnection().createStatement();
            String query = "SELECT MAX(billNr) FROM Reservation ";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return 0;
    }

    @Override
    public void payReservation(Reservation reservation) throws DAOException {
        LOG.info("Pay or Cancel reservation {}", reservation);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(payReservation, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(reservation.getDateBill()));
            preparedStatement.setInt(2, reservation.getNumberBill());
            preparedStatement.setString(3, reservation.getStatus());
            preparedStatement.setInt(4, reservation.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void delete(Reservation reservation) throws DAOException {
        LOG.info("delete reservation {}", reservation);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(reservationDelete, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, reservation.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void cancelReservation(Reservation reservation) throws DAOException {
        LOG.info("Pay or Cancel reservation {}", reservation);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(cancelReservation, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setDouble(1, reservation.getTotalPrice());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(reservation.getDateBill()));
            preparedStatement.setInt(3, reservation.getNumberBill());
            preparedStatement.setString(4, reservation.getStatus());
            preparedStatement.setInt(5, reservation.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    private Reservation resultSetReservation(ResultSet resultSet) throws DAOException {
        Reservation reservation = new Reservation();
        try {
            reservation.setId(resultSet.getInt("id"));
            reservation.setName(resultSet.getString("name"));
            reservation.setBankOrCreditCard(resultSet.getString("cardNr"));
            reservation.setDateFrom(resultSet.getTimestamp("dateFrom").toLocalDateTime());
            reservation.setDateTo(resultSet.getTimestamp("dateTo").toLocalDateTime());
            reservation.setTotalPrice(resultSet.getDouble("sum"));
            reservation.setDate(resultSet.getDate("date").toLocalDate());
            reservation.setDateBill(resultSet.getTimestamp("dateBill") == null ? null : resultSet.getTimestamp("dateBill").toLocalDateTime());
            reservation.setNumberBill(resultSet.getInt("billNr"));
            reservation.setStatus(resultSet.getString("status"));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return reservation;
    }

}
