package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Customer;

public class CustomerDAO {

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (License_ID, First_Name, Middle_Initial, Last_Name, Address, Email, Phone) " +
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

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE Customer SET First_Name = ?, Middle_Initial = ?, Last_Name = ?, " +
                     "Address = ?, Email = ?, Phone = ? WHERE License_ID = ?";

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

    public void deleteCustomer(String licenseId) throws SQLException {
        String sql = "DELETE FROM Customer WHERE License_ID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, licenseId);
            stmt.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT License_ID, First_Name, Middle_Initial, Last_Name, Address, Email, Phone " +
                     "FROM Customer ORDER BY Last_Name, First_Name";

        List<Customer> customers = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

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

    /**
     * Search customers by any combination of fields.
     * Only non-empty parameters are used to filter the results.
     */
    public List<Customer> searchCustomers(String licenseId,
                                          String firstName,
                                          String middleInitial,
                                          String lastName,
                                          String address,
                                          String email,
                                          String phone) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT License_ID, First_Name, Middle_Initial, Last_Name, Address, Email, Phone " +
                "FROM Customer WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (licenseId != null && !licenseId.isBlank()) {
            sql.append(" AND License_ID = ?");
            params.add(licenseId.trim());
        }
        if (firstName != null && !firstName.isBlank()) {
            sql.append(" AND First_Name = ?");
            params.add(firstName.trim());
        }
        if (middleInitial != null && !middleInitial.isBlank()) {
            sql.append(" AND Middle_Initial = ?");
            params.add(middleInitial.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            sql.append(" AND Last_Name = ?");
            params.add(lastName.trim());
        }
        if (address != null && !address.isBlank()) {
            sql.append(" AND Address = ?");
            params.add(address.trim());
        }
        if (email != null && !email.isBlank()) {
            sql.append(" AND Email = ?");
            params.add(email.trim());
        }
        if (phone != null && !phone.isBlank()) {
            sql.append(" AND Phone = ?");
            params.add(phone.trim());
        }

        List<Customer> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
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
                    results.add(c);
                }
            }
        }

        return results;
    }
}
