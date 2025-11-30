package dao;

import db.DBConnection;
import model.VehicleSale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleSaleDAO {

    // INSERT (Sale_ID is AUTO_INCREMENT)
    public void addSale(VehicleSale sale) throws SQLException {
        String sql = "INSERT INTO Vehicle_Sales_Leases_Finances " +
                     "(License_ID, VIN, Emp_SSN, Terms, Final_Price, Sale_Date, Sale_Type) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getLicenseId());
            stmt.setString(2, sale.getVin());
            stmt.setString(3, sale.getEmpSSN());
            stmt.setString(4, sale.getTerms());

            if (sale.getFinalPrice() != null) {
                stmt.setDouble(5, sale.getFinalPrice());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }

            if (sale.getSaleDate() != null && !sale.getSaleDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(sale.getSaleDate())); // expects yyyy-MM-dd
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, sale.getSaleType());

            stmt.executeUpdate();
        }
    }

    // SELECT all with joined info
    public List<VehicleSale> getAllSales() throws SQLException {
        List<VehicleSale> list = new ArrayList<>();

        String sql =
            "SELECT s.Sale_ID, s.License_ID, s.VIN, s.Emp_SSN, " +
            "       s.Terms, s.Final_Price, s.Sale_Date, s.Sale_Type, " +
            "       c.First_Name  AS Cust_First_Name, " +
            "       c.Last_Name   AS Cust_Last_Name, " +
            "       e.First_Name  AS Emp_First_Name, " +
            "       e.Last_Name   AS Emp_Last_Name, " +
            "       v.Year, v.Make, v.Model " +
            "FROM Vehicle_Sales_Leases_Finances s " +
            "JOIN Customer c        ON s.License_ID = c.License_ID " +
            "JOIN Employee e        ON s.Emp_SSN    = e.Emp_SSN " +
            "JOIN Offered_Vehicles v ON s.VIN       = v.VIN";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int saleId           = rs.getInt("Sale_ID");
                String licenseId     = rs.getString("License_ID");
                String vin           = rs.getString("VIN");
                String empSSN        = rs.getString("Emp_SSN");
                String terms         = rs.getString("Terms");
                Double finalPrice    = rs.getObject("Final_Price") == null
                        ? null
                        : rs.getDouble("Final_Price");
                Date saleDate        = rs.getDate("Sale_Date");
                String saleDateStr   = (saleDate != null) ? saleDate.toString() : null;
                String saleType      = rs.getString("Sale_Type");

                String custFirstName = rs.getString("Cust_First_Name");
                String custLastName  = rs.getString("Cust_Last_Name");
                String empFirstName  = rs.getString("Emp_First_Name");
                String empLastName   = rs.getString("Emp_Last_Name");
                int year             = rs.getInt("Year");
                String make          = rs.getString("Make");
                String model         = rs.getString("Model");

                String vehicleDesc = year + " " + make + " " + model;

                VehicleSale sale = new VehicleSale(
                        saleId,
                        licenseId,
                        vin,
                        empSSN,
                        terms,
                        finalPrice,
                        saleDateStr,
                        saleType,
                        custFirstName,
                        custLastName,
                        empFirstName,
                        empLastName,
                        vehicleDesc
                );

                list.add(sale);
            }
        }

        return list;
    }

    // UPDATE by Sale_ID
    public void updateSale(VehicleSale sale) throws SQLException {
        String sql =
            "UPDATE Vehicle_Sales_Leases_Finances SET " +
            "License_ID = ?, VIN = ?, Emp_SSN = ?, Terms = ?, " +
            "Final_Price = ?, Sale_Date = ?, Sale_Type = ? " +
            "WHERE Sale_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getLicenseId());
            stmt.setString(2, sale.getVin());
            stmt.setString(3, sale.getEmpSSN());
            stmt.setString(4, sale.getTerms());

            if (sale.getFinalPrice() != null) {
                stmt.setDouble(5, sale.getFinalPrice());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }

            if (sale.getSaleDate() != null && !sale.getSaleDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(sale.getSaleDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, sale.getSaleType());
            stmt.setInt(8, sale.getSaleId());

            stmt.executeUpdate();
        }
    }

    // DELETE by Sale_ID
    public void deleteSale(int saleId) throws SQLException {
        String sql = "DELETE FROM Vehicle_Sales_Leases_Finances WHERE Sale_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            stmt.executeUpdate();
        }
    }
}
