package at.ac.tuwien.sepm.assignment.individual.RentAVehicle.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static Connection connection = null;

    public static Connection getConnection(){
        if(connection == null){
            connection = openConnection();
        }

        return connection;
    }

    private static Connection openConnection(){
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:h2:~/sepm;INIT=runscript from 'classpath:create.sql'", "dbUser", "dbPassword");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
