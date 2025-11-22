package ui;

import dao.CustomerDAO;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerUI extends JFrame {

    private JTextField licenseField;
    private JTextField firstNameField;
    private JTextField middleInitialField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField emailField;
    private JTextField phoneField;

    private JTable customerTable;
    private DefaultTableModel tableModel;

    private CustomerDAO customerDAO;  // <--- new

    public CustomerUI(JFrame parent) {
        setTitle("Manage Customers");
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // init DAO
        customerDAO = new CustomerDAO();

        // --------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        licenseField       = new JTextField();
        firstNameField     = new JTextField();
        middleInitialField = new JTextField();
        lastNameField      = new JTextField();
        addressField       = new JTextField();
        emailField         = new JTextField();
        phoneField         = new JTextField();

        formPanel.add(new JLabel("License ID:"));
        formPanel.add(licenseField);
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
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"License_ID", "First Name", "Last Name", "Email", "Phone"},
                0
        );
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // --------- Buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn    = new JButton("Add");
        JButton loadBtn   = new JButton("Load All");
        // you can add update/delete later

        buttonPanel.add(addBtn);
        buttonPanel.add(loadBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // ----- Button actions -----

        addBtn.addActionListener(e -> addCustomer());
        loadBtn.addActionListener(e -> loadCustomers());

        setVisible(true);
    }

    private void addCustomer() {
        String license = licenseField.getText().trim();
        String first   = firstNameField.getText().trim();
        String middle  = middleInitialField.getText().trim();
        String last    = lastNameField.getText().trim();
        String addr    = addressField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();

        if (license.isEmpty() || first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "License ID, First Name, and Last Name are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer c = new Customer(license, first, middle, last, addr, email, phone);

        try {
            customerDAO.addCustomer(c);
            JOptionPane.showMessageDialog(this, "Customer added.");
            clearForm();
            loadCustomers();   // refresh table
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding customer: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();

            // clear table
            tableModel.setRowCount(0);

            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                        c.getLicenseId(),
                        c.getFirstName(),
                        c.getLastName(),
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
        licenseField.setText("");
        firstNameField.setText("");
        middleInitialField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        emailField.setText("");
        phoneField.setText("");
    }
}
