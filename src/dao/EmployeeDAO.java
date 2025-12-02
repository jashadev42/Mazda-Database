package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Employee;

public class EmployeeDAO {

    public void addEmployee(Employee emp) throws SQLException {
        String sql = "INSERT INTO Employee " +
                "(Emp_SSN, Salary, Position, First_Name, Middle_Initial, Last_Name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, emp.getEmpSSN());
            stmt.setDouble(2, emp.getSalary());
            stmt.setString(3, emp.getPosition());
            stmt.setString(4, emp.getFirstName());
            stmt.setString(5, emp.getMiddleInitial());
            stmt.setString(6, emp.getLastName());

            stmt.executeUpdate();
        }
    }

    public void updateEmployee(Employee emp) throws SQLException {
        String sql = "UPDATE Employee SET " +
                "Salary = ?, Position = ?, First_Name = ?, Middle_Initial = ?, Last_Name = ? " +
                "WHERE Emp_SSN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, emp.getSalary());
            stmt.setString(2, emp.getPosition());
            stmt.setString(3, emp.getFirstName());
            stmt.setString(4, emp.getMiddleInitial());
            stmt.setString(5, emp.getLastName());
            stmt.setString(6, emp.getEmpSSN());

            stmt.executeUpdate();
        }
    }

    public void deleteEmployee(String empSsn) throws SQLException {
        String sql = "DELETE FROM Employee WHERE Emp_SSN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empSsn);
            stmt.executeUpdate();
        }
    }

    public List<Employee> getAllEmployees() throws SQLException {
        String sql = "SELECT Emp_SSN, Salary, Position, First_Name, Middle_Initial, Last_Name " +
                     "FROM Employee ORDER BY Last_Name, First_Name";

        List<Employee> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getString("Emp_SSN"),
                        rs.getDouble("Salary"),
                        rs.getString("Position"),
                        rs.getString("First_Name"),
                        rs.getString("Middle_Initial"),
                        rs.getString("Last_Name")
                );
                list.add(emp);
            }
        }

        return list;
    }

    /**
     * Search employees by any combination of fields.
     * Only non-empty / non-null parameters are used as filters.
     */
    public List<Employee> searchEmployees(String empSsn,
                                          String firstName,
                                          String middleInitial,
                                          String lastName,
                                          String position,
                                          Double salaryExact) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT Emp_SSN, Salary, Position, First_Name, Middle_Initial, Last_Name " +
                "FROM Employee WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (empSsn != null && !empSsn.isBlank()) {
            sql.append(" AND Emp_SSN = ?");
            params.add(empSsn.trim());
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
        if (position != null && !position.isBlank()) {
            sql.append(" AND Position = ?");
            params.add(position.trim());
        }
        if (salaryExact != null) {
            sql.append(" AND Salary = ?");
            params.add(salaryExact);
        }

        List<Employee> results = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Employee emp = new Employee(
                            rs.getString("Emp_SSN"),
                            rs.getDouble("Salary"),
                            rs.getString("Position"),
                            rs.getString("First_Name"),
                            rs.getString("Middle_Initial"),
                            rs.getString("Last_Name")
                    );
                    results.add(emp);
                }
            }
        }

        return results;
    }
}
