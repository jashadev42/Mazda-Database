package ui;

import dao.EmployeeDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Employee;

public class EmployeeUI extends JFrame {

    private JTextField ssnField;
    private JTextField salaryField;
    private JTextField positionField;
    private JTextField firstNameField;
    private JTextField middleInitialField;
    private JTextField lastNameField;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private EmployeeDAO employeeDAO;

    public EmployeeUI(JFrame parent) {
        setTitle("Manage Employees");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        employeeDAO = new EmployeeDAO();

        // ---------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        ssnField           = new JTextField();
        salaryField        = new JTextField();
        positionField      = new JTextField();
        firstNameField     = new JTextField();
        middleInitialField = new JTextField();
        lastNameField      = new JTextField();

        formPanel.add(new JLabel("Emp SSN:"));
        formPanel.add(ssnField);
        formPanel.add(new JLabel("Salary:"));
        formPanel.add(salaryField);

        formPanel.add(new JLabel("Position:"));
        formPanel.add(positionField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Middle Initial:"));
        formPanel.add(middleInitialField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        add(formPanel, BorderLayout.NORTH);

        // ---------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"Emp_SSN", "Salary", "Position", "First_Name",
                        "Middle_Initial", "Last_Name"},
                0
        );
        employeeTable = new JTable(tableModel);
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        // ---------- Buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn    = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton loadBtn   = new JButton("Load All");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // ---------- Button actions ----------
        addBtn.addActionListener(e -> addEmployee());
        updateBtn.addActionListener(e -> updateEmployee());
        deleteBtn.addActionListener(e -> deleteEmployee());
        loadBtn.addActionListener(e -> loadEmployees());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = employeeTable.getSelectedRow();
                if (row >= 0) {
                    ssnField.setText(valueOrEmpty(tableModel.getValueAt(row, 0)));
                    salaryField.setText(valueOrEmpty(tableModel.getValueAt(row, 1)));
                    positionField.setText(valueOrEmpty(tableModel.getValueAt(row, 2)));
                    firstNameField.setText(valueOrEmpty(tableModel.getValueAt(row, 3)));
                    middleInitialField.setText(valueOrEmpty(tableModel.getValueAt(row, 4)));
                    lastNameField.setText(valueOrEmpty(tableModel.getValueAt(row, 5)));
                }
            }
        });

        // Load all on open
        loadEmployees();

        setVisible(true);
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : value.toString();
    }

    private void addEmployee() {
        String ssn       = ssnField.getText().trim();
        String salaryTxt = salaryField.getText().trim();
        String position  = positionField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middle    = middleInitialField.getText().trim();
        String lastName  = lastNameField.getText().trim();

        if (ssn.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Emp SSN, First Name, and Last Name are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double salary = null;
        if (!salaryTxt.isEmpty()) {
            try {
                salary = Double.parseDouble(salaryTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Salary must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Employee emp = new Employee(ssn, salary, position, firstName, middle, lastName);

        try {
            employeeDAO.addEmployee(emp);
            JOptionPane.showMessageDialog(this, "Employee added.");
            clearForm();
            loadEmployees();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding employee: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select an employee in the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ssn       = ssnField.getText().trim();
        String salaryTxt = salaryField.getText().trim();
        String position  = positionField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String middle    = middleInitialField.getText().trim();
        String lastName  = lastNameField.getText().trim();

        if (ssn.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Emp SSN, First Name, and Last Name are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double salary = null;
        if (!salaryTxt.isEmpty()) {
            try {
                salary = Double.parseDouble(salaryTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Salary must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Employee emp = new Employee(ssn, salary, position, firstName, middle, lastName);

        try {
            employeeDAO.updateEmployee(emp);
            JOptionPane.showMessageDialog(this, "Employee updated.");
            clearForm();
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
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select an employee in the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ssn = tableModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete employee with SSN: " + ssn + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            employeeDAO.deleteEmployee(ssn);
            JOptionPane.showMessageDialog(this, "Employee deleted.");
            clearForm();
            loadEmployees();
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

            for (Employee e : list) {
                tableModel.addRow(new Object[] {
                        e.getEmpSSN(),
                        e.getSalary(),
                        e.getPosition(),
                        e.getFirstName(),
                        e.getMiddleInitial(),
                        e.getLastName()
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

    private void clearForm() {
        ssnField.setText("");
        salaryField.setText("");
        positionField.setText("");
        firstNameField.setText("");
        middleInitialField.setText("");
        lastNameField.setText("");
        employeeTable.clearSelection();
    }
}
