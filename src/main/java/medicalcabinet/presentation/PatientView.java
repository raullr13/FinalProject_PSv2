package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientView extends JFrame implements IPatientView {
    private PatientPresenter presenter;

    // Get the Singleton instance
    private I18nManager i18n = I18nManager.getInstance();

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

    public PatientView() {
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JButton btnEn = new JButton("EN");
        JButton btnFr = new JButton("FR");
        JButton btnEs = new JButton("ES"); // Changed to ES

        btnEn.addActionListener(e -> changeLanguage("en", "US"));
        btnFr.addActionListener(e -> changeLanguage("fr", "FR"));
        btnEs.addActionListener(e -> changeLanguage("es", "ES")); // Changed to Spanish Locale

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
        topPanel.add(langPanel); // Add language buttons to the top right

        tableModel = new DefaultTableModel(new String[]{"", "", "", ""}, 0);
        patientTable = new JTable(tableModel);
        patientTable.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Patient List"));

        addButton = new JButton();
        updateButton = new JButton();
        deleteButton = new JButton();
        medicalRecordButton = new JButton();
        demographicsButton = new JButton();
        auditButton = new JButton("Run Data Audit"); // Or Directory (Plugin)

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

        // Action Listeners
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

    // --- Helper Methods for i18n ---

    private void changeLanguage(String lang, String country) {
        i18n.setLocale(lang, country);
        updateUITexts();
    }

    private void updateUITexts() {
        setTitle(i18n.getString("window.title"));
        searchLabel.setText(i18n.getString("lbl.search_name"));
        searchButton.setText(i18n.getString("btn.search"));
        addButton.setText(i18n.getString("btn.add"));
        updateButton.setText(i18n.getString("btn.update"));
        deleteButton.setText(i18n.getString("btn.delete"));
        medicalRecordButton.setText(i18n.getString("btn.medical_record"));
        demographicsButton.setText(i18n.getString("btn.demographics"));

        String[] columns = {
                i18n.getString("table.id"),
                i18n.getString("table.name"),
                i18n.getString("table.cnp"),
                i18n.getString("table.age")
        };
        tableModel.setColumnIdentifiers(columns);
    }

    // --- Interface Methods (RESTORED) ---

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
        // This hooks back into your actual PatientDialog window
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
            // Extract the data from the selected row in the JTable
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String cnp = (String) tableModel.getValueAt(selectedRow, 2);
            int age = (int) tableModel.getValueAt(selectedRow, 3);

            PatientDTO patient = new PatientDTO();
            patient.setFullName(name);
            patient.setCnp(cnp);
            patient.setAge(age);
            patient.setId(id);

            return patient;
        }
        return null;
    }

    @Override
    public void displayPatients(List<PatientDTO> patients) {
        // Clear the existing table data
        tableModel.setRowCount(0);

        // Populate the table with the new data from the backend
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
