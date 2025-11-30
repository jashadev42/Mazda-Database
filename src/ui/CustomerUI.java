package ui;

import dao.CustomerDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Customer;

public class CustomerUI extends JFrame {

    private JTextField licenseIdField;
    private JTextField firstNameField;
    private JTextField middleInitialField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField emailField;
    private JTextField phoneField;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    private CustomerDAO customerDAO;

    public CustomerUI(JFrame parent) {
        setTitle("Manage Customers");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        customerDAO = new CustomerDAO();

        // ---------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        licenseIdField      = new JTextField();
        firstNameField      = new JTextField();
        middleInitialField  = new JTextField();
        lastNameField       = new JTextField();
        addressField        = new JTextField();
        emailField          = new JTextField();
        phoneField          = new JTextField();

        formPanel.add(new JLabel("License ID:"));
        formPanel.add(licenseIdField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Middle Initial:"));
        formPanel.add(middleInitialField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        add(formPanel, BorderLayout.NORTH);

        // ---------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[] {
                        "License_ID",
                        "First_Name",
                        "Middle_Initial",
                        "Last_Name",
                        "Address",
                        "Email",
                        "Phone"
                }, 0
        );
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // ---------- Buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton addButton    = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton loadButton   = new JButton("Load All");
        JButton clearButton  = new JButton("Clear");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ---------- Button actions ----------
        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        loadButton.addActionListener(e -> loadCustomers());
        clearButton.addActionListener(e -> clearForm());

        // When a row is clicked, populate the form for editing
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = customerTable.getSelectedRow();
                if (row >= 0) {
                    licenseIdField.setText(valueAt(row, 0));
                    firstNameField.setText(valueAt(row, 1));
                    middleInitialField.setText(valueAt(row, 2));
                    lastNameField.setText(valueAt(row, 3));
                    addressField.setText(valueAt(row, 4));
                    emailField.setText(valueAt(row, 5));
                    phoneField.setText(valueAt(row, 6));
                }
            }
        });

        // Load initial data
        loadCustomers();

        setVisible(true);
    }

    private String valueAt(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    // ---------- CRUD helpers ----------

    private void addCustomer() {
        Customer c = buildCustomerFromForm(true);
        if (c == null) return;

        try {
            customerDAO.addCustomer(c);
            JOptionPane.showMessageDialog(this, "Customer added.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding customer: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        Customer c = buildCustomerFromForm(false);
        if (c == null) return;

        try {
            customerDAO.updateCustomer(c);
            JOptionPane.showMessageDialog(this, "Customer updated.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating customer: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        String licenseId = licenseIdField.getText().trim();

        if (licenseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter or select a License ID to delete.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete customer with License_ID: " + licenseId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            customerDAO.deleteCustomer(licenseId);
            JOptionPane.showMessageDialog(this, "Customer deleted.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting customer: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            tableModel.setRowCount(0);

            for (Customer c : customers) {
                tableModel.addRow(new Object[] {
                        c.getLicenseId(),
                        c.getFirstName(),
                        c.getMiddleInitial(),
                        c.getLastName(),
                        c.getAddress(),
                        c.getEmail(),
                        c.getPhone()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading customers: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        licenseIdField.setText("");
        firstNameField.setText("");
        middleInitialField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        emailField.setText("");
        phoneField.setText("");
        customerTable.clearSelection();
    }

    /**
     * Build a Customer from form fields.
     * If isAdd is true, we require License_ID to be non-empty (PK).
     */
    private Customer buildCustomerFromForm(boolean isAdd) {
        String licenseId     = licenseIdField.getText().trim();
        String firstName     = firstNameField.getText().trim();
        String middleInitial = middleInitialField.getText().trim();
        String lastName      = lastNameField.getText().trim();
        String address       = addressField.getText().trim();
        String email         = emailField.getText().trim();
        String phone         = phoneField.getText().trim();

        if (licenseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "License ID is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "First Name and Last Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        // Middle initial can be null/blank; DB allows CHAR(1) but can accept empty as null if you want.
        if (middleInitial.length() > 1) {
            JOptionPane.showMessageDialog(this,
                    "Middle Initial must be at most 1 character.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Customer(
                licenseId,
                firstName,
                middleInitial.isEmpty() ? null : middleInitial,
                lastName,
                address,
                email,
                phone
        );
    }
}
