package dao;

import db.DBConnection;
import model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // INSERT
    public void addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO Customer " +
                     "(License_ID, First_Name, Middle_Initial, Last_Name, Address, Email, Phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, c.getLicenseId());
            stmt.setString(2, c.getFirstName());
            stmt.setString(3, c.getMiddleInitial());
            stmt.setString(4, c.getLastName());
            stmt.setString(5, c.getAddress());
            stmt.setString(6, c.getEmail());
            stmt.setString(7, c.getPhone());

            stmt.executeUpdate();
        }
    }

    // SELECT all
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT License_ID, First_Name, Middle_Initial, Last_Name, " +
                     "Address, Email, Phone FROM Customer";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer c = new Customer(
                    rs.getString("License_ID"),
                    rs.getString("First_Name"),
                    rs.getString("Middle_Initial"),
                    rs.getString("Last_Name"),
                    rs.getString("Address"),
                    rs.getString("Email"),
                    rs.getString("Phone")
                );
                customers.add(c);
            }
        }

        return customers;
    }

    // You can add update/delete/search later:
    // updateCustomer(Customer c)
    // deleteCustomer(String licenseId)
    // findByLicense(String licenseId)
}
