package dao;

import db.DBConnection;
import model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // INSERT
    public void addEmployee(Employee e) throws SQLException {
        String sql = "INSERT INTO Employee " +
                "(Emp_SSN, Salary, Position, First_Name, Middle_Initial, Last_Name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getEmpSSN());

            if (e.getSalary() != null) {
                stmt.setDouble(2, e.getSalary());
            } else {
                stmt.setNull(2, Types.DECIMAL);
            }

            stmt.setString(3, e.getPosition());
            stmt.setString(4, e.getFirstName());
            stmt.setString(5, e.getMiddleInitial());
            stmt.setString(6, e.getLastName());

            stmt.executeUpdate();
        }
    }

    // SELECT all
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();

        String sql = "SELECT Emp_SSN, Salary, Position, First_Name, Middle_Initial, Last_Name " +
                     "FROM Employee";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String empSSN        = rs.getString("Emp_SSN");
                Double salary        = rs.getObject("Salary") == null ? null : rs.getDouble("Salary");
                String position      = rs.getString("Position");
                String firstName     = rs.getString("First_Name");
                String middleInitial = rs.getString("Middle_Initial");
                String lastName      = rs.getString("Last_Name");

                Employee e = new Employee(empSSN, salary, position,
                                          firstName, middleInitial, lastName);
                list.add(e);
            }
        }

        return list;
    }

    // UPDATE by Emp_SSN
    public void updateEmployee(Employee e) throws SQLException {
        String sql = "UPDATE Employee SET " +
                "Salary = ?, Position = ?, First_Name = ?, Middle_Initial = ?, Last_Name = ? " +
                "WHERE Emp_SSN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (e.getSalary() != null) {
                stmt.setDouble(1, e.getSalary());
            } else {
                stmt.setNull(1, Types.DECIMAL);
            }

            stmt.setString(2, e.getPosition());
            stmt.setString(3, e.getFirstName());
            stmt.setString(4, e.getMiddleInitial());
            stmt.setString(5, e.getLastName());
            stmt.setString(6, e.getEmpSSN());

            stmt.executeUpdate();
        }
    }

    // DELETE by Emp_SSN
    public void deleteEmployee(String empSSN) throws SQLException {
        String sql = "DELETE FROM Employee WHERE Emp_SSN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empSSN);
            stmt.executeUpdate();
        }
    }
}
