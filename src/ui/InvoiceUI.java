package ui;

import dao.InvoiceDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Invoice;

public class InvoiceUI extends JFrame {

    private JTextField invoiceIdField;
    private JTextField empSSNField;
    private JTextField licenseIdField;
    private JTextField amountField;
    private JTextField purposeField;
    private JTextField dateField; // yyyy-MM-dd

    private JTable invoiceTable;
    private DefaultTableModel tableModel;

    private InvoiceDAO invoiceDAO;

    public InvoiceUI(JFrame parent) {
        setTitle("Invoices");
        setSize(900, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        invoiceDAO = new InvoiceDAO();

        // --------- Form panel ---------
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        invoiceIdField = new JTextField();
        invoiceIdField.setEditable(false); // auto-generated

        empSSNField    = new JTextField();
        licenseIdField = new JTextField();
        amountField    = new JTextField();
        purposeField   = new JTextField();
        dateField      = new JTextField(); // yyyy-MM-dd

        formPanel.add(new JLabel("Invoice ID (auto):"));
        formPanel.add(invoiceIdField);
        formPanel.add(new JLabel("Employee SSN:"));
        formPanel.add(empSSNField);

        formPanel.add(new JLabel("Customer License ID:"));
        formPanel.add(licenseIdField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);

        formPanel.add(new JLabel("Purpose:"));
        formPanel.add(purposeField);
        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        formPanel.add(dateField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ---------
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Invoice_ID",
                        "Emp_SSN",
                        "Employee Name",
                        "License_ID",
                        "Customer Name",
                        "Amount",
                        "Purpose",
                        "Date"
                },
                0
        );
        invoiceTable = new JTable(tableModel);
        add(new JScrollPane(invoiceTable), BorderLayout.CENTER);

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
        addBtn.addActionListener(e -> addInvoice());
        updateBtn.addActionListener(e -> updateInvoice());
        deleteBtn.addActionListener(e -> deleteInvoice());
        loadBtn.addActionListener(e -> loadInvoices());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = invoiceTable.getSelectedRow();
                if (row >= 0) {
                    invoiceIdField.setText(toStr(tableModel.getValueAt(row, 0)));
                    empSSNField.setText(toStr(tableModel.getValueAt(row, 1)));
                    licenseIdField.setText(toStr(tableModel.getValueAt(row, 3)));
                    amountField.setText(toStr(tableModel.getValueAt(row, 5)));
                    purposeField.setText(toStr(tableModel.getValueAt(row, 6)));
                    dateField.setText(toStr(tableModel.getValueAt(row, 7)));
                }
            }
        });

        // Load on open
        loadInvoices();

        setVisible(true);
    }

    private String toStr(Object value) {
        return value == null ? "" : value.toString();
    }

    private void addInvoice() {
        String empSSN    = empSSNField.getText().trim();
        String licenseId = licenseIdField.getText().trim();
        String amountTxt = amountField.getText().trim();
        String purpose   = purposeField.getText().trim();
        String dateStr   = dateField.getText().trim();

        if (empSSN.isEmpty() || licenseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Employee SSN and Customer License ID are required.",
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

        Invoice invoice = new Invoice(
                null,
                empSSN,
                licenseId,
                amount,
                purpose,
                dateStr.isEmpty() ? null : dateStr
        );

        try {
            invoiceDAO.addInvoice(invoice);
            JOptionPane.showMessageDialog(this, "Invoice added.");
            clearForm();
            loadInvoices();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding invoice: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateInvoice() {
        String invoiceIdTxt = invoiceIdField.getText().trim();
        if (invoiceIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select an invoice from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceId  = Integer.parseInt(invoiceIdTxt);
        String empSSN  = empSSNField.getText().trim();
        String licenseId = licenseIdField.getText().trim();
        String amountTxt = amountField.getText().trim();
        String purpose   = purposeField.getText().trim();
        String dateStr   = dateField.getText().trim();

        if (empSSN.isEmpty() || licenseId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Employee SSN and Customer License ID are required.",
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

        Invoice invoice = new Invoice(
                invoiceId,
                empSSN,
                licenseId,
                amount,
                purpose,
                dateStr.isEmpty() ? null : dateStr
        );

        try {
            invoiceDAO.updateInvoice(invoice);
            JOptionPane.showMessageDialog(this, "Invoice updated.");
            clearForm();
            loadInvoices();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating invoice: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteInvoice() {
        String invoiceIdTxt = invoiceIdField.getText().trim();
        if (invoiceIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select an invoice from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceId = Integer.parseInt(invoiceIdTxt);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Invoice_ID: " + invoiceId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            invoiceDAO.deleteInvoice(invoiceId);
            JOptionPane.showMessageDialog(this, "Invoice deleted.");
            clearForm();
            loadInvoices();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting invoice: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInvoices() {
        try {
            List<Invoice> list = invoiceDAO.getAllInvoices();
            tableModel.setRowCount(0);

            for (Invoice inv : list) {
                String employeeName = ((inv.getEmployeeFirstName() != null) ? inv.getEmployeeFirstName() : "")
                        + " "
                        + ((inv.getEmployeeLastName() != null) ? inv.getEmployeeLastName() : "");

                String customerName = ((inv.getCustomerFirstName() != null) ? inv.getCustomerFirstName() : "")
                        + " "
                        + ((inv.getCustomerLastName() != null) ? inv.getCustomerLastName() : "");

                tableModel.addRow(new Object[]{
                        inv.getInvoiceId(),
                        inv.getEmpSSN(),
                        employeeName.trim(),
                        inv.getLicenseId(),
                        customerName.trim(),
                        inv.getAmount(),
                        inv.getPurpose(),
                        inv.getDate()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading invoices: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        invoiceIdField.setText("");
        empSSNField.setText("");
        licenseIdField.setText("");
        amountField.setText("");
        purposeField.setText("");
        dateField.setText("");
        invoiceTable.clearSelection();
    }
}
