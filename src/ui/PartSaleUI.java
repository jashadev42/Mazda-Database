package ui;

import dao.PartSaleDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.PartSale;

public class PartSaleUI extends JFrame {

    private JTextField partSaleIdField;
    private JTextField licenseIdField;
    private JTextField partIdField;
    private JTextField empSSNField;
    private JTextField countField;
    private JTextField amountField;
    private JTextField saleDateField; // yyyy-MM-dd

    private JTable salesTable;
    private DefaultTableModel tableModel;

    private PartSaleDAO partSaleDAO;

    public PartSaleUI(JFrame parent) {
        setTitle("Part Sales");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        partSaleDAO = new PartSaleDAO();

        // --------- Form panel ---------
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        partSaleIdField = new JTextField();
        partSaleIdField.setEditable(false); // auto-generated

        licenseIdField  = new JTextField();
        partIdField     = new JTextField();
        empSSNField     = new JTextField();
        countField      = new JTextField();
        amountField     = new JTextField();
        saleDateField   = new JTextField(); // yyyy-MM-dd

        formPanel.add(new JLabel("Part Sale ID (auto):"));
        formPanel.add(partSaleIdField);
        formPanel.add(new JLabel("Customer License ID:"));
        formPanel.add(licenseIdField);

        formPanel.add(new JLabel("Part ID:"));
        formPanel.add(partIdField);
        formPanel.add(new JLabel("Employee SSN:"));
        formPanel.add(empSSNField);

        formPanel.add(new JLabel("Count:"));
        formPanel.add(countField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);

        formPanel.add(new JLabel("Sale Date (yyyy-MM-dd):"));
        formPanel.add(saleDateField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ---------
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Part_Sale_ID",
                        "License_ID",
                        "Customer Name",
                        "Part_ID",
                        "Part Name",
                        "Emp_SSN",
                        "Employee Name",
                        "Count",
                        "Amount",
                        "Sale Date"
                },
                0
        );
        salesTable = new JTable(tableModel);
        add(new JScrollPane(salesTable), BorderLayout.CENTER);

