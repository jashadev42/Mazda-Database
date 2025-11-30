package dao;

import db.DBConnection;
import model.Invoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    // INSERT
    public void addInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO Sent_Invoices " +
                     "(Emp_SSN, License_ID, Amount, Purpose, Date) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, invoice.getEmpSSN());
            stmt.setString(2, invoice.getLicenseId());

            if (invoice.getAmount() != null) {
                stmt.setDouble(3, invoice.getAmount());
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }

            stmt.setString(4, invoice.getPurpose());

            if (invoice.getDate() != null && !invoice.getDate().isEmpty()) {
                stmt.setDate(5, Date.valueOf(invoice.getDate())); // yyyy-MM-dd
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.executeUpdate();
        }
    }

    // SELECT all with joined info
    public List<Invoice> getAllInvoices() throws SQLException {
        List<Invoice> list = new ArrayList<>();

        String sql =
            "SELECT i.Invoice_ID, i.Emp_SSN, i.License_ID, i.Amount, i.Purpose, i.Date, " +
            "       c.First_Name AS Cust_First_Name, c.Last_Name AS Cust_Last_Name, " +
            "       e.First_Name AS Emp_First_Name,  e.Last_Name AS Emp_Last_Name " +
            "FROM Sent_Invoices i " +
            "JOIN Customer c ON i.License_ID = c.License_ID " +
            "JOIN Employee e ON i.Emp_SSN   = e.Emp_SSN";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int invoiceId         = rs.getInt("Invoice_ID");
                String empSSN         = rs.getString("Emp_SSN");
                String licenseId      = rs.getString("License_ID");
                Double amount         = rs.getObject("Amount") == null
                        ? null
                        : rs.getDouble("Amount");
                String purpose        = rs.getString("Purpose");
                Date date             = rs.getDate("Date");
                String dateStr        = (date != null) ? date.toString() : null;

                String custFirstName  = rs.getString("Cust_First_Name");
                String custLastName   = rs.getString("Cust_Last_Name");
                String empFirstName   = rs.getString("Emp_First_Name");
                String empLastName    = rs.getString("Emp_Last_Name");

                Invoice invoice = new Invoice(
                        invoiceId,
                        empSSN,
                        licenseId,
                        amount,
                        purpose,
                        dateStr,
                        custFirstName,
                        custLastName,
                        empFirstName,
                        empLastName
                );

                list.add(invoice);
            }
        }

        return list;
    }

    // UPDATE by Invoice_ID
    public void updateInvoice(Invoice invoice) throws SQLException {
        String sql =
            "UPDATE Sent_Invoices SET " +
            "Emp_SSN = ?, License_ID = ?, Amount = ?, Purpose = ?, Date = ? " +
            "WHERE Invoice_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, invoice.getEmpSSN());
            stmt.setString(2, invoice.getLicenseId());

            if (invoice.getAmount() != null) {
                stmt.setDouble(3, invoice.getAmount());
            } else {
                stmt.setNull(3, Types.DECIMAL);
            }

            stmt.setString(4, invoice.getPurpose());

            if (invoice.getDate() != null && !invoice.getDate().isEmpty()) {
                stmt.setDate(5, Date.valueOf(invoice.getDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setInt(6, invoice.getInvoiceId());

            stmt.executeUpdate();
        }
    }

    // DELETE by Invoice_ID
    public void deleteInvoice(int invoiceId) throws SQLException {
        String sql = "DELETE FROM Sent_Invoices WHERE Invoice_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, invoiceId);
            stmt.executeUpdate();
        }
    }
}
