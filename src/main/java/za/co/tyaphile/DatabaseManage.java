package za.co.tyaphile;

import za.co.tyaphile.connect.Connect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseManage {
    private final String DB_SCHEMA = "products";
    private final String DB_TABLE = "product";
    private final String username = "root";
    private final String password = "P@ssi0nat3ly";
//    private final String password = "P@ssw0rd_01";
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public DatabaseManage() {
        Connect.createDatabase(DB_SCHEMA, username, password);

        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + " (" +
                    "`id` VARCHAR(255) NOT NULL, " +
                    "`product_name` VARCHAR(255) NOT NULL, " +
                    "`product_description` TEXT NOT NULL, " +
                    "`product_old_price` DOUBLE, " +
                    "`product_current_price` DOUBLE NOT NULL, " +
                    "`product_weight` DOUBLE DEFAULT 0 NOT NULL, " +
                    "`product_measure` VARCHAR(11), " +
                    "`product_bulk` VARCHAR(45), " +
                    "`product_image` BLOB NOT NULL, " +
                    "`product_link` VARCHAR(500) NOT NULL, " +
                    "PRIMARY KEY (`id`)" +
                    ");";

            connection = Connect.getConnection(DB_SCHEMA, username, password);
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void addProduct(long id, String productName, String productDescription, double productOldPrice, double productCurrentPrice,
                              double productWeight, String productMeasure, String productBulk, String productImage, String productLink) {
        String sql = "INSERT INTO " + DB_TABLE + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            connection = Connect.getConnection(DB_SCHEMA, username, password);
            preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, productName);
            preparedStatement.setString(3, productDescription);
            preparedStatement.setDouble(4, productOldPrice);
            preparedStatement.setDouble(5, productCurrentPrice);
            preparedStatement.setDouble(6, productWeight);
            preparedStatement.setString(7, productMeasure);
            preparedStatement.setString(8, productBulk);
            preparedStatement.setString(9, productImage);
            preparedStatement.setString(10, productLink);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}