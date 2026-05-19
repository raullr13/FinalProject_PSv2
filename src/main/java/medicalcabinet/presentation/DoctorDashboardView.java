package medicalcabinet.presentation;

import javax.swing.*;
import java.awt.*;

public class DoctorDashboardView extends JFrame {
    public DoctorDashboardView() {
        setTitle("Medical Cabinet - Doctor Dashboard");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filter My Patients by:"));
        topPanel.add(new JComboBox<>(new String[]{"Diagnosis", "Treatment"}));
        topPanel.add(new JTextField(15));
        topPanel.add(new JButton("Search"));
        add(topPanel, BorderLayout.NORTH);

        // Center Table (Mock Data)
        String[] columns = {"Patient ID", "Name", "Last Diagnosis", "Next Appointment"};
        Object[][] data = {
                {"1", "Popescu Alex", "Hypertension", "2024-05-20 10:00"},
                {"5", "Ionescu Maria", "Bronchitis", "2024-05-21 14:30"}
        };
        JTable patientTable = new JTable(data, columns);
        add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(new JButton("Set My Working Hours"));
        bottomPanel.add(new JSeparator(SwingConstants.VERTICAL));
        bottomPanel.add(new JButton("Update Selected Medical Record"));
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }
}