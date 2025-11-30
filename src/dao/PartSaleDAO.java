package dao;

import db.DBConnection;
import model.PartSale;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartSaleDAO {

    // INSERT
    public void addPartSale(PartSale sale) throws SQLException {
        String sql = "INSERT INTO Part_Sales " +
                     "(License_ID, Part_ID, Emp_SSN, Count, Amount, Sale_Date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getLicenseId());
            stmt.setString(2, sale.getPartId());
            stmt.setString(3, sale.getEmpSSN());

            if (sale.getCount() != null) {
                stmt.setInt(4, sale.getCount());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (sale.getAmount() != null) {
                stmt.setDouble(5, sale.getAmount());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }

            if (sale.getSaleDate() != null && !sale.getSaleDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(sale.getSaleDate())); // yyyy-MM-dd
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.executeUpdate();
        }
    }

    // SELECT all (with joins)
    public List<PartSale> getAllPartSales() throws SQLException {
        List<PartSale> list = new ArrayList<>();

        String sql =
            "SELECT ps.Part_Sale_ID, ps.License_ID, ps.Part_ID, ps.Emp_SSN, " +
            "       ps.Count, ps.Amount, ps.Sale_Date, " +
            "       c.First_Name AS Cust_First_Name, c.Last_Name AS Cust_Last_Name, " +
            "       e.First_Name AS Emp_First_Name,  e.Last_Name AS Emp_Last_Name, " +
            "       p.Part_Name " +
            "FROM Part_Sales ps " +
            "JOIN Customer        c ON ps.License_ID = c.License_ID " +
            "JOIN Employee        e ON ps.Emp_SSN    = e.Emp_SSN " +
            "JOIN Parts_Inventory p ON ps.Part_ID    = p.Part_ID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int partSaleId       = rs.getInt("Part_Sale_ID");
                String licenseId     = rs.getString("License_ID");
                String partId        = rs.getString("Part_ID");
                String empSSN        = rs.getString("Emp_SSN");
                Integer count        = rs.getObject("Count") == null
                        ? null
                        : rs.getInt("Count");
                Double amount        = rs.getObject("Amount") == null
                        ? null
                        : rs.getDouble("Amount");
                Date saleDate        = rs.getDate("Sale_Date");
                String saleDateStr   = (saleDate != null) ? saleDate.toString() : null;

                String custFirstName = rs.getString("Cust_First_Name");
                String custLastName  = rs.getString("Cust_Last_Name");
                String empFirstName  = rs.getString("Emp_First_Name");
                String empLastName   = rs.getString("Emp_Last_Name");
                String partName      = rs.getString("Part_Name");

                PartSale sale = new PartSale(
                        partSaleId,
                        licenseId,
                        partId,
                        empSSN,
                        count,
                        amount,
                        saleDateStr,
                        custFirstName,
                        custLastName,
                        empFirstName,
                        empLastName,
                        partName
                );

                list.add(sale);
            }
        }

        return list;
    }

    // UPDATE
    public void updatePartSale(PartSale sale) throws SQLException {
        String sql =
            "UPDATE Part_Sales SET " +
            "License_ID = ?, Part_ID = ?, Emp_SSN = ?, Count = ?, Amount = ?, Sale_Date = ? " +
            "WHERE Part_Sale_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, sale.getLicenseId());
            stmt.setString(2, sale.getPartId());
            stmt.setString(3, sale.getEmpSSN());

            if (sale.getCount() != null) {
                stmt.setInt(4, sale.getCount());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            if (sale.getAmount() != null) {
                stmt.setDouble(5, sale.getAmount());
            } else {
                stmt.setNull(5, Types.DECIMAL);
            }

            if (sale.getSaleDate() != null && !sale.getSaleDate().isEmpty()) {
                stmt.setDate(6, Date.valueOf(sale.getSaleDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setInt(7, sale.getPartSaleId());

            stmt.executeUpdate();
        }
    }

    // DELETE
    public void deletePartSale(int partSaleId) throws SQLException {
        String sql = "DELETE FROM Part_Sales WHERE Part_Sale_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, partSaleId);
            stmt.executeUpdate();
        }
    }

    // COMPLETE SALE: subtract Count from Parts_Inventory.Quantity
    public void completeSale(int partSaleId) throws SQLException {
        String selectSaleSql =
            "SELECT Part_ID, Count FROM Part_Sales WHERE Part_Sale_ID = ?";
        String selectInvSql =
            "SELECT Quantity FROM Parts_Inventory WHERE Part_ID = ?";
        String updateInvSql =
            "UPDATE Parts_Inventory SET Quantity = Quantity - ? WHERE Part_ID = ?";

        Connection conn = null;
        PreparedStatement saleStmt = null;
        PreparedStatement invSelectStmt = null;
        PreparedStatement invUpdateStmt = null;
        ResultSet saleRs = null;
        ResultSet invRs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Get Part_ID and Count for this sale
            saleStmt = conn.prepareStatement(selectSaleSql);
            saleStmt.setInt(1, partSaleId);
            saleRs = saleStmt.executeQuery();

            if (!saleRs.next()) {
                throw new SQLException("Part_Sale_ID " + partSaleId + " not found.");
            }

            String partId = saleRs.getString("Part_ID");
            int count = saleRs.getInt("Count");

            // Check current inventory
            invSelectStmt = conn.prepareStatement(selectInvSql);
            invSelectStmt.setString(1, partId);
            invRs = invSelectStmt.executeQuery();

            if (!invRs.next()) {
                throw new SQLException("Part_ID " + partId + " not found in Parts_Inventory.");
            }

            int currentQty = invRs.getInt("Quantity");
            if (currentQty < count) {
                throw new SQLException("Not enough inventory. Current: " + currentQty +
                        ", required: " + count);
            }

            // Subtract from inventory
            invUpdateStmt = conn.prepareStatement(updateInvSql);
            invUpdateStmt.setInt(1, count);
            invUpdateStmt.setString(2, partId);
            invUpdateStmt.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (saleRs != null) try { saleRs.close(); } catch (SQLException ignored) {}
            if (invRs != null) try { invRs.close(); } catch (SQLException ignored) {}
            if (saleStmt != null) try { saleStmt.close(); } catch (SQLException ignored) {}
            if (invSelectStmt != null) try { invSelectStmt.close(); } catch (SQLException ignored) {}
            if (invUpdateStmt != null) try { invUpdateStmt.close(); } catch (SQLException ignored) {}
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
        }
    }
}
