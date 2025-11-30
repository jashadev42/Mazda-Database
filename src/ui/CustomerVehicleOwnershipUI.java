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

        // --------- Form panel ---------
        JPanel formPanel = new JPanel(new GridLayout(1, 4, 5, 5));

        vinField       = new JTextField();
        licenseIdField = new JTextField();

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Customer License ID:"));
        formPanel.add(licenseIdField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ---------
        tableModel = new DefaultTableModel(
                new Object[]{"VIN", "License_ID", "Owner First Name", "Owner Last Name", "Vehicle"},
                0
        );
        ownershipTable = new JTable(tableModel);
        add(new JScrollPane(ownershipTable), BorderLayout.CENTER);

        // --------- Buttons ---------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn  = new JButton("Add Ownership");
        JButton delBtn  = new JButton("Delete Ownership");
        JButton loadBtn = new JButton("Load All");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(delBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // --------- Actions ---------
        addBtn.addActionListener(e -> addOwnership());
        delBtn.addActionListener(e -> deleteOwnership());
        loadBtn.addActionListener(e -> loadOwnerships());
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
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        CustomerVehicleOwnership ownership =
                new CustomerVehicleOwnership(vin, licenseId);

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
                    "Select a row in the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vin       = tableModel.getValueAt(row, 0).toString();
        String licenseId = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete ownership for VIN " + vin + " and License ID " + licenseId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

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

    private void clearForm() {
        vinField.setText("");
        licenseIdField.setText("");
        ownershipTable.clearSelection();
    }
}
