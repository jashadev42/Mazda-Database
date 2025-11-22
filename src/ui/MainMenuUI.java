package ui;

import java.awt.*;
import javax.swing.*;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {
        setTitle("Mazda Towson Database");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null); // center on screen

        // ----- Layout -----
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("Mazda Towson DB System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(5, 1, 10, 10));

        JButton customersBtn = new JButton("Manage Customers");
        JButton vehiclesBtn  = new JButton("Manage Vehicles");
        JButton serviceBtn   = new JButton("Service Requests");
        JButton partsBtn     = new JButton("Parts & Inventory");
        JButton exitBtn      = new JButton("Exit");

        buttonsPanel.add(customersBtn);
        buttonsPanel.add(vehiclesBtn);
        buttonsPanel.add(serviceBtn);
        buttonsPanel.add(partsBtn);
        buttonsPanel.add(exitBtn);

        add(buttonsPanel, BorderLayout.CENTER);

        // ----- Button actions -----
        customersBtn.addActionListener(e -> new CustomerUI(this));
        vehiclesBtn.addActionListener(e -> new VehicleUI(this));
        serviceBtn.addActionListener(e -> new ServiceRequestUI(this));
        partsBtn.addActionListener(e -> new PartsInventoryUI(this));
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }
}
