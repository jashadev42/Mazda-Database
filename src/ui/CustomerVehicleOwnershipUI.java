package ui;

import dao.CustomerVehicleOwnershipDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.CustomerVehicleOwnership;

public class CustomerVehicleOwnershipUI extends JFrame {

    private JTextField vinField;
    private JTextField licenseIdField;

    private JTable ownershipTable;
    private DefaultTableModel tableModel;

    private CustomerVehicleOwnershipDAO ownershipDAO;

    public CustomerVehicleOwnershipUI(JFrame parent) {
        setTitle("Customer - Vehicle Ownership");
        setSize(800, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        ownershipDAO = new CustomerVehicleOwnershipDAO();

        // --------- Top form (filters + data entry) ---------
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 8, 8));

        vinField = new JTextField();
        licenseIdField = new JTextField();

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("License ID:"));
        formPanel.add(licenseIdField);

        // Fill remaining cells for spacing
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ---------
        tableModel = new DefaultTableModel(
                new Object[]{"VIN", "License_ID", "Owner First Name", "Owner Last Name", "Vehicle"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ownershipTable = new JTable(tableModel);
        add(new JScrollPane(ownershipTable), BorderLayout.CENTER);

        // --------- Buttons ---------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn    = new JButton("Add Ownership");
        JButton delBtn    = new JButton("Delete Ownership");
        JButton loadBtn   = new JButton("Load All");
        JButton searchBtn = new JButton("Search");
        JButton clearBtn  = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // --------- Actions ---------
        addBtn.addActionListener(e -> addOwnership());
        delBtn.addActionListener(e -> deleteOwnership());
        loadBtn.addActionListener(e -> loadOwnerships());
        searchBtn.addActionListener(e -> searchOwnerships());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form (VIN + License_ID only)
        ownershipTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = ownershipTable.getSelectedRow();
                if (row >= 0) {
                    vinField.setText(tableModel.getValueAt(row, 0).toString());
                    licenseIdField.setText(tableModel.getValueAt(row, 1).toString());
                }
            }
        });

        // Load all on open
        loadOwnerships();

        setVisible(true);
    }

    private void addOwnership() {
        String vin       = vinField.getText().trim();
        String licenseId = licenseIdField.getText().trim();

        if (vin.isEmpty() || licenseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "VIN and License ID are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CustomerVehicleOwnership ownership =
                new CustomerVehicleOwnership(vin, licenseId, null, null, null);

        try {
            ownershipDAO.addOwnership(ownership);
            JOptionPane.showMessageDialog(this, "Ownership record added.");
            clearForm();
            loadOwnerships();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding ownership: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOwnership() {
        int row = ownershipTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an ownership record to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vin       = tableModel.getValueAt(row, 0).toString();
        String licenseId = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete ownership for VIN: " + vin + " and License ID: " + licenseId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ownershipDAO.deleteOwnership(vin, licenseId);
            JOptionPane.showMessageDialog(this, "Ownership record deleted.");
            clearForm();
            loadOwnerships();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting ownership: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOwnerships() {
        try {
            List<CustomerVehicleOwnership> list = ownershipDAO.getAllOwnerships();
            tableModel.setRowCount(0);

            for (CustomerVehicleOwnership o : list) {
                tableModel.addRow(new Object[]{
                        o.getVin(),
                        o.getLicenseId(),
                        o.getOwnerFirstName(),
                        o.getOwnerLastName(),
                        o.getVehicleDescription()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading ownerships: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Search ownership records by any combination of VIN and License ID.
     * If both fields are empty, the user is prompted instead of running a query.
     */
    private void searchOwnerships() {
        String vinFilter       = vinField.getText().trim();
        String licenseIdFilter = licenseIdField.getText().trim();

        if (vinFilter.isEmpty() && licenseIdFilter.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter VIN and/or License ID to search.\n" +
                    "To view all records, click Load All.",
                    "No Search Criteria",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            List<CustomerVehicleOwnership> list =
                    ownershipDAO.searchOwnerships(vinFilter, licenseIdFilter);

            tableModel.setRowCount(0);

            for (CustomerVehicleOwnership o : list) {
                tableModel.addRow(new Object[]{
                        o.getVin(),
                        o.getLicenseId(),
                        o.getOwnerFirstName(),
                        o.getOwnerLastName(),
                        o.getVehicleDescription()
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No ownership records matched the given criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching ownerships: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        vinField.setText("");
        licenseIdField.setText("");
        ownershipTable.clearSelection();
    }
}
