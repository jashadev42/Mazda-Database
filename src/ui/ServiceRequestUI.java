package ui;

import dao.ServiceRequestDAO;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ServiceRequest;

public class ServiceRequestUI extends JFrame {

    private JTextField requestIdField;
    private JTextField licenseField;
    private JTextField vinField;
    private JTextField purposeField;    // maps to Description
    private JTextField statusField;
    private JTextField startDateField;
    private JTextField finishDateField;
    private JTextField priceField;      // maps to Cost

    private JTable serviceTable;
    private DefaultTableModel tableModel;

    private ServiceRequestDAO serviceDAO;

    public ServiceRequestUI(JFrame parent) {
        setTitle("Service Requests");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        serviceDAO = new ServiceRequestDAO();

        // --------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        requestIdField   = new JTextField();
        licenseField     = new JTextField();
        vinField         = new JTextField();
        purposeField     = new JTextField();
        statusField      = new JTextField();
        startDateField   = new JTextField();
        finishDateField  = new JTextField();
        priceField       = new JTextField();

        formPanel.add(new JLabel("Request ID (for search/update):"));
        formPanel.add(requestIdField);
        formPanel.add(new JLabel("License ID:"));
        formPanel.add(licenseField);

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Purpose / Description:"));
        formPanel.add(purposeField);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusField);
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        formPanel.add(startDateField);

        formPanel.add(new JLabel("Finish Date (YYYY-MM-DD):"));
        formPanel.add(finishDateField);
        formPanel.add(new JLabel("Price / Cost:"));
        formPanel.add(priceField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"Request_ID", "License_ID", "VIN", "Purpose",
                        "Status", "Start_Date", "Finish_Date", "Price"},
                0
        );
        serviceTable = new JTable(tableModel);
        add(new JScrollPane(serviceTable), BorderLayout.CENTER);

        // --------- Buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn    = new JButton("Create");
        JButton updateBtn = new JButton("Update");
        JButton closeBtn  = new JButton("Mark Completed");
        JButton loadBtn   = new JButton("Load All");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(closeBtn);
        buttonPanel.add(loadBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // --------- Button actions ----------
        addBtn.addActionListener(e -> createServiceRequest());
        updateBtn.addActionListener(e -> updateServiceRequest());
        closeBtn.addActionListener(e -> closeServiceRequest());
        loadBtn.addActionListener(e -> loadServiceRequests());
        clearBtn.addActionListener(e -> clearForm());

        // When a row is selected, fill the form fields
        serviceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = serviceTable.getSelectedRow();
                if (row >= 0) {
                    requestIdField.setText(tableModel.getValueAt(row, 0).toString());
                    licenseField.setText(tableModel.getValueAt(row, 1).toString());
                    vinField.setText(tableModel.getValueAt(row, 2).toString());
                    purposeField.setText(
                            tableModel.getValueAt(row, 3) != null
                                    ? tableModel.getValueAt(row, 3).toString()
                                    : ""
                    );
                    statusField.setText(
                            tableModel.getValueAt(row, 4) != null
                                    ? tableModel.getValueAt(row, 4).toString()
                                    : ""
                    );
                    startDateField.setText(
                            tableModel.getValueAt(row, 5) != null
                                    ? tableModel.getValueAt(row, 5).toString()
                                    : ""
                    );
                    finishDateField.setText(
                            tableModel.getValueAt(row, 6) != null
                                    ? tableModel.getValueAt(row, 6).toString()
                                    : ""
                    );
                    priceField.setText(
                            tableModel.getValueAt(row, 7) != null
                                    ? tableModel.getValueAt(row, 7).toString()
                                    : ""
                    );
                }
            }
        });

        // Optionally load on open
        loadServiceRequests();

        setVisible(true);
    }

    private void createServiceRequest() {
        String licenseId  = licenseField.getText().trim();
        String vin        = vinField.getText().trim();
        String description = purposeField.getText().trim();
        String status     = statusField.getText().trim();
        String startDate  = startDateField.getText().trim();
        String finishDate = finishDateField.getText().trim();
        String costText   = priceField.getText().trim();

        if (licenseId.isEmpty() || vin.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "License ID, VIN, and Purpose/Description are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double cost = null;
        if (!costText.isEmpty()) {
            try {
                cost = Double.parseDouble(costText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Cost must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        ServiceRequest r = new ServiceRequest(
                licenseId,
                vin,
                cost,
                description,
                status.isEmpty() ? "Open" : status,
                startDate.isEmpty() ? null : startDate,
                finishDate.isEmpty() ? null : finishDate
        );

        try {
            serviceDAO.addServiceRequest(r);
            JOptionPane.showMessageDialog(this, "Service request created.");
            clearForm();
            loadServiceRequests();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error creating service request: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateServiceRequest() {
        String requestIdText = requestIdField.getText().trim();
        if (requestIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter or select a Request ID to update.",
                    "No Request ID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Request ID must be an integer.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String licenseId  = licenseField.getText().trim();
        String vin        = vinField.getText().trim();
        String description = purposeField.getText().trim();
        String status     = statusField.getText().trim();
        String startDate  = startDateField.getText().trim();
        String finishDate = finishDateField.getText().trim();
        String costText   = priceField.getText().trim();

        if (licenseId.isEmpty() || vin.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "License ID, VIN, and Purpose/Description are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Double cost = null;
        if (!costText.isEmpty()) {
            try {
                cost = Double.parseDouble(costText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Cost must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        ServiceRequest r = new ServiceRequest(
                requestId,
                licenseId,
                vin,
                cost,
                description,
                status.isEmpty() ? "Open" : status,
                startDate.isEmpty() ? null : startDate,
                finishDate.isEmpty() ? null : finishDate
        );

        try {
            serviceDAO.updateServiceRequest(r);
            JOptionPane.showMessageDialog(this, "Service request updated.");
            clearForm();
            loadServiceRequests();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error updating service request: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void closeServiceRequest() {
        String requestIdText = requestIdField.getText().trim();
        if (requestIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Enter or select a Request ID to mark as completed.",
                    "No Request ID",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(requestIdText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Request ID must be an integer.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String costText   = priceField.getText().trim();
        String finishDate = finishDateField.getText().trim();

        Double cost = null;
        if (!costText.isEmpty()) {
            try {
                cost = Double.parseDouble(costText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Cost must be a number.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // If finish date empty, you could also auto-set to today's date,
        // but here we'll just allow null.
        if (finishDate.isEmpty()) {
            finishDate = null;
        }

        try {
            serviceDAO.markCompleted(requestId, cost, finishDate);
            JOptionPane.showMessageDialog(this, "Service request marked as completed.");
            clearForm();
            loadServiceRequests();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error completing service request: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadServiceRequests() {
        try {
            List<ServiceRequest> list = serviceDAO.getAllServiceRequests();

            tableModel.setRowCount(0);

            for (ServiceRequest r : list) {
                tableModel.addRow(new Object[]{
                        r.getRequestId(),
                        r.getLicenseId(),
                        r.getVin(),
                        r.getDescription(),
                        r.getStatus(),
                        r.getStartDate(),
                        r.getFinishDate(),
                        r.getCost()
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading service requests: " + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        requestIdField.setText("");
        licenseField.setText("");
        vinField.setText("");
        purposeField.setText("");
        statusField.setText("");
        startDateField.setText("");
        finishDateField.setText("");
        priceField.setText("");
        serviceTable.clearSelection();
    }
}
