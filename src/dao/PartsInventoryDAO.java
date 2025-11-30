package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Part;

public class PartsInventoryDAO {

    // INSERT
    public void addPart(Part p) throws SQLException {
        String sql = "INSERT INTO Parts_Inventory " +
                     "(Part_ID, Quantity, Part_Name, Unit_Price) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getPartId());
            stmt.setInt(2, p.getQuantity());
            stmt.setString(3, p.getPartName());
            stmt.setDouble(4, p.getUnitPrice());

            stmt.executeUpdate();
        }
    }

    // SELECT all
    public List<Part> getAllParts() throws SQLException {
        List<Part> parts = new ArrayList<>();

        String sql = "SELECT Part_ID, Quantity, Part_Name, Unit_Price " +
                     "FROM Parts_Inventory";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Part p = new Part(
                        rs.getString("Part_ID"),
                        rs.getString("Part_Name"),
                        rs.getInt("Quantity"),
                        rs.getDouble("Unit_Price")
                );
                parts.add(p);
            }
        }

        return parts;
    }

    // UPDATE by Part_ID
    public void updatePart(Part p) throws SQLException {
        String sql = "UPDATE Parts_Inventory SET " +
                     "Quantity = ?, Part_Name = ?, Unit_Price = ? " +
                     "WHERE Part_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getQuantity());
            stmt.setString(2, p.getPartName());
            stmt.setDouble(3, p.getUnitPrice());
            stmt.setString(4, p.getPartId());

            stmt.executeUpdate();
        }
    }

    // DELETE by Part_ID
    public void deletePart(String partId) throws SQLException {
        String sql = "DELETE FROM Parts_Inventory WHERE Part_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, partId);
            stmt.executeUpdate();
        }
    }

    // Optional: find one part by ID
    public Part findById(String partId) throws SQLException {
        String sql = "SELECT Part_ID, Quantity, Part_Name, Unit_Price " +
                     "FROM Parts_Inventory WHERE Part_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, partId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Part(
                            rs.getString("Part_ID"),
                            rs.getString("Part_Name"),
                            rs.getInt("Quantity"),
                            rs.getDouble("Unit_Price")
                    );
                }
            }
        }
        return null;
    }
}
