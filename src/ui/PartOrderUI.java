package ui;

import dao.PartOrderDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.PartOrder;

public class PartOrderUI extends JFrame {

    private JTextField orderIdField;
    private JTextField empSSNField;
    private JTextField partIdField;
    private JTextField countField;
    private JTextField orderDateField; // yyyy-MM-dd

    private JTable ordersTable;
    private DefaultTableModel tableModel;

    private PartOrderDAO partOrderDAO;

    public PartOrderUI(JFrame parent) {
        setTitle("Part Orders");
        setSize(950, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        partOrderDAO = new PartOrderDAO();

        // --------- Form panel ---------
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));

        orderIdField   = new JTextField();
        orderIdField.setEditable(false); // auto-generated

        empSSNField    = new JTextField();
        partIdField    = new JTextField();
        countField     = new JTextField();
        orderDateField = new JTextField(); // yyyy-MM-dd

        formPanel.add(new JLabel("Order ID (auto):"));
        formPanel.add(orderIdField);
        formPanel.add(new JLabel("Employee SSN:"));
        formPanel.add(empSSNField);

        formPanel.add(new JLabel("Part ID:"));
        formPanel.add(partIdField);
        formPanel.add(new JLabel("Count:"));
        formPanel.add(countField);

        formPanel.add(new JLabel("Order Date (yyyy-MM-dd):"));
        formPanel.add(orderDateField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ---------
        tableModel = new DefaultTableModel(
                new Object[]{
                        "Order_ID",
                        "Emp_SSN",
                        "Employee Name",
                        "Part_ID",
                        "Part Name",
                        "Count",
                        "Order Date"
                },
                0
        );
        ordersTable = new JTable(tableModel);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        // --------- Buttons ---------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn     = new JButton("Add");
        JButton updateBtn  = new JButton("Update");
        JButton deleteBtn  = new JButton("Delete");
        JButton loadBtn    = new JButton("Load All");
        JButton receiveBtn = new JButton("Receive (Update Inventory)");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(receiveBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // --------- Actions ---------
        addBtn.addActionListener(e -> addOrder());
        updateBtn.addActionListener(e -> updateOrder());
        deleteBtn.addActionListener(e -> deleteOrder());
        loadBtn.addActionListener(e -> loadOrders());
        receiveBtn.addActionListener(e -> receiveOrder());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = ordersTable.getSelectedRow();
                if (row >= 0) {
                    orderIdField.setText(toStr(tableModel.getValueAt(row, 0)));
                    empSSNField.setText(toStr(tableModel.getValueAt(row, 1)));
                    partIdField.setText(toStr(tableModel.getValueAt(row, 3)));
                    countField.setText(toStr(tableModel.getValueAt(row, 5)));
                    orderDateField.setText(toStr(tableModel.getValueAt(row, 6)));
                }
            }
        });

        // Load on open
        loadOrders();

        setVisible(true);
    }

    private String toStr(Object v) {
        return v == null ? "" : v.toString();
    }

    private void addOrder() {
        String empSSN   = empSSNField.getText().trim();
        String partId   = partIdField.getText().trim();
        String countTxt = countField.getText().trim();
        String dateStr  = orderDateField.getText().trim();

        if (empSSN.isEmpty() || partId.isEmpty() || countTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Employee SSN, Part ID, and Count are required.",
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

        PartOrder order = new PartOrder(
                null,
                empSSN,
                partId,
                count,
                dateStr.isEmpty() ? null : dateStr
        );

        try {
            partOrderDAO.addOrder(order);
            JOptionPane.showMessageDialog(this, "Part order added.");
            clearForm();
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error adding part order: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrder() {
        String orderIdTxt = orderIdField.getText().trim();
        if (orderIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select an order from the table to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId   = Integer.parseInt(orderIdTxt);
        String empSSN = empSSNField.getText().trim();
        String partId = partIdField.getText().trim();
        String countTxt = countField.getText().trim();
        String dateStr  = orderDateField.getText().trim();

        if (empSSN.isEmpty() || partId.isEmpty() || countTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Employee SSN, Part ID, and Count are required.",
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

        PartOrder order = new PartOrder(
                orderId,
                empSSN,
                partId,
                count,
                dateStr.isEmpty() ? null : dateStr
        );

        try {
            partOrderDAO.updateOrder(order);
            JOptionPane.showMessageDialog(this, "Part order updated.");
            clearForm();
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating part order: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOrder() {
        String orderIdTxt = orderIdField.getText().trim();
        if (orderIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select an order from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = Integer.parseInt(orderIdTxt);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Order_ID: " + orderId + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            partOrderDAO.deleteOrder(orderId);
            JOptionPane.showMessageDialog(this, "Part order deleted.");
            clearForm();
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting part order: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void receiveOrder() {
        String orderIdTxt = orderIdField.getText().trim();
        if (orderIdTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Select an order to receive (update inventory).",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = Integer.parseInt(orderIdTxt);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this order as received and add its Count to inventory?\n" +
                "⚠ If you do this twice, inventory will increase twice.",
                "Confirm Receive",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            partOrderDAO.receiveOrder(orderId);
            JOptionPane.showMessageDialog(this, "Inventory updated for this order.");
            loadOrders();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error receiving order: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrders() {
        try {
            List<PartOrder> list = partOrderDAO.getAllOrders();
            tableModel.setRowCount(0);

            for (PartOrder o : list) {
                String empName = ((o.getEmployeeFirstName() != null) ? o.getEmployeeFirstName() : "")
                        + " "
                        + ((o.getEmployeeLastName() != null) ? o.getEmployeeLastName() : "");

                tableModel.addRow(new Object[]{
                        o.getOrderId(),
                        o.getEmpSSN(),
                        empName.trim(),
                        o.getPartId(),
                        o.getPartName(),
                        o.getCount(),
                        o.getOrderDate()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading part orders: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        orderIdField.setText("");
        empSSNField.setText("");
        partIdField.setText("");
        countField.setText("");
        orderDateField.setText("");
        ordersTable.clearSelection();
    }
}
