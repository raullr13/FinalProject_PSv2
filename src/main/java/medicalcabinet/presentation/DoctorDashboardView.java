package medicalcabinet.presentation;

import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorDashboardView extends JFrame {
    private DoctorPresenter presenter;
    private String doctorName;

    private JLabel doctorInfoLabel;
    private JLabel lblSearch;
    private JButton searchBtn;
    private JLabel lblDiagnosis;
    private JLabel lblTreatment;
    private JButton filterBtn;
    private JButton resetBtn;
    private JPanel bottomPanel;
    private JLabel lblSymptoms;
    private JLabel lblDiagForm;
    private JLabel lblTreatForm;
    private JButton updateBtn;
    private JButton logoutBtn;

    private JTextField searchNameField;
    private JTextField filterDiagnosisField;
    private JTextField filterTreatmentField;
    private JTable consultationsTable;
    private DefaultTableModel tableModel;
    private JTextArea symptomsArea;
    private JTextArea diagnosisArea;
    private JTextArea treatmentArea;

    public DoctorDashboardView(String doctorName) {
        this.doctorName = doctorName;

        setSize(1050, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topContainer = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        doctorInfoLabel = new JLabel();
        doctorInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(doctorInfoLabel, BorderLayout.WEST);

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEn = new JButton("EN");
        JButton btnFr = new JButton("FR");
        JButton btnEs = new JButton("ES");

        btnEn.addActionListener(e -> changeLanguage("en", "US"));
        btnFr.addActionListener(e -> changeLanguage("fr", "FR"));
        btnEs.addActionListener(e -> changeLanguage("es", "ES"));

        langPanel.add(btnEn);
        langPanel.add(btnFr);
        langPanel.add(btnEs);

        headerPanel.add(langPanel, BorderLayout.EAST);
        topContainer.add(headerPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        lblSearch = new JLabel();
        searchNameField = new JTextField(10);
        searchBtn = new JButton();

        lblDiagnosis = new JLabel();
        filterDiagnosisField = new JTextField(10);

        lblTreatment = new JLabel();
        filterTreatmentField = new JTextField(10);

        filterBtn = new JButton();
        resetBtn = new JButton();

        actionPanel.add(lblSearch);
        actionPanel.add(searchNameField);
        actionPanel.add(searchBtn);
        actionPanel.add(lblDiagnosis);
        actionPanel.add(filterDiagnosisField);
        actionPanel.add(lblTreatment);
        actionPanel.add(filterTreatmentField);
        actionPanel.add(filterBtn);
        actionPanel.add(resetBtn);

        topContainer.add(actionPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(0, 6) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        consultationsTable = new JTable(tableModel);
        add(new JScrollPane(consultationsTable), BorderLayout.CENTER);

        bottomPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        symptomsArea = new JTextArea(4, 20);
        diagnosisArea = new JTextArea(4, 20);
        treatmentArea = new JTextArea(4, 20);

        lblSymptoms = new JLabel();
        lblDiagForm = new JLabel();
        lblTreatForm = new JLabel();

        formPanel.add(createTextAreaPanel(lblSymptoms, symptomsArea));
        formPanel.add(createTextAreaPanel(lblDiagForm, diagnosisArea));
        formPanel.add(createTextAreaPanel(lblTreatForm, treatmentArea));

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        updateBtn = new JButton();
        logoutBtn = new JButton();
        btnPanel.add(updateBtn);
        btnPanel.add(logoutBtn);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        updateLanguageTexts();

        consultationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && consultationsTable.getSelectedRow() != -1) {
                int row = consultationsTable.getSelectedRow();
                symptomsArea.setText(getValueAtSafe(row, 3));
                diagnosisArea.setText(getValueAtSafe(row, 4));
                treatmentArea.setText(getValueAtSafe(row, 5));
            }
        });

        searchBtn.addActionListener(e -> { if (presenter != null) presenter.onSearchClicked(searchNameField.getText()); });
        filterBtn.addActionListener(e -> { if (presenter != null) presenter.onFilterClicked(filterDiagnosisField.getText(), filterTreatmentField.getText()); });
        resetBtn.addActionListener(e -> {
            searchNameField.setText("");
            filterDiagnosisField.setText("");
            filterTreatmentField.setText("");
            if (presenter != null) presenter.loadAllMyPatients();
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = consultationsTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage(I18nManager.getString("doc.msg.select", "Te rog să selectezi o fișă din tabel pentru a o actualiza."));
                return;
            }
            if (presenter != null) {
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                presenter.onUpdateRecordClicked(id, symptomsArea.getText(), diagnosisArea.getText(), treatmentArea.getText());
            }
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            medicalcabinet.presentation.LoginView loginView = new medicalcabinet.presentation.LoginView();
            medicalcabinet.services.AuthRestClient authClient = new medicalcabinet.services.AuthRestClient();
            medicalcabinet.presentation.LoginPresenter loginPresenter = new medicalcabinet.presentation.LoginPresenter(loginView, authClient);
            loginView.setPresenter(loginPresenter);
            loginView.setVisible(true);
        });

        setLocationRelativeTo(null);
    }

    private void changeLanguage(String lang, String country) {
        I18nManager.setLocale(lang, country);
        updateLanguageTexts();
    }

    private void updateLanguageTexts() {
        setTitle(I18nManager.getString("doc.title", "Cabinet Medical - Doctor Dashboard"));
        doctorInfoLabel.setText("  " + I18nManager.getString("doc.welcome", "Bun venit, Dr. ") + doctorName + "!");

        lblSearch.setText(I18nManager.getString("doc.search", "Caută Pacient:"));
        searchBtn.setText(I18nManager.getString("doc.searchBtn", "Caută"));

        lblDiagnosis.setText(" | " + I18nManager.getString("doc.diagnosis", "Diagnostic:"));
        lblTreatment.setText(I18nManager.getString("doc.treatment", "Tratament:"));

        filterBtn.setText(I18nManager.getString("doc.filterBtn", "Filtrează"));
        resetBtn.setText(I18nManager.getString("doc.resetBtn", "Reset"));

        bottomPanel.setBorder(BorderFactory.createTitledBorder(
                I18nManager.getString("doc.bottomPanel", "Actualizare Fișă Medicală (Selectează din tabel)")));

        lblSymptoms.setText(I18nManager.getString("doc.symptoms", "Simptome"));
        lblDiagForm.setText(I18nManager.getString("doc.diagnosisForm", "Diagnostic"));
        lblTreatForm.setText(I18nManager.getString("doc.treatmentForm", "Tratament"));

        updateBtn.setText(I18nManager.getString("doc.updateBtn", "Salvează Modificările"));
        logoutBtn.setText(I18nManager.getString("doc.logoutBtn", "Logout"));

        String[] columns = {
                I18nManager.getString("doc.col.id", "ID Fişă"),
                I18nManager.getString("doc.col.name", "Nume Pacient"),
                I18nManager.getString("doc.col.date", "Data"),
                I18nManager.getString("doc.col.symp", "Simptome"),
                I18nManager.getString("doc.col.diag", "Diagnostic"),
                I18nManager.getString("doc.col.treat", "Tratament")
        };
        tableModel.setColumnIdentifiers(columns);
    }

    private JPanel createTextAreaPanel(JLabel label, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(label, BorderLayout.NORTH);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private String getValueAtSafe(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    public void setPresenter(DoctorPresenter presenter) { this.presenter = presenter; }

    public void refreshTable(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) tableModel.addRow(row);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}