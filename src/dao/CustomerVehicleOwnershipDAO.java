package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.CustomerVehicleOwnership;

public class CustomerVehicleOwnershipDAO {

    // INSERT
    public void addOwnership(CustomerVehicleOwnership ownership) throws SQLException {
        String sql = "INSERT INTO Customer_Vehical_Ownership (VIN, License_ID) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownership.getVin());
            stmt.setString(2, ownership.getLicenseId());

            stmt.executeUpdate();
        }
    }

    // SELECT all with joined info
    public List<CustomerVehicleOwnership> getAllOwnerships() throws SQLException {
        List<CustomerVehicleOwnership> list = new ArrayList<>();

        String sql =
                "SELECT o.VIN, o.License_ID, " +
                "       c.First_Name, c.Last_Name, " +
                "       v.Year, v.Make, v.Model " +
                "FROM Customer_Vehical_Ownership o " +
                "JOIN Customer c ON o.License_ID = c.License_ID " +
                "JOIN Offered_Vehicles v ON o.VIN = v.VIN";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String vin        = rs.getString("VIN");
                String licenseId  = rs.getString("License_ID");
                String firstName  = rs.getString("First_Name");
                String lastName   = rs.getString("Last_Name");
                int year          = rs.getInt("Year");
                String make       = rs.getString("Make");
                String model      = rs.getString("Model");

                String vehicleDesc = year + " " + make + " " + model;

                CustomerVehicleOwnership ownership =
                        new CustomerVehicleOwnership(
                                vin,
                                licenseId,
                                firstName,
                                lastName,
                                vehicleDesc
                        );

                list.add(ownership);
            }
        }

        return list;
    }

    /**
     * Search ownerships by optional VIN and/or License_ID.
     * If a parameter is null or empty, it is ignored.
     * Uses LIKE so partial matches work (e.g. partial VIN).
     */
    public List<CustomerVehicleOwnership> searchOwnerships(String vinFilter, String licenseIdFilter) throws SQLException {
        List<CustomerVehicleOwnership> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT o.VIN, o.License_ID, " +
                "       c.First_Name, c.Last_Name, " +
                "       v.Year, v.Make, v.Model " +
                "FROM Customer_Vehical_Ownership o " +
                "JOIN Customer c ON o.License_ID = c.License_ID " +
                "JOIN Offered_Vehicles v ON o.VIN = v.VIN " +
                "WHERE 1=1"
        );

        boolean hasVin = vinFilter != null && !vinFilter.trim().isEmpty();
        boolean hasLicense = licenseIdFilter != null && !licenseIdFilter.trim().isEmpty();

        if (hasVin) {
            sql.append(" AND o.VIN LIKE ?");
        }
        if (hasLicense) {
            sql.append(" AND o.License_ID LIKE ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            if (hasVin) {
                stmt.setString(paramIndex++, "%" + vinFilter.trim() + "%");
            }
            if (hasLicense) {
                stmt.setString(paramIndex++, "%" + licenseIdFilter.trim() + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String vin        = rs.getString("VIN");
                    String licenseId  = rs.getString("License_ID");
                    String firstName  = rs.getString("First_Name");
                    String lastName   = rs.getString("Last_Name");
                    int year          = rs.getInt("Year");
                    String make       = rs.getString("Make");
                    String model      = rs.getString("Model");

                    String vehicleDesc = year + " " + make + " " + model;

                    CustomerVehicleOwnership ownership =
                            new CustomerVehicleOwnership(
                                    vin,
                                    licenseId,
                                    firstName,
                                    lastName,
                                    vehicleDesc
                            );

                    list.add(ownership);
                }
            }
        }

        return list;
    }

    // DELETE by composite PK
    public void deleteOwnership(String vin, String licenseId) throws SQLException {
        String sql = "DELETE FROM Customer_Vehical_Ownership WHERE VIN = ? AND License_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vin);
            stmt.setString(2, licenseId);

            stmt.executeUpdate();
        }
    }
}
