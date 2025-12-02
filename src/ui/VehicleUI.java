package ui;

import dao.OfferedVehicleDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.OfferedVehicle;

public class VehicleUI extends JFrame {

    private final OfferedVehicleDAO vehicleDAO;

    private JTextField vinField;
    private JTextField makeField;
    private JTextField modelField;
    private JTextField yearField;
    private JTextField priceField;
    private JTextField statusField;

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public VehicleUI() {
        this.vehicleDAO = new OfferedVehicleDAO();
        initializeUI();
    }

    // Called from MainMenuUI (new VehicleUI(this))
    public VehicleUI(MainMenuUI parent) {
        this();
    }

    private void initializeUI() {
        setTitle("Manage Vehicles");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== Top form =====
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        vinField   = new JTextField();
        makeField  = new JTextField();
        modelField = new JTextField();
        yearField  = new JTextField();
        priceField = new JTextField();
        statusField = new JTextField();

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
        formPanel.add(statusField);

        add(formPanel, BorderLayout.NORTH);

        // ===== Table =====
        tableModel = new DefaultTableModel(
                new Object[]{"VIN", "Make", "Model", "Year", "Price", "Status"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vehicleTable = new JTable(tableModel);
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedVehicleIntoForm();
            }
        });

        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton    = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton loadButton   = new JButton("Load All");
        JButton searchButton = new JButton("Search");
        JButton clearButton  = new JButton("Clear");

        addButton.addActionListener(e -> addVehicle());
        updateButton.addActionListener(e -> updateVehicle());
        deleteButton.addActionListener(e -> deleteVehicle());
        loadButton.addActionListener(e -> loadVehicles());
        searchButton.addActionListener(e -> searchVehicles());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initial data
        loadVehicles();

        setVisible(true);
    }

    // ===== Helpers =====

    private OfferedVehicle buildVehicleFromForm(boolean requireVinForUpdate) {
        String vin    = vinField.getText().trim();
        String make   = makeField.getText().trim();
        String model  = modelField.getText().trim();
        String yearTx = yearField.getText().trim();
        String priceTx = priceField.getText().trim();
        String status = statusField.getText().trim();

        if (requireVinForUpdate && vin.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "VIN is required for update.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (vin.isEmpty() || make.isEmpty() || model.isEmpty()
                || yearTx.isEmpty() || priceTx.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int year;
        try {
            year = Integer.parseInt(yearTx);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Year must be a valid integer.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double price;
        try {
            price = Double.parseDouble(priceTx);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new OfferedVehicle(vin, make, model, year, price, status);
    }

    private void addVehicle() {
        OfferedVehicle v = buildVehicleFromForm(false);
        if (v == null) return;

        try {
            vehicleDAO.addVehicle(v);
            JOptionPane.showMessageDialog(this,
                    "Vehicle added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadVehicles();
            clearForm();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding vehicle: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicle() {
        OfferedVehicle v = buildVehicleFromForm(true);
        if (v == null) return;

        try {
            vehicleDAO.updateVehicle(v);
            JOptionPane.showMessageDialog(this,
                    "Vehicle updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
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
        int row = vehicleTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a vehicle to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vin = tableModel.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete vehicle with VIN: " + vin + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            vehicleDAO.deleteVehicle(vin);
            JOptionPane.showMessageDialog(this,
                    "Vehicle deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadVehicles();
            clearForm();
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
            List<OfferedVehicle> list = vehicleDAO.getAllVehicles();
            tableModel.setRowCount(0);

            for (OfferedVehicle v : list) {
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

    private void searchVehicles() {
        String vin    = vinField.getText().trim();
        String make   = makeField.getText().trim();
        String model  = modelField.getText().trim();
        String yearTx = yearField.getText().trim();
        String priceTx = priceField.getText().trim();
        String status = statusField.getText().trim();

        if (vin.isEmpty() && make.isEmpty() && model.isEmpty()
                && yearTx.isEmpty() && priceTx.isEmpty() && status.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Enter at least one field to search.\n" +
                    "To see all vehicles, click Load All.",
                    "No Search Criteria",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Integer year = null;
        if (!yearTx.isEmpty()) {
            try {
                year = Integer.parseInt(yearTx);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Year must be a valid integer.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        Double price = null;
        if (!priceTx.isEmpty()) {
            try {
                price = Double.parseDouble(priceTx);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Price must be a valid number.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        try {
            List<OfferedVehicle> list = vehicleDAO.searchVehicles(
                    vin, make, model, year, price, status
            );

            tableModel.setRowCount(0);
            for (OfferedVehicle v : list) {
                tableModel.addRow(new Object[]{
                        v.getVin(),
                        v.getMake(),
                        v.getModel(),
                        v.getYear(),
                        v.getPrice(),
                        v.getStatus()
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No vehicles matched the given criteria.",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching vehicles: " + ex.getMessage(),
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
        statusField.setText("");
        vehicleTable.clearSelection();
    }

    private void loadSelectedVehicleIntoForm() {
        int row = vehicleTable.getSelectedRow();
        if (row < 0) return;

        vinField.setText(tableModel.getValueAt(row, 0).toString());
        makeField.setText(tableModel.getValueAt(row, 1).toString());
        modelField.setText(tableModel.getValueAt(row, 2).toString());
        yearField.setText(tableModel.getValueAt(row, 3).toString());
        priceField.setText(tableModel.getValueAt(row, 4).toString());
        statusField.setText(tableModel.getValueAt(row, 5).toString());
    }
}
