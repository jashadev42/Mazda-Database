package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ServiceRequestUI extends JFrame {

    private JTextField requestIdField;
    private JTextField licenseField;
    private JTextField vinField;
    private JTextField purposeField;
    private JTextField statusField;
    private JTextField startDateField;
    private JTextField finishDateField;
    private JTextField priceField;

    private JTable serviceTable;
    private DefaultTableModel tableModel;

    public ServiceRequestUI(JFrame parent) {
        setTitle("Service Requests");
        setSize(900, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // --------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 5, 5));

        requestIdField = new JTextField();
        licenseField   = new JTextField();
        vinField       = new JTextField();
        purposeField   = new JTextField();
        statusField    = new JTextField();
        startDateField = new JTextField();
        finishDateField= new JTextField();
        priceField     = new JTextField();

        formPanel.add(new JLabel("Request ID (for search):"));
        formPanel.add(requestIdField);
        formPanel.add(new JLabel("License ID:"));
        formPanel.add(licenseField);

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Purpose:"));
        formPanel.add(purposeField);

        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusField);
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        formPanel.add(startDateField);

        formPanel.add(new JLabel("Finish Date (YYYY-MM-DD):"));
        formPanel.add(finishDateField);
        formPanel.add(new JLabel("Price:"));
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

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(closeBtn);
        buttonPanel.add(loadBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // TODO: wire up to ServiceRequestDAO

        setVisible(true);
    }
}
