package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import java.awt.*;

public class PatientDialog extends JDialog {
    private JTextField nameField;
    private JTextField cnpField;
    private JTextField ageField;

    private boolean approved = false;
    private PatientDTO patient;

    public PatientDialog(JFrame parent, PatientDTO patientToEdit) {
        super(parent, patientToEdit == null ? "Add New Patient" : "Edit Patient", true);
        setSize(300, 200);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        this.patient = patientToEdit;

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField(patient != null ? patient.getFullName() : "");

        cnpField = new JTextField(patient != null ? patient.getCnp() : "");

        ageField = new JTextField(patient != null ? String.valueOf(patient.getAge()) : "");

        formPanel.add(new JLabel(I18nManager.getString("lbl.name", "Name:")));
        formPanel.add(nameField);

        formPanel.add(new JLabel(I18nManager.getString("lbl.cnp", "CNP:")));
        formPanel.add(cnpField);

        formPanel.add(new JLabel(I18nManager.getString("lbl.age", "Age:")));
        formPanel.add(ageField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String cnp = cnpField.getText();
                int age = Integer.parseInt(ageField.getText());

                if (patient == null) {
                    patient = new PatientDTO();
                } else {
                    patient.setFullName(name);
                    patient.setCnp(cnp);
                    patient.setAge(age);
                }

                approved = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Age must be a valid number!");
            }
        });

        cancelButton.addActionListener(e -> {
            approved = false;
            setVisible(false);
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public boolean isApproved() {
        return approved;
    }

    public PatientDTO getPatient() {
        return patient;
    }
}