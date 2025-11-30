package dao;

import db.DBConnection;
import model.OfferedVehicle;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfferedVehicleDAO {

    // INSERT
    public void addVehicle(OfferedVehicle v) throws SQLException {
        String sql = "INSERT INTO Offered_Vehicles " +
                     "(VIN, Make, Model, Year, Price, Status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getVin());
            stmt.setString(2, v.getMake());
            stmt.setString(3, v.getModel());
            stmt.setInt(4, v.getYear());
            stmt.setDouble(5, v.getPrice());
            stmt.setString(6, v.getStatus());

            stmt.executeUpdate();
        }
    }

    // SELECT all
    public List<OfferedVehicle> getAllVehicles() throws SQLException {
        List<OfferedVehicle> vehicles = new ArrayList<>();

        String sql = "SELECT VIN, Make, Model, Year, Price, Status " +
                     "FROM Offered_Vehicles";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                OfferedVehicle v = new OfferedVehicle(
                        rs.getString("VIN"),
                        rs.getString("Make"),
                        rs.getString("Model"),
                        rs.getInt("Year"),
                        rs.getDouble("Price"),
                        rs.getString("Status")
                );
                vehicles.add(v);
            }
        }

        return vehicles;
    }

    // UPDATE (by VIN)
    public void updateVehicle(OfferedVehicle v) throws SQLException {
        String sql = "UPDATE Offered_Vehicles SET " +
                     "Make = ?, Model = ?, Year = ?, Price = ?, Status = ? " +
                     "WHERE VIN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getMake());
            stmt.setString(2, v.getModel());
            stmt.setInt(3, v.getYear());
            stmt.setDouble(4, v.getPrice());
            stmt.setString(5, v.getStatus());
            stmt.setString(6, v.getVin());

            stmt.executeUpdate();
        }
    }

    // DELETE (by VIN)
    public void deleteVehicle(String vin) throws SQLException {
        String sql = "DELETE FROM Offered_Vehicles WHERE VIN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vin);
            stmt.executeUpdate();
        }
    }

    // (Optional) find one by VIN
    public OfferedVehicle findByVin(String vin) throws SQLException {
        String sql = "SELECT VIN, Make, Model, Year, Price, Status " +
                     "FROM Offered_Vehicles WHERE VIN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new OfferedVehicle(
                            rs.getString("VIN"),
                            rs.getString("Make"),
                            rs.getString("Model"),
                            rs.getInt("Year"),
                            rs.getDouble("Price"),
                            rs.getString("Status")
                    );
                }
            }
        }
        return null;
    }
}