        // --------- Buttons ---------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn      = new JButton("Add");
        JButton updateBtn   = new JButton("Update");
        JButton deleteBtn   = new JButton("Delete");
        JButton loadBtn     = new JButton("Load All");
        JButton completeBtn = new JButton("Complete Sale (Update Inventory)");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(completeBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // --------- Actions ---------
        addBtn.addActionListener(e -> addPartSale());
        updateBtn.addActionListener(e -> updatePartSale());
        deleteBtn.addActionListener(e -> deletePartSale());
        loadBtn.addActionListener(e -> loadPartSales());
        completeBtn.addActionListener(e -> completeSale());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form
        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = salesTable.getSelectedRow();
                if (row >= 0) {
                    partSaleIdField.setText(toStr(tableModel.getValueAt(row, 0)));
                    licenseIdField.setText(toStr(tableModel.getValueAt(row, 1)));
                    partIdField.setText(toStr(tableModel.getValueAt(row, 3)));
                    empSSNField.setText(toStr(tableModel.getValueAt(row, 5)));
                    countField.setText(toStr(tableModel.getValueAt(row, 7)));
                    amountField.setText(toStr(tableModel.getValueAt(row, 8)));
                    saleDateField.setText(toStr(tableModel.getValueAt(row, 9)));
                }
            }
        });

        // Load on open
        loadPartSales();

        setVisible(true);
    }

    private String toStr(Object v) {
        return v == null ? "" : v.toString();
    }

    private void addPartSale() {
        String licenseId = licenseIdField.getText().trim();
        String partId    = partIdField.getText().trim();
        String empSSN    = empSSNField.getText().trim();
        String countTxt  = countField.getText().trim();
        String amountTxt = amountField.getText().trim();
        String saleDate  = saleDateField.getText().trim();

        if (licenseId.isEmpty() || partId.isEmpty() || empSSN.isEmpty() || countTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Customer License ID, Part ID, Employee SSN, and Count are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer count;
        try {
            count = Integer.parseInt(countTxt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Count must be an integer.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double amount = null;
        if (!amountTxt.isEmpty()) {
            try {
                amount = Double.parseDouble(amountTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Amount must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        PartSale sale = new PartSale(
                null,
                licenseId,
                partId,
                empSSN,
                count,
                amount,
                saleDate.isEmpty() ? null : saleDate
        );

        try {
            partSaleDAO.addPartSale(sale);
            JOptionPane.showMessageDialog(this, "Part sale added.");
            clearForm();
            loadPartSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding part sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePartSale() {
        String partSaleIdTxt = partSaleIdField.getText().trim();
        if (partSaleIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a part sale from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int partSaleId = Integer.parseInt(partSaleIdTxt);

        String licenseId = licenseIdField.getText().trim();
        String partId    = partIdField.getText().trim();
        String empSSN    = empSSNField.getText().trim();
        String countTxt  = countField.getText().trim();
        String amountTxt = amountField.getText().trim();
        String saleDate  = saleDateField.getText().trim();

        if (licenseId.isEmpty() || partId.isEmpty() || empSSN.isEmpty() || countTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Customer License ID, Part ID, Employee SSN, and Count are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer count;
        try {
            count = Integer.parseInt(countTxt);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Count must be an integer.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double amount = null;
        if (!amountTxt.isEmpty()) {
            try {
                amount = Double.parseDouble(amountTxt);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Amount must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        PartSale sale = new PartSale(
                partSaleId,
                licenseId,
                partId,
                empSSN,
                count,
                amount,
                saleDate.isEmpty() ? null : saleDate
        );

        try {
            partSaleDAO.updatePartSale(sale);
            JOptionPane.showMessageDialog(this, "Part sale updated.");
            clearForm();
            loadPartSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating part sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePartSale() {
        String partSaleIdTxt = partSaleIdField.getText().trim();
        if (partSaleIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a part sale from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int partSaleId = Integer.parseInt(partSaleIdTxt);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Part_Sale_ID: " + partSaleId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            partSaleDAO.deletePartSale(partSaleId);
            JOptionPane.showMessageDialog(this, "Part sale deleted.");
            clearForm();
            loadPartSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting part sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completeSale() {
        String partSaleIdTxt = partSaleIdField.getText().trim();
        if (partSaleIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select a part sale to complete (update inventory).",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int partSaleId = Integer.parseInt(partSaleIdTxt);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this sale as complete and subtract its Count from inventory?\n" +
                "⚠ Doing this twice will subtract twice.",
                "Confirm Complete Sale",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            partSaleDAO.completeSale(partSaleId);
            JOptionPane.showMessageDialog(this, "Inventory updated for this sale.");
            loadPartSales();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error completing sale: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPartSales() {
        try {
            List<PartSale> list = partSaleDAO.getAllPartSales();
            tableModel.setRowCount(0);

            for (PartSale s : list) {
                String customerName = ((s.getCustomerFirstName() != null) ? s.getCustomerFirstName() : "")
                        + " "
                        + ((s.getCustomerLastName() != null) ? s.getCustomerLastName() : "");

                String employeeName = ((s.getEmployeeFirstName() != null) ? s.getEmployeeFirstName() : "")
                        + " "
                        + ((s.getEmployeeLastName() != null) ? s.getEmployeeLastName() : "");

                tableModel.addRow(new Object[]{
                        s.getPartSaleId(),
                        s.getLicenseId(),
                        customerName.trim(),
                        s.getPartId(),
                        s.getPartName(),
                        s.getEmpSSN(),
                        employeeName.trim(),
                        s.getCount(),
                        s.getAmount(),
                        s.getSaleDate()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading part sales: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        partSaleIdField.setText("");
        licenseIdField.setText("");
        partIdField.setText("");
        empSSNField.setText("");
        countField.setText("");
        amountField.setText("");
        saleDateField.setText("");
        salesTable.clearSelection();
    }
}
