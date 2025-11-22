package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PartsInventoryUI extends JFrame {

    private JTextField partIdField;
    private JTextField partNameField;
    private JTextField countField;
    private JTextField unitPriceField;

    private JTable partsTable;
    private DefaultTableModel tableModel;

    public PartsInventoryUI(JFrame parent) {
        setTitle("Parts & Inventory");
        setSize(800, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));

        // --------- Form panel ----------
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        partIdField     = new JTextField();
        partNameField   = new JTextField();
        countField      = new JTextField();
        unitPriceField  = new JTextField();

        formPanel.add(new JLabel("Part ID:"));
        formPanel.add(partIdField);
        formPanel.add(new JLabel("Part Name:"));
        formPanel.add(partNameField);

        formPanel.add(new JLabel("Count:"));
        formPanel.add(countField);
        formPanel.add(new JLabel("Unit Price:"));
        formPanel.add(unitPriceField);

        add(formPanel, BorderLayout.NORTH);

        // --------- Table ----------
        tableModel = new DefaultTableModel(
                new Object[]{"Part_ID", "Part_Name", "Count", "Unit_Price"},
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

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(loadBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // TODO: connect to PartsInventoryDAO

        setVisible(true);
    }
}
