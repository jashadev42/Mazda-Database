package ui;

import dao.OfferedVehicleDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.OfferedVehicle;

public class VehicleUI extends JFrame {

    private JTextField vinField;
    private JTextField makeField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField priceField;
    private JComboBox<String> statusCombo;

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    private OfferedVehicleDAO vehicleDAO;

    public VehicleUI(JFrame parent) {
        setTitle("Manage Offered Vehicles");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        vehicleDAO = new OfferedVehicleDAO();

        // --------- Top form ----------
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        vinField   = new JTextField();
        makeField  = new JTextField();
        modelField = new JTextField();
        yearField  = new JTextField();
        priceField = new JTextField();
        statusCombo = new JComboBox<>(new String[]{"Available", "Sold", "Pending"});

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Make:"));
        formPanel.add(makeField);

        formPanel.add(new JLabel("Model:"));
        formPanel.add(modelField);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);

        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusCombo);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"VIN", "Make", "Model", "Year", "Price", "Status"},
                0
        );
        vehicleTable = new JTable(tableModel);
        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // --------- Buttons ----------
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

        // --------- Button actions ----------
        addBtn.addActionListener(e -> addVehicle());
        updateBtn.addActionListener(e -> updateVehicle());
        deleteBtn.addActionListener(e -> deleteVehicle());
        loadBtn.addActionListener(e -> loadVehicles());
        clearBtn.addActionListener(e -> clearForm());

        // When clicking a row, fill the form (to make Update/Delete easier)
        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = vehicleTable.getSelectedRow();
                if (row >= 0) {
                    vinField.setText(tableModel.getValueAt(row, 0).toString());
                    makeField.setText(tableModel.getValueAt(row, 1).toString());
                    modelField.setText(tableModel.getValueAt(row, 2).toString());
                    yearField.setText(tableModel.getValueAt(row, 3).toString());
                    priceField.setText(tableModel.getValueAt(row, 4).toString());
                    statusCombo.setSelectedItem(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        // Optionally load on open
        loadVehicles();

        setVisible(true);
    }

    private void addVehicle() {
        String vin   = vinField.getText().trim();
        String make  = makeField.getText().trim();
        String model = modelField.getText().trim();
        String yearText  = yearField.getText().trim();
        String priceText = priceField.getText().trim();
        String status    = (String) statusCombo.getSelectedItem();

        if (vin.isEmpty() || make.isEmpty() || model.isEmpty()
                || yearText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "VIN, Make, Model, Year, and Price are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int year;
        double price;
        try {
            year = Integer.parseInt(yearText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Year must be an integer and Price must be a number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        OfferedVehicle v = new OfferedVehicle(vin, make, model, year, price, status);

        try {
            vehicleDAO.addVehicle(v);
            JOptionPane.showMessageDialog(this, "Vehicle added.");
            clearForm();
            loadVehicles();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding vehicle: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a vehicle from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vin   = vinField.getText().trim();
        String make  = makeField.getText().trim();
        String model = modelField.getText().trim();
        String yearText  = yearField.getText().trim();
        String priceText = priceField.getText().trim();
        String status    = (String) statusCombo.getSelectedItem();

        if (vin.isEmpty() || make.isEmpty() || model.isEmpty()
                || yearText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "VIN, Make, Model, Year, and Price are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int year;
        double price;
        try {
            year = Integer.parseInt(yearText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Year must be an integer and Price must be a number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        OfferedVehicle v = new OfferedVehicle(vin, make, model, year, price, status);

        try {
            vehicleDAO.updateVehicle(v);
            JOptionPane.showMessageDialog(this, "Vehicle updated.");
            clearForm();
            loadVehicles();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating vehicle: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a vehicle from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vin = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete vehicle with VIN: " + vin + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            vehicleDAO.deleteVehicle(vin);
            JOptionPane.showMessageDialog(this, "Vehicle deleted.");
            clearForm();
            loadVehicles();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting vehicle: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadVehicles() {
        try {
            List<OfferedVehicle> vehicles = vehicleDAO.getAllVehicles();

            tableModel.setRowCount(0);

            for (OfferedVehicle v : vehicles) {
                tableModel.addRow(new Object[]{
                        v.getVin(),
                        v.getMake(),
                        v.getModel(),
                        v.getYear(),
                        v.getPrice(),
                        v.getStatus()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading vehicles: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        vinField.setText("");
        makeField.setText("");
        modelField.setText("");
        yearField.setText("");
        priceField.setText("");
        statusCombo.setSelectedIndex(0);
        vehicleTable.clearSelection();
    }
}
