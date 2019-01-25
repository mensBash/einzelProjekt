package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.dao;

import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.entity.Vehicle;
import at.ac.tuwien.sepm.assignment.individual.RentAVehicle.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class VehicleDAOJDBC implements VehicleDAO {
    private String vehicleInsert = "INSERT INTO Vehicle (label, year, description, nrOfSeats, registrationNr, type, power, price, date, picture, drivingLicence, dateChanged, isDeleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private String vehicleUpdate = "UPDATE Vehicle SET label = ?, year = ?, description = ?, nrOfSeats = ?, registrationNr = ?, type = ?, power = ?, price = ?, date = ?, picture = ?, drivingLicence = ?, dateChanged = ? WHERE id = ?";
    private String vehicleDelete = "UPDATE Vehicle SET isDeleted = true WHERE id = ?";
    private String vehicleList = "SELECT * FROM Vehicle v WHERE id NOT IN(SELECT b.vehicleId FROM Bill b INNER JOIN Reservation r ON r.id = b.reservationId  WHERE r.dateFrom <= ? AND r.dateTo >= ? AND status = 'open') AND isDeleted = false";
    private String statisticsVehicles = "SELECT * FROM Bill b INNER JOIN Reservation r ON r.id = b.reservationId WHERE r.dateFrom <= ? AND r.dateTo >= ?";

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void create(Vehicle vehicle) throws DAOException {
        LOG.info("create a Vehicle: {}", vehicle);

        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(vehicleInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, vehicle.getLabel());
            preparedStatement.setInt(2, vehicle.getYear());
            preparedStatement.setString(3, vehicle.getDescription());
            preparedStatement.setInt(4, vehicle.getSeats());
            preparedStatement.setString(5, vehicle.getRegistrationNr());
            preparedStatement.setString(6, vehicle.getType());
            preparedStatement.setInt(7, vehicle.getPower());
            preparedStatement.setDouble(8, vehicle.getPrice());
            preparedStatement.setDate(9, Date.valueOf(vehicle.getDate()));
            preparedStatement.setString(10, savePicture(vehicle.getPicture()));
            preparedStatement.setString(11, vehicle.getDrivingLicence());
            preparedStatement.setDate(12, Date.valueOf(vehicle.getDateChanged()));
            preparedStatement.setBoolean(13,false);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void update(Vehicle vehicle) throws DAOException {
        LOG.info("update a Vehicle {}", vehicle);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(vehicleUpdate, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, vehicle.getLabel());
            preparedStatement.setInt(2, vehicle.getYear());
            preparedStatement.setString(3, vehicle.getDescription());
            preparedStatement.setInt(4, vehicle.getSeats());
            preparedStatement.setString(5, vehicle.getRegistrationNr());
            preparedStatement.setString(6, vehicle.getType());
            preparedStatement.setInt(7, vehicle.getPower());
            preparedStatement.setDouble(8, vehicle.getPrice());
            preparedStatement.setDate(9, Date.valueOf(vehicle.getDate()));
            preparedStatement.setString(10, savePicture(vehicle.getPicture()));
            preparedStatement.setString(11, vehicle.getDrivingLicence());
            preparedStatement.setDate(12, Date.valueOf(vehicle.getDateChanged()));
            preparedStatement.setInt(13, vehicle.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
    }

    @Override
    public void delete(Vehicle vehicle) throws DAOException {
        LOG.info("delete a Vehicle {}", vehicle);
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(vehicleDelete, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, vehicle.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }

    }

    @Override
    public ArrayList<Vehicle> populateTableVehicle() throws DAOException {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        LOG.info("Populating table vehicle");
         try {
             Statement statement = DBUtil.getConnection().createStatement();
             String query = "SELECT * FROM Vehicle WHERE isDeleted = false ORDER BY id ASC";
             ResultSet resultSet = statement.executeQuery(query);
             while (resultSet.next()){
                 vehicleList.add(resultSetVehicle(resultSet));
             }
             resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
             throw new DAOException(e.getMessage());
        }
        LOG.info("Populating done");
        return vehicleList;
    }

    @Override
    public ArrayList<Vehicle> getVehicleList(LocalDateTime dateFrom, LocalDateTime dateTo) throws DAOException {
        LOG.info("Getting free vehicles");
        ArrayList<Vehicle> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = DBUtil.getConnection().prepareStatement(vehicleList, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTo));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateFrom));
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

    @Override
    public List<Vehicle> filterVehicle(String query) throws DAOException {
        List<Vehicle> vehicleList = new ArrayList<>();
        LOG.info("Populating table vehicle with filter");
        try {
            Statement statement = DBUtil.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                vehicleList.add(resultSetVehicle(resultSet));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        LOG.info("Populating done");
        return vehicleList;
    }

    @Override
    public List<Vehicle> getVehiclesStatistics(LocalDateTime dateFrom, LocalDateTime dateTo) throws DAOException {
        List<Vehicle> vehicleList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DBUtil.getConnection().prepareStatement(statisticsVehicles, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(dateTo));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateFrom));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                Vehicle vehicle = new Vehicle();
                vehicle.setId(resultSet.getInt("vehicleId"));
                vehicle.setDrivingLicence(resultSet.getString("drivingLicence"));
                vehicle.setPrice(resultSet.getDouble("price"));
                vehicle.setStatisticsDateFrom(resultSet.getTimestamp("dateFrom").toLocalDateTime());
                vehicle.setStatisticsDateTo(resultSet.getTimestamp("dateTo").toLocalDateTime());
                vehicleList.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return vehicleList;
    }

    private String savePicture(String imagePath) throws DAOException {
        if (imagePath != null) {
            if (!imagePath.isEmpty()) {
                File input = new File(imagePath);
                File output = new File("/Users/Mensur/SEPMpictures/" + input.getName());
                try {
                    Files.copy(input.toPath(), output.toPath(), REPLACE_EXISTING);
                    LOG.info("Saving picture to SEPMpictures");
                    return output.getAbsolutePath();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return null;
    }


    private Vehicle resultSetVehicle(ResultSet resultSet) throws DAOException {
        Vehicle vehicle = new Vehicle();
        try {
            vehicle.setId(resultSet.getInt("id"));
            vehicle.setLabel(resultSet.getString("label"));
            vehicle.setYear(resultSet.getInt("year"));
            vehicle.setDescription(resultSet.getString("description"));
            vehicle.setSeats(resultSet.getInt("nrOfSeats"));
            vehicle.setRegistrationNr(resultSet.getString("registrationNr"));
            vehicle.setType(resultSet.getString("type"));
            vehicle.setPower(resultSet.getInt("power"));
            vehicle.setPrice(resultSet.getDouble("price"));
            vehicle.setDate(resultSet.getDate("date").toLocalDate());
            vehicle.setPicture(resultSet.getString("picture"));
            vehicle.setDrivingLicence(resultSet.getString("drivingLicence"));
            vehicle.setDateChanged(resultSet.getDate("dateChanged") == null ? null:resultSet.getDate("dateChanged").toLocalDate());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DAOException(e.getMessage());
        }
        return vehicle;
    }

}
