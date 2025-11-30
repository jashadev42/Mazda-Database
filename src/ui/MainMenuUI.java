package ui;

import java.awt.*;
import javax.swing.*;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {
        setTitle("Mazda Towson Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 400);
        setLocationRelativeTo(null); // center on screen

        // ----- Layout -----
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Mazda Towson DB System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        getContentPane().setBackground(new Color(40, 40, 40));
        add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(5, 1, 10, 10));
        buttonsPanel.setBackground(new Color(40, 40, 40));
        
        JButton customersBtn = new JButton("Manage Customers");
        JButton manageEmployeesBtn = new JButton("Manage Employees");
        JButton ownershipBtn = new JButton("Customer - Vehicle Ownership");
        JButton vehicleSalesBtn = new JButton("Vehicle Sales / Financing");
        JButton vehiclesBtn  = new JButton("Manage Vehicles");
        JButton serviceBtn   = new JButton("Service Requests");
        JButton partsBtn     = new JButton("Parts & Inventory");
        JButton partOrdersBtn = new JButton("Part Orders");
        JButton partSalesBtn = new JButton("Part Sales");
        JButton invoicesBtn = new JButton("Invoices");
        JButton exitBtn      = new JButton("Exit");

        buttonsPanel.add(customersBtn);
        buttonsPanel.add(manageEmployeesBtn);
        buttonsPanel.add(ownershipBtn);
        buttonsPanel.add(vehicleSalesBtn);
        buttonsPanel.add(vehiclesBtn);
        buttonsPanel.add(serviceBtn);
        buttonsPanel.add(partsBtn);
        buttonsPanel.add(partOrdersBtn);
        buttonsPanel.add(partSalesBtn);
        buttonsPanel.add(invoicesBtn);
        buttonsPanel.add(exitBtn);

        add(buttonsPanel, BorderLayout.CENTER);

        // ----- Button actions -----
        customersBtn.addActionListener(e -> new CustomerUI(this));
        manageEmployeesBtn.addActionListener(e -> new EmployeeUI(this));
        ownershipBtn.addActionListener(e -> new CustomerVehicleOwnershipUI(this));
        vehicleSalesBtn.addActionListener(e -> new VehicleSaleUI(this));
        vehiclesBtn.addActionListener(e -> new VehicleUI(this));
        serviceBtn.addActionListener(e -> new ServiceRequestUI(this));
        partsBtn.addActionListener(e -> new PartsInventoryUI(this));
        partOrdersBtn.addActionListener(e -> new PartOrderUI(this));
        partSalesBtn.addActionListener(e -> new PartSaleUI(this));
        invoicesBtn.addActionListener(e -> new InvoiceUI(this));
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
}