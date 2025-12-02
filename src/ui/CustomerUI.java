package ui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerUI extends JFrame {

    private final CustomerDAO customerDAO;

    private JTextField licenseIdField;
    private JTextField firstNameField;
    private JTextField middleInitialField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField emailField;
    private JTextField phoneField;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Default constructor
    public CustomerUI() {
        this.customerDAO = new CustomerDAO();
        initializeUI();
    }

    // Overloaded constructor so MainMenuUI can call new CustomerUI(this)
    public CustomerUI(MainMenuUI parent) {
        this(); // just delegate to the default constructor
    }

    private void initializeUI() {
        setTitle("Manage Customers");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // ===== Top Form Panel =====
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 8, 8));

        licenseIdField     = new JTextField();
        firstNameField     = new JTextField();
        middleInitialField = new JTextField();
        lastNameField      = new JTextField();
        addressField       = new JTextField();
        emailField         = new JTextField();
        phoneField         = new JTextField();

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
        // pad the grid
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.NORTH);

        // ===== Center Table =====
        tableModel = new DefaultTableModel(
                new Object[]{"License ID", "First Name", "M.I.", "Last Name", "Address", "Email", "Phone"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is read-only; edit via form
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When user selects a row, load data into form
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomerIntoForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Bottom Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton    = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton loadButton   = new JButton("Load All");
        JButton searchButton = new JButton("Search");
        JButton clearButton  = new JButton("Clear");

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        loadButton.addActionListener(e -> loadCustomers());
        searchButton.addActionListener(e -> searchCustomers());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load all customers initially
        loadCustomers();

        // Make the window actually appear
        setVisible(true);
    }

    private Customer buildCustomerFromForm() {
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

        return new Customer(
                licenseId,
                firstName,
                middleInitial,
                lastName,
                address,
                email,
                phone
        );
    }

    private void addCustomer() {
        Customer customer = buildCustomerFromForm();
        if (customer == null) {
            return;
        }

        try {
            customerDAO.addCustomer(customer);
            JOptionPane.showMessageDialog(this,
                    "Customer added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadCustomers();
            clearForm();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding customer: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        Customer customer = buildCustomerFromForm();
        if (customer == null) {
            return;
        }

        try {
            customerDAO.updateCustomer(customer);
            JOptionPane.showMessageDialog(this,
                    "Customer updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
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
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String licenseId = (String) tableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete customer with License ID: " + licenseId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            customerDAO.deleteCustomer(licenseId);
            JOptionPane.showMessageDialog(this,
                    "Customer deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadCustomers();
            clearForm();
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
                tableModel.addRow(new Object[]{
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

    /**
     * Search customers using any combination of the fields above.
     * Only non-empty fields are used as filters.
     */
    private void searchCustomers() {
        String licenseId     = licenseIdField.getText().trim();
        String firstName     = firstNameField.getText().trim();
        String middleInitial = middleInitialField.getText().trim();
        String lastName      = lastNameField.getText().trim();
        String address       = addressField.getText().trim();
        String email         = emailField.getText().trim();
        String phone         = phoneField.getText().trim();

        if (licenseId.isEmpty() && firstName.isEmpty() && middleInitial.isEmpty()
                && lastName.isEmpty() && address.isEmpty()
                && email.isEmpty() && phone.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Please enter at least one field to search.\n" +
                            "To see all customers, click Load All.",
                    "No Search Criteria",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            List<Customer> customers = customerDAO.searchCustomers(
                    licenseId, firstName, middleInitial,
                    lastName, address, email, phone
            );

            tableModel.setRowCount(0);
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                        c.getLicenseId(),
                        c.getFirstName(),
                        c.getMiddleInitial(),
                        c.getLastName(),
                        c.getAddress(),
                        c.getEmail(),
                        c.getPhone()
                });
            }

            if (customers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No customers matched the given criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching customers: " + ex.getMessage(),
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

    private void loadSelectedCustomerIntoForm() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        licenseIdField.setText((String) tableModel.getValueAt(selectedRow, 0));
        firstNameField.setText((String) tableModel.getValueAt(selectedRow, 1));
        middleInitialField.setText((String) tableModel.getValueAt(selectedRow, 2));
        lastNameField.setText((String) tableModel.getValueAt(selectedRow, 3));
        addressField.setText((String) tableModel.getValueAt(selectedRow, 4));
        emailField.setText((String) tableModel.getValueAt(selectedRow, 5));
        phoneField.setText((String) tableModel.getValueAt(selectedRow, 6));
    }
}
