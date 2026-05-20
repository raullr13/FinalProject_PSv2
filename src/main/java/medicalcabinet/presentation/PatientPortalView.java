package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPortalView extends JFrame {
    private PatientPortalPresenter presenter;

    private JLabel nameLabel;
    private JLabel cnpLabel;
    private JLabel ageLabel;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton exportBtn;

    public PatientPortalView() {
        setTitle("Cabinet Medical - Portal Pacient");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel de sus: Datele Personale ale Pacientului
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Date Personale Pacient"));
        nameLabel = new JLabel("Nume: Încărcare...");
        cnpLabel = new JLabel("CNP: Încărcare...");
        ageLabel = new JLabel("Vârstă: Încărcare...");
        infoPanel.add(nameLabel);
        infoPanel.add(cnpLabel);
        infoPanel.add(ageLabel);
        add(infoPanel, BorderLayout.NORTH);

        // Panel Central: Tabelul cu Consultații (Fișa Medicală)
        String[] columns = {"Dată", "Simptome", "Diagnostic", "Tratament"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Istoricul tău Medical (Consultații)"));
        centerPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Panel de jos: Buton Export Word
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportBtn = new JButton("Salvează fișa în Word (.doc)");
        bottomPanel.add(exportBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Eveniment Buton
        exportBtn.addActionListener(e -> {
            if (presenter != null) presenter.onExportToWordClicked();
        });

        setLocationRelativeTo(null);
    }

    public void setPresenter(PatientPortalPresenter presenter) {
        this.presenter = presenter;
    }

    public void displayPatientInfo(PatientDTO patient) {
        nameLabel.setText("Nume Complet: " + patient.getFullName());
        cnpLabel.setText("CNP: " + patient.getCnp());
        ageLabel.setText("Vârstă: " + patient.getAge() + " ani");
    }

    public void displayMedicalHistory(List<ConsultationDTO> history) {
        tableModel.setRowCount(0);
        for (ConsultationDTO c : history) {
            tableModel.addRow(new Object[]{
                    c.getConsultationDate().toString(),
                    c.getSymptoms(),
                    c.getDiagnosis(),
                    c.getTreatment()
            });
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}