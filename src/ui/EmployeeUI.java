package ui;

import dao.EmployeeDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Employee;

public class EmployeeUI extends JFrame {

    private final EmployeeDAO employeeDAO;

    private JTextField empSsnField;
    private JTextField firstNameField;
    private JTextField middleInitialField;
    private JTextField lastNameField;
    private JTextField positionField;
    private JTextField salaryField;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    // Default constructor
    public EmployeeUI() {
        this.employeeDAO = new EmployeeDAO();
        initializeUI();
    }

    // Overloaded for MainMenuUI (new EmployeeUI(this))
    public EmployeeUI(MainMenuUI parent) {
        this();
    }

    private void initializeUI() {
        setTitle("Manage Employees");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== Top form =====
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 8, 8));

        empSsnField        = new JTextField();
        firstNameField     = new JTextField();
        middleInitialField = new JTextField();
        lastNameField      = new JTextField();
        positionField      = new JTextField();
        salaryField        = new JTextField();

        formPanel.add(new JLabel("Emp SSN:"));
        formPanel.add(empSsnField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Middle Initial:"));
        formPanel.add(middleInitialField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Position:"));
        formPanel.add(positionField);
        formPanel.add(new JLabel("Salary:"));
        formPanel.add(salaryField);

        add(formPanel, BorderLayout.NORTH);

        // ===== Table =====
        tableModel = new DefaultTableModel(
                new Object[]{"Emp SSN", "First Name", "M.I.", "Last Name", "Position", "Salary"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmployeeIntoForm();
            }
        });

        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton    = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton loadButton   = new JButton("Load All");
        JButton searchButton = new JButton("Search");
        JButton clearButton  = new JButton("Clear");

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        loadButton.addActionListener(e -> loadEmployees());
        searchButton.addActionListener(e -> searchEmployees());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initial data
        loadEmployees();

        setVisible(true);
    }

    private Employee buildEmployeeFromForm() {
        String empSsn        = empSsnField.getText().trim();
        String firstName     = firstNameField.getText().trim();
        String middleInitial = middleInitialField.getText().trim();
        String lastName      = lastNameField.getText().trim();
        String position      = positionField.getText().trim();
        String salaryText    = salaryField.getText().trim();

        if (empSsn.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Emp SSN is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double salary = 0.0;
        if (!salaryText.isEmpty()) {
            try {
                salary = Double.parseDouble(salaryText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Salary must be a valid number.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }

        return new Employee(
                empSsn,
                salary,
                position,
                firstName,
                middleInitial,
                lastName
        );
    }

    private void addEmployee() {
        Employee emp = buildEmployeeFromForm();
        if (emp == null) return;

        try {
            employeeDAO.addEmployee(emp);
            JOptionPane.showMessageDialog(this,
                    "Employee added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadEmployees();
            clearForm();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding employee: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        Employee emp = buildEmployeeFromForm();
        if (emp == null) return;

        try {
            employeeDAO.updateEmployee(emp);
            JOptionPane.showMessageDialog(this,
                    "Employee updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadEmployees();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating employee: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String empSsn = (String) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete employee with SSN: " + empSsn + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            employeeDAO.deleteEmployee(empSsn);
            JOptionPane.showMessageDialog(this,
                    "Employee deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadEmployees();
            clearForm();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting employee: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEmployees() {
        try {
            List<Employee> list = employeeDAO.getAllEmployees();
            tableModel.setRowCount(0);

            for (Employee emp : list) {
                tableModel.addRow(new Object[]{
                        emp.getEmpSSN(),
                        emp.getFirstName(),
                        emp.getMiddleInitial(),
                        emp.getLastName(),
                        emp.getPosition(),
                        emp.getSalary()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading employees: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEmployees() {
        String empSsn        = empSsnField.getText().trim();
        String firstName     = firstNameField.getText().trim();
        String middleInitial = middleInitialField.getText().trim();
        String lastName      = lastNameField.getText().trim();
        String position      = positionField.getText().trim();
        String salaryText    = salaryField.getText().trim();

        if (empSsn.isEmpty() && firstName.isEmpty() && middleInitial.isEmpty()
                && lastName.isEmpty() && position.isEmpty() && salaryText.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Enter at least one field to search.\n" +
                    "To see all employees, click Load All.",
                    "No Search Criteria",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Double salaryExact = null;
        if (!salaryText.isEmpty()) {
            try {
                salaryExact = Double.parseDouble(salaryText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Salary must be a valid number for searching.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            List<Employee> list = employeeDAO.searchEmployees(
                    empSsn, firstName, middleInitial, lastName, position, salaryExact
            );

            tableModel.setRowCount(0);
            for (Employee emp : list) {
                tableModel.addRow(new Object[]{
                        emp.getEmpSSN(),
                        emp.getFirstName(),
                        emp.getMiddleInitial(),
                        emp.getLastName(),
                        emp.getPosition(),
                        emp.getSalary()
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No employees matched the given criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching employees: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        empSsnField.setText("");
        firstNameField.setText("");
        middleInitialField.setText("");
        lastNameField.setText("");
        positionField.setText("");
        salaryField.setText("");
        employeeTable.clearSelection();
    }

    private void loadSelectedEmployeeIntoForm() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) return;

        empSsnField.setText(tableModel.getValueAt(row, 0).toString());
        firstNameField.setText(tableModel.getValueAt(row, 1).toString());
        middleInitialField.setText(tableModel.getValueAt(row, 2).toString());
        lastNameField.setText(tableModel.getValueAt(row, 3).toString());
        positionField.setText(tableModel.getValueAt(row, 4).toString());
        salaryField.setText(tableModel.getValueAt(row, 5).toString());
    }
}
