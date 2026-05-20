package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

public class ScheduleDialog extends JDialog {
    private JComboBox<PatientItem> patientCombo;
    private JComboBox<DoctorItem> doctorCombo;
    private JTextField dateField;

    private ConsultationDTO result = null;

    public ScheduleDialog(JFrame parent, List<PatientDTO> patients, Map<Integer, String> doctors) {
        super(parent, "Programează o Consultație", true);
        setSize(400, 250);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. Dropdown Pacienți
        formPanel.add(new JLabel("Selectează Pacientul:"));
        patientCombo = new JComboBox<>();
        for (PatientDTO p : patients) {
            patientCombo.addItem(new PatientItem(p.getId(), p.getFullName()));
        }
        formPanel.add(patientCombo);

        // 2. Dropdown Medici
        formPanel.add(new JLabel("Selectează Medicul:"));
        doctorCombo = new JComboBox<>();
        for (Map.Entry<Integer, String> entry : doctors.entrySet()) {
            doctorCombo.addItem(new DoctorItem(entry.getKey(), entry.getValue()));
        }
        formPanel.add(doctorCombo);

        // 3. Data Consultației
        formPanel.add(new JLabel("Data (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString()); // Default to today
        formPanel.add(dateField);

        add(formPanel, BorderLayout.CENTER);

        // Butoane Salvare / Anulare
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Programează");
        JButton cancelBtn = new JButton("Anulează");

        saveBtn.addActionListener(e -> saveConsultation());
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void saveConsultation() {
        try {
            PatientItem selectedPatient = (PatientItem) patientCombo.getSelectedItem();
            DoctorItem selectedDoctor = (DoctorItem) doctorCombo.getSelectedItem();
            LocalDate date = LocalDate.parse(dateField.getText());

            if (selectedPatient == null || selectedDoctor == null) {
                JOptionPane.showMessageDialog(this, "Te rog selectează un pacient și un medic.");
                return;
            }

            // Creăm DTO-ul cu ID 0 (va fi generat de MySQL) și câmpuri medicale goale
            result = new ConsultationDTO(
                    0,
                    selectedPatient.id,
                    selectedDoctor.id,
                    date,
                    "", "", "" // Goale, așteaptă ca medicul să le completeze!
            );
            dispose();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format dată invalid. Te rog folosește YYYY-MM-DD (ex: 2026-10-15)");
        }
    }

    public ConsultationDTO getResult() {
        return result;
    }

    private static class PatientItem {
        int id; String name;
        PatientItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name; }
    }

    private static class DoctorItem {
        int id; String name;
        DoctorItem(int id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return "Dr. " + name; }
    }
}