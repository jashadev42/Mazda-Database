package ui;

import dao.VehicleSaleDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.VehicleSale;

public class VehicleSaleUI extends JFrame {

    private JTextField saleIdField;
    private JTextField licenseIdField;
    private JTextField vinField;
    private JTextField empSSNField;
    private JTextField termsField;
    private JTextField finalPriceField;
    private JTextField saleDateField; // yyyy-MM-dd
    private JTextField saleTypeField;

    private JTable salesTable;
    private DefaultTableModel tableModel;

    private VehicleSaleDAO saleDAO;

    public VehicleSaleUI(JFrame parent) {
        setTitle("Vehicle Sales / Financing");
        setSize(1000, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        saleDAO = new VehicleSaleDAO();

        // --------- Form panel ---------
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        saleIdField     = new JTextField();
        saleIdField.setEditable(false); // auto-generated
        licenseIdField  = new JTextField();
        vinField        = new JTextField();
        empSSNField     = new JTextField();
        termsField      = new JTextField();
        finalPriceField = new JTextField();
        saleDateField   = new JTextField(); // "2025-11-30"
        saleTypeField   = new JTextField(); // e.g. "Sale", "Lease"

        formPanel.add(new JLabel("Sale ID (auto):"));
        formPanel.add(saleIdField);
        formPanel.add(new JLabel("Customer License ID:"));
        formPanel.add(licenseIdField);

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Employee SSN:"));
        formPanel.add(empSSNField);

        formPanel.add(new JLabel("Terms:"));
        formPanel.add(termsField);
        formPanel.add(new JLabel("Final Price:"));
        formPanel.add(finalPriceField);

        formPanel.add(new JLabel("Sale Date (yyyy-MM-dd):"));
        formPanel.add(saleDateField);
        formPanel.add(new JLabel("Sale Type:"));
        formPanel.add(saleTypeField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ---------
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Sale_ID",
                        "License_ID",
                        "Customer Name",
                        "VIN",
                        "Vehicle",
                        "Emp_SSN",
                        "Employee Name",
                        "Terms",
                        "Final Price",
                        "Sale Date",
                        "Sale Type"
                },
                0
        );
        salesTable = new JTable(tableModel);
        add(new JScrollPane(salesTable), BorderLayout.CENTER);

        // --------- Buttons ---------
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

        // --------- Actions ---------
        addBtn.addActionListener(e -> addSale());
        updateBtn.addActionListener(e -> updateSale());
        deleteBtn.addActionListener(e -> deleteSale());
        loadBtn.addActionListener(e -> loadSales());
        clearBtn.addActionListener(e -> clearForm());
        
        // When a row is selected, fill the form
        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = salesTable.getSelectedRow();
                if (row >= 0) {
                    saleIdField.setText(toStr(tableModel.getValueAt(row, 0)));
                    licenseIdField.setText(toStr(tableModel.getValueAt(row, 1)));
                    vinField.setText(toStr(tableModel.getValueAt(row, 3)));
                    empSSNField.setText(toStr(tableModel.getValueAt(row, 5)));
                    termsField.setText(toStr(tableModel.getValueAt(row, 7)));
                    finalPriceField.setText(toStr(tableModel.getValueAt(row, 8)));
                    saleDateField.setText(toStr(tableModel.getValueAt(row, 9)));
                    saleTypeField.setText(toStr(tableModel.getValueAt(row, 10)));
                }
            }
        });

        // Load on open
        loadSales();

        setVisible(true);
    }

    private String toStr(Object value) {
        return value == null ? "" : value.toString();
    }

    private void addSale() {
        String licenseId = licenseIdField.getText().trim();
        String vin       = vinField.getText().trim();
        String empSSN    = empSSNField.getText().trim();
        String terms     = termsField.getText().trim();
        String finalPriceTxt = finalPriceField.getText().trim();
        String saleDate  = saleDateField.getText().trim();
        String saleType  = saleTypeField.getText().trim();

        if (licenseId.isEmpty() || vin.isEmpty() || empSSN.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "License ID, VIN, and Employee SSN are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double finalPrice = null;
        if (!finalPriceTxt.isEmpty()) {
            try {
                finalPrice = Double.parseDouble(finalPriceTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Final Price must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        VehicleSale sale = new VehicleSale(
                null,
                licenseId,
                vin,
                empSSN,
                terms,
                finalPrice,
                saleDate.isEmpty() ? null : saleDate,
                saleType
        );

        try {
            saleDAO.addSale(sale);
            JOptionPane.showMessageDialog(this, "Sale record added.");
            clearForm();
            loadSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSale() {
        String saleIdTxt = saleIdField.getText().trim();
        if (saleIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a sale record from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int saleId = Integer.parseInt(saleIdTxt);

        String licenseId = licenseIdField.getText().trim();
        String vin       = vinField.getText().trim();
        String empSSN    = empSSNField.getText().trim();
        String terms     = termsField.getText().trim();
        String finalPriceTxt = finalPriceField.getText().trim();
        String saleDate  = saleDateField.getText().trim();
        String saleType  = saleTypeField.getText().trim();

        if (licenseId.isEmpty() || vin.isEmpty() || empSSN.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "License ID, VIN, and Employee SSN are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double finalPrice = null;
        if (!finalPriceTxt.isEmpty()) {
            try {
                finalPrice = Double.parseDouble(finalPriceTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Final Price must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        VehicleSale sale = new VehicleSale(
                saleId,
                licenseId,
                vin,
                empSSN,
                terms,
                finalPrice,
                saleDate.isEmpty() ? null : saleDate,
                saleType
        );

        try {
            saleDAO.updateSale(sale);
            JOptionPane.showMessageDialog(this, "Sale record updated.");
            clearForm();
            loadSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSale() {
        String saleIdTxt = saleIdField.getText().trim();
        if (saleIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a sale record from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int saleId = Integer.parseInt(saleIdTxt);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Sale_ID: " + saleId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            saleDAO.deleteSale(saleId);
            JOptionPane.showMessageDialog(this, "Sale record deleted.");
            clearForm();
            loadSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSales() {
        try {
            List<VehicleSale> list = saleDAO.getAllSales();
            tableModel.setRowCount(0);

            for (VehicleSale s : list) {
                String customerName = (s.getCustomerFirstName() != null ? s.getCustomerFirstName() : "")
                        + " "
                        + (s.getCustomerLastName() != null ? s.getCustomerLastName() : "");

                String employeeName = (s.getEmployeeFirstName() != null ? s.getEmployeeFirstName() : "")
                        + " "
                        + (s.getEmployeeLastName() != null ? s.getEmployeeLastName() : "");

                tableModel.addRow(new Object[]{
                        s.getSaleId(),
                        s.getLicenseId(),
                        customerName.trim(),
                        s.getVin(),
                        s.getVehicleDescription(),
                        s.getEmpSSN(),
                        employeeName.trim(),
                        s.getTerms(),
                        s.getFinalPrice(),
                        s.getSaleDate(),
                        s.getSaleType()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading sales: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        saleIdField.setText("");
        licenseIdField.setText("");
        vinField.setText("");
        empSSNField.setText("");
        termsField.setText("");
        finalPriceField.setText("");
        saleDateField.setText("");
        saleTypeField.setText("");
        salesTable.clearSelection();
    }
}
