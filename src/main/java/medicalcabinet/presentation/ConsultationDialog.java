package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.presentation.utils.I18nManager;

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

    private JLabel lblDate;
    private JLabel lblSymptoms;
    private JLabel lblDiagnosis;
    private JLabel lblTreatment;
    private JButton saveBtn;
    private JButton cancelBtn;

    public ConsultationDialog(Dialog parent, ConsultationDTO editData, int patientId) {
        super(parent, editData == null ? I18nManager.getString("cons.diag.add", "Add Consultation") : I18nManager.getString("cons.diag.edit", "Edit Consultation"), true);
        setSize(400, 300);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblDate = new JLabel(I18nManager.getString("cons.diag.date", "Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString());
        form.add(lblDate);
        form.add(dateField);

        lblSymptoms = new JLabel(I18nManager.getString("cons.diag.symptoms", "Symptoms:"));
        symptomsField = new JTextField();
        form.add(lblSymptoms);
        form.add(symptomsField);

        lblDiagnosis = new JLabel(I18nManager.getString("cons.diag.diagnosis", "Diagnosis:"));
        diagnosisField = new JTextField();
        form.add(lblDiagnosis);
        form.add(diagnosisField);

        lblTreatment = new JLabel(I18nManager.getString("cons.diag.treatment", "Treatment:"));
        treatmentField = new JTextField();
        form.add(lblTreatment);
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
        saveBtn = new JButton(I18nManager.getString("btn.save", "Save"));
        cancelBtn = new JButton(I18nManager.getString("btn.cancel", "Cancel"));

        saveBtn.addActionListener(e -> {
            try {
                resultDTO.setConsultationDate(LocalDate.parse(dateField.getText().trim()));
                resultDTO.setSymptoms(symptomsField.getText().trim());
                resultDTO.setDiagnosis(diagnosisField.getText().trim());
                resultDTO.setTreatment(treatmentField.getText().trim());
                isApproved = true;
                dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, I18nManager.getString("cons.diag.error", "Invalid Date Format. Use YYYY-MM-DD."));
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