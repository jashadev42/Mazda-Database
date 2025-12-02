package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.VehicleSale;

public class VehicleSaleDAO {

    // =========================
    // Helpers
    // =========================

    private VehicleSale mapRow(ResultSet rs) throws SQLException {
        return new VehicleSale(
                rs.getInt("Sale_ID"),
                rs.getString("License_ID"),
                rs.getString("VIN"),
                rs.getString("Emp_SSN"),
                rs.getString("Terms"),
                rs.getDouble("Final_Price"),
                rs.getString("Sale_Date"),     // <-- String, matches your model
                rs.getString("Sale_Type"),
                rs.getString("CustFirst"),
                rs.getString("CustLast"),
                rs.getString("EmpFirst"),
                rs.getString("EmpLast"),
                rs.getString("VehicleDesc")
        );
    }

    /**
     * Converts the String saleDate ("yyyy-MM-dd") into java.sql.Date.
     * If null/empty, returns null so the DB can store NULL.
     */
    private Date toSqlDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        return Date.valueOf(dateString); // expects yyyy-MM-dd
    }

    // =========================
    // BASIC CRUD
    // =========================

    public List<VehicleSale> getAllVehicleSales() throws SQLException {
        String sql =
                "SELECT vs.Sale_ID, vs.License_ID, vs.VIN, vs.Emp_SSN, vs.Terms, " +
                "       vs.Final_Price, vs.Sale_Date, vs.Sale_Type, " +
                "       c.First_Name AS CustFirst, c.Last_Name AS CustLast, " +
                "       e.First_Name AS EmpFirst, e.Last_Name AS EmpLast, " +
                "       CONCAT(ov.Year, ' ', ov.Make, ' ', ov.Model) AS VehicleDesc " +
                "FROM Vehicle_Sales_Leases_Finances vs " +
                "LEFT JOIN Customer c ON vs.License_ID = c.License_ID " +
                "LEFT JOIN Employee e ON vs.Emp_SSN = e.Emp_SSN " +
                "LEFT JOIN Offered_Vehicles ov ON vs.VIN = ov.VIN " +
                "ORDER BY vs.Sale_ID";

        List<VehicleSale> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }

        return list;
    }

    public void addVehicleSale(VehicleSale sale) throws SQLException {
        String sql =
                "INSERT INTO Vehicle_Sales_Leases_Finances " +
                " (License_ID, VIN, Emp_SSN, Terms, Final_Price, Sale_Date, Sale_Type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getLicenseId());
            stmt.setString(2, sale.getVin());
            stmt.setString(3, sale.getEmpSSN());
            stmt.setString(4, sale.getTerms());
            if (sale.getFinalPrice() == null) {
                stmt.setNull(5, java.sql.Types.DECIMAL);
            } else {
                stmt.setDouble(5, sale.getFinalPrice());
            }

            Date sqlDate = toSqlDate(sale.getSaleDate());
            if (sqlDate == null) {
                stmt.setNull(6, java.sql.Types.DATE);
            } else {
                stmt.setDate(6, sqlDate);
            }

            stmt.setString(7, sale.getSaleType());

            stmt.executeUpdate();
        }
    }

    public void updateVehicleSale(VehicleSale sale) throws SQLException {
        String sql =
                "UPDATE Vehicle_Sales_Leases_Finances " +
                "SET License_ID = ?, VIN = ?, Emp_SSN = ?, Terms = ?, " +
                "    Final_Price = ?, Sale_Date = ?, Sale_Type = ? " +
                "WHERE Sale_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getLicenseId());
            stmt.setString(2, sale.getVin());
            stmt.setString(3, sale.getEmpSSN());
            stmt.setString(4, sale.getTerms());

            if (sale.getFinalPrice() == null) {
                stmt.setNull(5, java.sql.Types.DECIMAL);
            } else {
                stmt.setDouble(5, sale.getFinalPrice());
            }

            Date sqlDate = toSqlDate(sale.getSaleDate());
            if (sqlDate == null) {
                stmt.setNull(6, java.sql.Types.DATE);
            } else {
                stmt.setDate(6, sqlDate);
            }

            stmt.setString(7, sale.getSaleType());
            stmt.setInt(8, sale.getSaleId());

            stmt.executeUpdate();
        }
    }

    public void deleteVehicleSale(int saleId) throws SQLException {
        String sql = "DELETE FROM Vehicle_Sales_Leases_Finances WHERE Sale_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.executeUpdate();
        }
    }

    // =========================
    // SEARCH (multi-field)
    // =========================

    /**
     * Search vehicle sales by any combination of fields.
     * Pass null/empty for fields you don't want to filter on.
     *
     * @param saleId     Sale_ID (exact match, optional)
     * @param licenseId  Customer License_ID (optional)
     * @param vin        VIN (optional)
     * @param empSsn     Employee SSN (optional)
     * @param saleType   Sale type (e.g. "Sale", "Lease", optional)
     * @param minPrice   Minimum Final_Price (optional)
     * @param maxPrice   Maximum Final_Price (optional)
     * @param startDate  Earliest Sale_Date (yyyy-MM-dd, optional)
     * @param endDate    Latest Sale_Date (yyyy-MM-dd, optional)
     */
    public List<VehicleSale> searchVehicleSales(
            String saleId,
            String licenseId,
            String vin,
            String empSsn,
            String saleType,
            String minPrice,
            String maxPrice,
            String startDate,
            String endDate
    ) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT vs.Sale_ID, vs.License_ID, vs.VIN, vs.Emp_SSN, vs.Terms, " +
                "       vs.Final_Price, vs.Sale_Date, vs.Sale_Type, " +
                "       c.First_Name AS CustFirst, c.Last_Name AS CustLast, " +
                "       e.First_Name AS EmpFirst, e.Last_Name AS EmpLast, " +
                "       CONCAT(ov.Year, ' ', ov.Make, ' ', ov.Model) AS VehicleDesc " +
                "FROM Vehicle_Sales_Leases_Finances vs " +
                "LEFT JOIN Customer c ON vs.License_ID = c.License_ID " +
                "LEFT JOIN Employee e ON vs.Emp_SSN = e.Emp_SSN " +
                "LEFT JOIN Offered_Vehicles ov ON vs.VIN = ov.VIN " +
                "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (saleId != null && !saleId.isBlank()) {
            sql.append(" AND vs.Sale_ID = ? ");
            params.add(Integer.parseInt(saleId));
        }
        if (licenseId != null && !licenseId.isBlank()) {
            sql.append(" AND vs.License_ID LIKE ? ");
            params.add("%" + licenseId + "%");
        }
        if (vin != null && !vin.isBlank()) {
            sql.append(" AND vs.VIN LIKE ? ");
            params.add("%" + vin + "%");
        }
        if (empSsn != null && !empSsn.isBlank()) {
            sql.append(" AND vs.Emp_SSN LIKE ? ");
            params.add("%" + empSsn + "%");
        }
        if (saleType != null && !saleType.isBlank()) {
            sql.append(" AND vs.Sale_Type LIKE ? ");
            params.add("%" + saleType + "%");
        }
        if (minPrice != null && !minPrice.isBlank()) {
            sql.append(" AND vs.Final_Price >= ? ");
            params.add(Double.parseDouble(minPrice));
        }
        if (maxPrice != null && !maxPrice.isBlank()) {
            sql.append(" AND vs.Final_Price <= ? ");
            params.add(Double.parseDouble(maxPrice));
        }
        if (startDate != null && !startDate.isBlank()) {
            sql.append(" AND vs.Sale_Date >= ? ");
            params.add(Date.valueOf(startDate));
        }
        if (endDate != null && !endDate.isBlank()) {
            sql.append(" AND vs.Sale_Date <= ? ");
            params.add(Date.valueOf(endDate));
        }

        sql.append(" ORDER BY vs.Sale_ID ");

        List<VehicleSale> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }

        return results;
    }
}
