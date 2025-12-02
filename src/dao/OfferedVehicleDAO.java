package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.OfferedVehicle;

public class OfferedVehicleDAO {

    // ===== Basic CRUD =====

    public void addVehicle(OfferedVehicle v) throws SQLException {
        String sql = "INSERT INTO Offered_Vehicles (VIN, Make, Model, Year, Price, Status) " +
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

    public void updateVehicle(OfferedVehicle v) throws SQLException {
        String sql = "UPDATE Offered_Vehicles " +
                     "SET Make = ?, Model = ?, Year = ?, Price = ?, Status = ? " +
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

    public void deleteVehicle(String vin) throws SQLException {
        String sql = "DELETE FROM Offered_Vehicles WHERE VIN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vin);
            stmt.executeUpdate();
        }
    }

    public List<OfferedVehicle> getAllVehicles() throws SQLException {
        String sql = "SELECT VIN, Make, Model, Year, Price, Status " +
                     "FROM Offered_Vehicles ORDER BY VIN";

        List<OfferedVehicle> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OfferedVehicle v = new OfferedVehicle(
                        rs.getString("VIN"),
                        rs.getString("Make"),
                        rs.getString("Model"),
                        rs.getInt("Year"),
                        rs.getDouble("Price"),
                        rs.getString("Status")
                );
                list.add(v);
            }
        }

        return list;
    }

    // ===== Search by any combination of fields =====

    /**
     * Search vehicles by any combination of fields.
     * Only non-empty / non-null parameters are used as filters.
     */
    public List<OfferedVehicle> searchVehicles(
            String vin,
            String make,
            String model,
            Integer year,
            Double price,
            String status
    ) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT VIN, Make, Model, Year, Price, Status " +
                "FROM Offered_Vehicles WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (vin != null && !vin.isBlank()) {
            sql.append(" AND VIN = ?");
            params.add(vin.trim());
        }
        if (make != null && !make.isBlank()) {
            sql.append(" AND Make = ?");
            params.add(make.trim());
        }
        if (model != null && !model.isBlank()) {
            sql.append(" AND Model = ?");
            params.add(model.trim());
        }
        if (year != null) {
            sql.append(" AND Year = ?");
            params.add(year);
        }
        if (price != null) {
            sql.append(" AND Price = ?");
            params.add(price);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND Status = ?");
            params.add(status.trim());
        }

        List<OfferedVehicle> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OfferedVehicle v = new OfferedVehicle(
                            rs.getString("VIN"),
                            rs.getString("Make"),
                            rs.getString("Model"),
                            rs.getInt("Year"),
                            rs.getDouble("Price"),
                            rs.getString("Status")
                    );
                    results.add(v);
                }
            }
        }

        return results;
    }
}
