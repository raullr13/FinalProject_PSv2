package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientView extends JFrame implements IPatientView {
    private PatientPresenter presenter;

    private JTextField searchField;
    private JButton searchButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton addButton;
    private JButton medicalRecordButton;
    private JButton demographicsButton;
    private JButton auditButton;
    private JLabel searchLabel;

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    public PatientView() {
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JButton btnRo = new JButton("RO");
        JButton btnEn = new JButton("EN");
        JButton btnFr = new JButton("FR");
        JButton btnEs = new JButton("ES");

        btnRo.addActionListener(e -> changeLanguage("ro", "RO"));
        btnEn.addActionListener(e -> changeLanguage("en", "US"));
        btnFr.addActionListener(e -> changeLanguage("fr", "FR"));
        btnEs.addActionListener(e -> changeLanguage("es", "ES"));

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.add(btnRo);
        langPanel.add(btnEn);
        langPanel.add(btnFr);
        langPanel.add(btnEs);

        searchField = new JTextField(20);
        searchButton = new JButton();
        searchLabel = new JLabel();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(langPanel);

        tableModel = new DefaultTableModel(new String[]{"", "", "", ""}, 0);
        patientTable = new JTable(tableModel);
        patientTable.setDefaultEditor(Object.class, null);

        scrollPane = new JScrollPane(patientTable);

        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();
        medicalRecordButton = new JButton();
        demographicsButton = new JButton();
        auditButton = new JButton();

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(auditButton);
        bottomPanel.add(demographicsButton);
        bottomPanel.add(medicalRecordButton);

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(5, 25));
        bottomPanel.add(separator);

        bottomPanel.add(addButton);
        bottomPanel.add(updateButton);
        bottomPanel.add(deleteButton);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> { if (presenter != null) presenter.onSearchButtonClicked(); });
        addButton.addActionListener(e -> { if (presenter != null) presenter.onAddPatientClicked(); });
        updateButton.addActionListener(e -> { if (presenter != null) presenter.onUpdatePatientClicked(); });
        deleteButton.addActionListener(e -> { if (presenter != null) presenter.onDeletePatientClicked(); });
        medicalRecordButton.addActionListener(e -> { if (presenter != null) presenter.onViewMedicalRecordClicked(); });
        demographicsButton.addActionListener(e -> { if (presenter != null) presenter.onDemographicsClicked(); });
        auditButton.addActionListener(e -> { if (presenter != null) presenter.onAuditClicked(); });

        updateUITexts();

        setLocationRelativeTo(null);
    }

    private void changeLanguage(String lang, String country) {
        I18nManager.setLocale(lang, country);
        updateUITexts();
    }

    private void updateUITexts() {
        setTitle(I18nManager.getString("window.title", "Gestionare Pacienți"));
        searchLabel.setText(I18nManager.getString("lbl.search_name", "Caută Nume:"));
        searchButton.setText(I18nManager.getString("btn.search", "Caută"));
        addButton.setText(I18nManager.getString("btn.add", "Adaugă Pacient"));
        updateButton.setText(I18nManager.getString("btn.update", "Actualizează"));
        deleteButton.setText(I18nManager.getString("btn.delete", "Șterge"));
        medicalRecordButton.setText(I18nManager.getString("btn.medical_record", "Fișă Medicală"));
        demographicsButton.setText(I18nManager.getString("btn.demographics", "Demografice"));
        auditButton.setText(I18nManager.getString("btn.audit", "Run Data Audit"));

        scrollPane.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("border.patient_list", "Lista Pacienți")));

        String[] columns = {
                I18nManager.getString("table.id", "ID"),
                I18nManager.getString("table.name", "Nume Complet"),
                I18nManager.getString("table.cnp", "CNP"),
                I18nManager.getString("table.age", "Vârstă")
        };
        tableModel.setColumnIdentifiers(columns);
    }

    @Override
    public void setPresenter(PatientPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getSearchText() {
        return searchField.getText();
    }

    @Override
    public PatientDTO showPatientFormDialog(PatientDTO patientToEdit) {
        PatientDialog dialog = new PatientDialog(this, patientToEdit);
        dialog.setVisible(true);
        if (dialog.isApproved()) {
            return dialog.getPatient();
        }
        return null;
    }

    @Override
    public PatientDTO getSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            String name = tableModel.getValueAt(selectedRow, 1).toString();
            String cnp = tableModel.getValueAt(selectedRow, 2).toString();
            int age = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

            PatientDTO patient = new PatientDTO();
            patient.setId(id);
            patient.setFullName(name);
            patient.setCnp(cnp);
            patient.setAge(age);

            return patient;
        }
        return null;
    }

    @Override
    public void displayPatients(List<PatientDTO> patients) {
        tableModel.setRowCount(0);
        if (patients != null) {
            for (PatientDTO p : patients) {
                tableModel.addRow(new Object[]{p.getId(), p.getFullName(), p.getCnp(), p.getAge()});
            }
        }
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}