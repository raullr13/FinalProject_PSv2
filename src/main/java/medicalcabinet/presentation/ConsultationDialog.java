package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ConsultationDialog extends JDialog {
    private JTextField dateField;
    private JTextField symptomsField;
    private JTextField diagnosisField;
    private JTextField treatmentField;
    private boolean isApproved = false;
    private ConsultationDTO resultDTO;

    public ConsultationDialog(Dialog parent, ConsultationDTO editData, int patientId) {
        super(parent, editData == null ? "Add Consultation" : "Edit Consultation", true);
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString());
        form.add(dateField);

        form.add(new JLabel("Symptoms:"));
        symptomsField = new JTextField();
        form.add(symptomsField);

        form.add(new JLabel("Diagnosis:"));
        diagnosisField = new JTextField();
        form.add(diagnosisField);

        form.add(new JLabel("Treatment:"));
        treatmentField = new JTextField();
        form.add(treatmentField);

        resultDTO = new ConsultationDTO();
        resultDTO.setPatientId(patientId);

        if (editData != null) {
            resultDTO.setId(editData.getId());
            dateField.setText(editData.getConsultationDate().toString());
            symptomsField.setText(editData.getSymptoms());
            diagnosisField.setText(editData.getDiagnosis());
            treatmentField.setText(editData.getTreatment());
        }

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(e -> {
            try {
                resultDTO.setConsultationDate(LocalDate.parse(dateField.getText().trim()));
                resultDTO.setSymptoms(symptomsField.getText().trim());
                resultDTO.setDiagnosis(diagnosisField.getText().trim());
                resultDTO.setTreatment(treatmentField.getText().trim());
                isApproved = true;
                dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Use YYYY-MM-DD.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());
        buttons.add(saveBtn);
        buttons.add(cancelBtn);

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    public ConsultationDTO showDialog() {
        setVisible(true);
        return isApproved ? resultDTO : null;
    }
}