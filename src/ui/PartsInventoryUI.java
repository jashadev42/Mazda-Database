package ui;

import dao.PartsInventoryDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Part;

public class PartsInventoryUI extends JFrame {

    private JTextField partIdField;
    private JTextField partNameField;
    private JTextField countField;
    private JTextField unitPriceField;

    private JTable partsTable;
    private DefaultTableModel tableModel;

    private PartsInventoryDAO partsDAO;

    public PartsInventoryUI(JFrame parent) {
        setTitle("Parts Inventory");
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        partsDAO = new PartsInventoryDAO();

        // --------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        partIdField    = new JTextField();
        partNameField  = new JTextField();
        countField     = new JTextField();
        unitPriceField = new JTextField();

        formPanel.add(new JLabel("Part ID:"));
        formPanel.add(partIdField);
        formPanel.add(new JLabel("Part Name:"));
        formPanel.add(partNameField);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(countField);
        formPanel.add(new JLabel("Unit Price:"));
        formPanel.add(unitPriceField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"Part_ID", "Part_Name", "Quantity", "Unit_Price"},
                0
        );
        partsTable = new JTable(tableModel);
        add(new JScrollPane(partsTable), BorderLayout.CENTER);

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
        addBtn.addActionListener(e -> addPart());
        updateBtn.addActionListener(e -> updatePart());
        deleteBtn.addActionListener(e -> deletePart());
        loadBtn.addActionListener(e -> loadParts());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form
        partsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = partsTable.getSelectedRow();
                if (row >= 0) {
                    partIdField.setText(tableModel.getValueAt(row, 0).toString());
                    partNameField.setText(tableModel.getValueAt(row, 1).toString());
                    countField.setText(tableModel.getValueAt(row, 2).toString());
                    unitPriceField.setText(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        // Optionally load on open
        loadParts();

        setVisible(true);
    }

    private void addPart() {
        String partId   = partIdField.getText().trim();
        String partName = partNameField.getText().trim();
        String qtyText  = countField.getText().trim();
        String priceText = unitPriceField.getText().trim();

        if (partId.isEmpty() || partName.isEmpty() ||
            qtyText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Part ID, Part Name, Quantity, and Unit Price are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        double unitPrice;
        try {
            quantity = Integer.parseInt(qtyText);
            unitPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be an integer and Unit Price must be a number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Part p = new Part(partId, partName, quantity, unitPrice);

        try {
            partsDAO.addPart(p);
            JOptionPane.showMessageDialog(this, "Part added.");
            clearForm();
            loadParts();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding part: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePart() {
        int selectedRow = partsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a part from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String partId   = partIdField.getText().trim();
        String partName = partNameField.getText().trim();
        String qtyText  = countField.getText().trim();
        String priceText = unitPriceField.getText().trim();

        if (partId.isEmpty() || partName.isEmpty() ||
            qtyText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Part ID, Part Name, Quantity, and Unit Price are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity;
        double unitPrice;
        try {
            quantity = Integer.parseInt(qtyText);
            unitPrice = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be an integer and Unit Price must be a number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Part p = new Part(partId, partName, quantity, unitPrice);

        try {
            partsDAO.updatePart(p);
            JOptionPane.showMessageDialog(this, "Part updated.");
            clearForm();
            loadParts();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating part: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePart() {
        int selectedRow = partsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a part from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String partId = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete part: " + partId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            partsDAO.deletePart(partId);
            JOptionPane.showMessageDialog(this, "Part deleted.");
            clearForm();
            loadParts();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting part: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadParts() {
        try {
            List<Part> parts = partsDAO.getAllParts();

            tableModel.setRowCount(0);

            for (Part p : parts) {
                tableModel.addRow(new Object[]{
                        p.getPartId(),
                        p.getPartName(),
                        p.getQuantity(),
                        p.getUnitPrice()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading parts: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        partIdField.setText("");
        partNameField.setText("");
        countField.setText("");
        unitPriceField.setText("");
        partsTable.clearSelection();
    }
}
