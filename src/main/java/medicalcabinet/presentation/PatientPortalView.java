package medicalcabinet.presentation;

import javax.swing.*;
import java.awt.*;

public class PatientPortalView extends JFrame {
    public PatientPortalView(String patientName) {
        setTitle("Medical Cabinet - Patient Portal");
        setSize(600, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Header
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Welcome to your Medical Record, " + patientName));
        add(topPanel, BorderLayout.NORTH);

        // Center Table (Consultations History)
        String[] columns = {"Date", "Symptoms", "Diagnosis", "Treatment"};
        Object[][] data = {
                {"2024-01-15", "Fever, Cough", "Viral Infection", "Rest, Ibuprofen"},
                {"2024-04-10", "Chest pain", "Bronchitis", "Antibiotics"}
        };
        JTable recordTable = new JTable(data, columns);
        add(new JScrollPane(recordTable), BorderLayout.CENTER);

        // Bottom Panel (Plugin Integration)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton exportDocBtn = new JButton("Export My Record to Word (DOC)");
        exportDocBtn.setBackground(new Color(41, 128, 185));
        exportDocBtn.setForeground(Color.WHITE);
        bottomPanel.add(exportDocBtn);

        // This is where you would hook up the DOC Plugin we made earlier!
        exportDocBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Exporting to DOC via Microkernel Plugin...");
        });

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }
}