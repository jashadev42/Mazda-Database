package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Customer;

public class CustomerDAO {

    // CREATE
    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer " +
                     "(License_ID, First_Name, Middle_Initial, Last_Name, Address, Email, Phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getLicenseId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getMiddleInitial());
            stmt.setString(4, customer.getLastName());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getEmail());
            stmt.setString(7, customer.getPhone());

            stmt.executeUpdate();
        }
    }

    // READ – all customers
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT License_ID, First_Name, Middle_Initial, Last_Name, " +
                     "Address, Email, Phone FROM Customer";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String licenseId     = rs.getString("License_ID");
                String firstName     = rs.getString("First_Name");
                String middleInitial = rs.getString("Middle_Initial");
                String lastName      = rs.getString("Last_Name");
                String address       = rs.getString("Address");
                String email         = rs.getString("Email");
                String phone         = rs.getString("Phone");

                Customer c = new Customer(
                        licenseId,
                        firstName,
                        middleInitial,
                        lastName,
                        address,
                        email,
                        phone
                );

                customers.add(c);
            }
        }

        return customers;
    }

    // UPDATE – change info for an existing customer
    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE Customer SET " +
                     "First_Name = ?, Middle_Initial = ?, Last_Name = ?, " +
                     "Address = ?, Email = ?, Phone = ? " +
                     "WHERE License_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getMiddleInitial());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getAddress());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getPhone());
            stmt.setString(7, customer.getLicenseId());

            stmt.executeUpdate();
        }
    }

    // DELETE – remove a customer by License_ID
    public void deleteCustomer(String licenseId) throws SQLException {
        String sql = "DELETE FROM Customer WHERE License_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licenseId);
            stmt.executeUpdate();
        }
    }
}
