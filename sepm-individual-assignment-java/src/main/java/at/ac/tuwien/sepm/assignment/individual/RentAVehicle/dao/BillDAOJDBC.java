package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Reservation;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDAOJDBC implements BillDAO {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private String billInsert = "INSERT INTO Bill (vehicleId, reservationId, model, year, description, nrOfSeats, registrationNr, type, power, price, picture, drivingLicence, licenceNr, licenceDate) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?)";
    private String getVehicles = "SELECT * FROM Bill WHERE reservationId = ?";
    private String deleteVehicles = "DELETE FROM Bill WHERE reservationId = ?";

    @Override
    public void create(Vehicle vehicle, Reservation reservation) throws DAOException {
        LOG.info("create a Reservation: {}", reservation);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(billInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, vehicle.getId());
            preparedStatement.setInt(2, reservation.getId());
            preparedStatement.setString(3, vehicle.getLabel());
            preparedStatement.setInt(4, vehicle.getYear());
            preparedStatement.setString(5, vehicle.getDescription());
            preparedStatement.setInt(6, vehicle.getSeats());
            preparedStatement.setString(7, vehicle.getRegistrationNr());
            preparedStatement.setString(8, vehicle.getType());
            preparedStatement.setInt(9, vehicle.getPower());
            preparedStatement.setDouble(10, vehicle.getPrice());
            preparedStatement.setString(11, vehicle.getPicture());
            preparedStatement.setString(12, vehicle.getDrivingLicence());
            preparedStatement.setString(13, vehicle.getLicenceNr());
            preparedStatement.setDate(14, vehicle.getLicenceDate() == null ? null : Date.valueOf(vehicle.getLicenceDate()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public List<Vehicle> getReservedVehicles(Integer id) throws DAOException {
        LOG.info("Getting reserved list of vehicles");
        List<Vehicle> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(getVehicles, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                list.add(resultSetVehicle(resultSet));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return list;
    }

    private Vehicle resultSetVehicle(ResultSet resultSet) throws DAOException {
        Vehicle vehicle = new Vehicle();
        try {
            vehicle.setId(resultSet.getInt("vehicleId"));
            vehicle.setLabel(resultSet.getString("model"));
            vehicle.setYear(resultSet.getInt("year"));
            vehicle.setDescription(resultSet.getString("description"));
            vehicle.setSeats(resultSet.getInt("nrOfSeats"));
            vehicle.setRegistrationNr(resultSet.getString("registrationNr"));
            vehicle.setType(resultSet.getString("type"));
            vehicle.setPower(resultSet.getInt("power"));
            vehicle.setPrice(resultSet.getDouble("price"));
            vehicle.setPicture(resultSet.getString("picture"));
            vehicle.setDrivingLicence(resultSet.getString("drivingLicence"));
            vehicle.setLicenceNr(resultSet.getString("licenceNr"));
            vehicle.setLicenceDate(resultSet.getDate("licenceDate") == null ? null : resultSet.getDate("licenceDate").toLocalDate());

        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return vehicle;
    }

    @Override
    public void deleteVehicles(Integer id) throws DAOException {
        LOG.info("Delete vehicles from table Bill");
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(deleteVehicles, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }



}
