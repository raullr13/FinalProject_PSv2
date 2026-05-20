package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPortalView extends JFrame {
    private PatientPortalPresenter presenter;

    private JLabel nameLabel;
    private JLabel cnpLabel;
    private JLabel ageLabel;
    private JPanel infoPanel;
    private JPanel centerPanel;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JButton exportBtn;

    private PatientDTO currentPatient;

    public PatientPortalView() {
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topContainer = new JPanel(new BorderLayout());

        infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        nameLabel = new JLabel();
        cnpLabel = new JLabel();
        ageLabel = new JLabel();
        infoPanel.add(nameLabel);
        infoPanel.add(cnpLabel);
        infoPanel.add(ageLabel);
        topContainer.add(infoPanel, BorderLayout.CENTER);

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRo = new JButton("RO");
        JButton btnEn = new JButton("EN");
        JButton btnFr = new JButton("FR");
        JButton btnEs = new JButton("ES");

        btnRo.addActionListener(e -> changeLanguage("ro", "RO"));
        btnEn.addActionListener(e -> changeLanguage("en", "US"));
        btnFr.addActionListener(e -> changeLanguage("fr", "FR"));
        btnEs.addActionListener(e -> changeLanguage("es", "ES"));

        langPanel.add(btnRo);
        langPanel.add(btnEn);
        langPanel.add(btnFr);
        langPanel.add(btnEs);
        topContainer.add(langPanel, BorderLayout.EAST);

        add(topContainer, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(0, 4) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(tableModel);

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportBtn = new JButton();
        bottomPanel.add(exportBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        exportBtn.addActionListener(e -> {
            if (presenter != null) presenter.onExportToWordClicked();
        });

        updateLanguageTexts();

        setLocationRelativeTo(null);
    }

    private void changeLanguage(String lang, String country) {
        I18nManager.setLocale(lang, country);
        updateLanguageTexts();
        if (currentPatient != null) {
            displayPatientInfo(currentPatient);
        }
    }

    private void updateLanguageTexts() {
        setTitle(I18nManager.getString("portal.title", "Cabinet Medical - Portal Pacient"));

        infoPanel.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("portal.personal_data", "Date Personale Pacient")));
        centerPanel.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("portal.history", "Istoricul tău Medical (Consultații)")));

        if(currentPatient == null) {
            nameLabel.setText(I18nManager.getString("portal.name", "Nume:") + " " + I18nManager.getString("portal.loading", "Încărcare..."));
            cnpLabel.setText(I18nManager.getString("portal.cnp", "CNP:") + " " + I18nManager.getString("portal.loading", "Încărcare..."));
            ageLabel.setText(I18nManager.getString("portal.age", "Vârstă:") + " " + I18nManager.getString("portal.loading", "Încărcare..."));
        }

        exportBtn.setText(I18nManager.getString("portal.export_doc", "Salvează fișa în Word (.doc)"));

        String[] columns = {
                I18nManager.getString("portal.col.date", "Dată"),
                I18nManager.getString("portal.col.symp", "Simptome"),
                I18nManager.getString("portal.col.diag", "Diagnostic"),
                I18nManager.getString("portal.col.treat", "Tratament")
        };
        tableModel.setColumnIdentifiers(columns);
    }

    public void setPresenter(PatientPortalPresenter presenter) {
        this.presenter = presenter;
    }

    public void displayPatientInfo(PatientDTO patient) {
        this.currentPatient = patient;
        nameLabel.setText(I18nManager.getString("portal.name_full", "Nume Complet:") + " " + patient.getFullName());
        cnpLabel.setText(I18nManager.getString("portal.cnp", "CNP:") + " " + patient.getCnp());
        ageLabel.setText(I18nManager.getString("portal.age", "Vârstă:") + " " + patient.getAge() + " " + I18nManager.getString("portal.years", "ani"));
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