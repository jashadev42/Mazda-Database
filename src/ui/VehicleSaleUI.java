package ui;

import dao.VehicleSaleDAO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.VehicleSale;

public class VehicleSaleUI extends JFrame {

    private final VehicleSaleDAO vehicleSaleDAO = new VehicleSaleDAO();

    // Form fields
    private JTextField saleIdField;
    private JTextField licenseIdField;
    private JTextField vinField;
    private JTextField empSsnField;
    private JTextField termsField;
    private JTextField finalPriceField;
    private JTextField saleDateField;   // yyyy-MM-dd
    private JTextField saleTypeField;

    // Table
    private JTable salesTable;
    private DefaultTableModel tableModel;

    private final JFrame parent;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public VehicleSaleUI(JFrame parent) {
        this.parent = parent;
        initializeUI();
        setLocationRelativeTo(parent);
    }

    public VehicleSaleUI() {
        this.parent = null;
        initializeUI();
        setLocationRelativeTo(null);
    }

    // -------------------------------------------------------------------------
    // UI setup
    // -------------------------------------------------------------------------

    private void initializeUI() {
        setTitle("Vehicle Sales / Financing");
        setSize(1100, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------- Top form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        saleIdField     = new JTextField();          // usually read-only for updates
        licenseIdField  = new JTextField();
        vinField        = new JTextField();
        empSsnField     = new JTextField();
        termsField      = new JTextField();
        finalPriceField = new JTextField();
        saleDateField   = new JTextField();          // yyyy-MM-dd
        saleTypeField   = new JTextField();

        formPanel.add(new JLabel("Sale ID:"));
        formPanel.add(saleIdField);
        formPanel.add(new JLabel("License ID:"));
        formPanel.add(licenseIdField);

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Employee SSN:"));
        formPanel.add(empSsnField);

        formPanel.add(new JLabel("Terms:"));
        formPanel.add(termsField);
        formPanel.add(new JLabel("Final Price:"));
        formPanel.add(finalPriceField);

        formPanel.add(new JLabel("Sale Date (yyyy-MM-dd):"));
        formPanel.add(saleDateField);
        formPanel.add(new JLabel("Sale Type:"));
        formPanel.add(saleTypeField);

        add(formPanel, BorderLayout.NORTH);

        // ---------- Table ----------
        tableModel = new DefaultTableModel(new Object[]{
                "Sale ID", "License ID", "VIN", "Employee SSN",
                "Terms", "Final Price", "Sale Date", "Sale Type",
                "Customer First", "Customer Last",
                "Employee First", "Employee Last",
                "Vehicle"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is read-only
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                if (row >= 0) {
                    populateFormFromTable(row);
                }
            }
        });

        add(new JScrollPane(salesTable), BorderLayout.CENTER);

        // ---------- Buttons ----------
        JPanel buttonPanel = new JPanel();
        JButton addBtn     = new JButton("Add");
        JButton updateBtn  = new JButton("Update");
        JButton deleteBtn  = new JButton("Delete");
        JButton loadAllBtn = new JButton("Load All");
        JButton searchBtn  = new JButton("Search");
        JButton clearBtn   = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(loadAllBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> addSale());
        updateBtn.addActionListener(e -> updateSale());
        deleteBtn.addActionListener(e -> deleteSale());
        loadAllBtn.addActionListener(e -> loadSales());
        searchBtn.addActionListener(e -> searchSales());
        clearBtn.addActionListener(e -> clearForm());

        // Initial load
        loadSales();

        setVisible(true);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void populateFormFromTable(int row) {
        saleIdField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        licenseIdField.setText((String) tableModel.getValueAt(row, 1));
        vinField.setText((String) tableModel.getValueAt(row, 2));
        empSsnField.setText((String) tableModel.getValueAt(row, 3));
        termsField.setText((String) tableModel.getValueAt(row, 4));
        Object priceObj = tableModel.getValueAt(row, 5);
        finalPriceField.setText(priceObj == null ? "" : priceObj.toString());
        saleDateField.setText((String) tableModel.getValueAt(row, 6));
        saleTypeField.setText((String) tableModel.getValueAt(row, 7));
    }

    private void clearForm() {
        saleIdField.setText("");
        licenseIdField.setText("");
        vinField.setText("");
        empSsnField.setText("");
        termsField.setText("");
        finalPriceField.setText("");
        saleDateField.setText("");
        saleTypeField.setText("");
        salesTable.clearSelection();
    }

    private VehicleSale buildSaleFromForm() {
        Integer saleId = null;
        String saleIdText = saleIdField.getText().trim();
        if (!saleIdText.isEmpty()) {
            try {
                saleId = Integer.parseInt(saleIdText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Sale ID must be a number.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        Double finalPrice = null;
        String priceText = finalPriceField.getText().trim();
        if (!priceText.isEmpty()) {
            try {
                finalPrice = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Final price must be numeric.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }

        return new VehicleSale(
                saleId,
                licenseIdField.getText().trim(),
                vinField.getText().trim(),
                empSsnField.getText().trim(),
                termsField.getText().trim(),
                finalPrice,
                saleDateField.getText().trim(),  // yyyy-MM-dd string
                saleTypeField.getText().trim()
        );
    }

    private void addSale() {
        VehicleSale sale = buildSaleFromForm();
        if (sale == null) return;

        try {
            vehicleSaleDAO.addVehicleSale(sale);
            JOptionPane.showMessageDialog(this,
                    "Vehicle sale added successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSales();
            clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding sale: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSale() {
        VehicleSale sale = buildSaleFromForm();
        if (sale == null) return;

        if (sale.getSaleId() == null) {
            JOptionPane.showMessageDialog(this,
                    "Select a row or enter a Sale ID to update.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            vehicleSaleDAO.updateVehicleSale(sale);
            JOptionPane.showMessageDialog(this,
                    "Vehicle sale updated successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSales();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating sale: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSale() {
        String saleIdText = saleIdField.getText().trim();
        if (saleIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter or select a Sale ID to delete.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this sale?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int saleId = Integer.parseInt(saleIdText);
            vehicleSaleDAO.deleteVehicleSale(saleId);
            JOptionPane.showMessageDialog(this,
                    "Vehicle sale deleted successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSales();
            clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Sale ID must be a number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting sale: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSales() {
        tableModel.setRowCount(0);
        try {
            List<VehicleSale> sales = vehicleSaleDAO.getAllVehicleSales();
            for (VehicleSale s : sales) {
                addRowToTable(s);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading sales: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRowToTable(VehicleSale s) {
        tableModel.addRow(new Object[]{
                s.getSaleId(),
                s.getLicenseId(),
                s.getVin(),
                s.getEmpSSN(),                // <-- matches model
                s.getTerms(),
                s.getFinalPrice(),
                s.getSaleDate(),
                s.getSaleType(),
                s.getCustomerFirstName(),
                s.getCustomerLastName(),
                s.getEmployeeFirstName(),
                s.getEmployeeLastName(),
                s.getVehicleDescription()
        });
    }

    /**
     * Search using the same text fields.
     * For price & date we treat the single field as "exact":
     *  - finalPriceField -> min & max
     *  - saleDateField   -> start & end
     */
    private void searchSales() {
        String saleId    = saleIdField.getText().trim();
        String licenseId = licenseIdField.getText().trim();
        String vin       = vinField.getText().trim();
        String empSsn    = empSsnField.getText().trim();
        String saleType  = saleTypeField.getText().trim();

        String priceText = finalPriceField.getText().trim();
        String minPrice  = priceText.isEmpty() ? null : priceText;
        String maxPrice  = priceText.isEmpty() ? null : priceText;

        String dateText  = saleDateField.getText().trim();
        String startDate = dateText.isEmpty() ? null : dateText;
        String endDate   = dateText.isEmpty() ? null : dateText;

        tableModel.setRowCount(0);

        try {
            List<VehicleSale> results = vehicleSaleDAO.searchVehicleSales(
                    saleId,
                    licenseId,
                    vin,
                    empSsn,
                    saleType,
                    minPrice,
                    maxPrice,
                    startDate,
                    endDate
            );

            for (VehicleSale s : results) {
                addRowToTable(s);
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No sales matched your search criteria.",
                        "Search", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching sales: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
