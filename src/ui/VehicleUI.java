package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VehicleUI extends JFrame {

    private JTextField vinField;
    private JTextField priceField;
    // Add Make/Model/Year later if you add to schema

    private JTable vehicleTable;
    private DefaultTableModel tableModel;

    public VehicleUI(JFrame parent) {
        setTitle("Manage Offered Vehicles");
        setSize(700, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // --------- Top form ----------
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        vinField   = new JTextField();
        priceField = new JTextField();

        formPanel.add(new JLabel("VIN:"));
        formPanel.add(vinField);
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);

        // spare cells for future Make/Model/Year
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));
        formPanel.add(new JLabel(""));

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"VIN", "Price"}, 0
        );
        vehicleTable = new JTable(tableModel);
        add(new JScrollPane(vehicleTable), BorderLayout.CENTER);

        // --------- Buttons ----------
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn    = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton loadBtn   = new JButton("Load All");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(loadBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // TODO: hook into VehicleDAO

        setVisible(true);
    }
}
