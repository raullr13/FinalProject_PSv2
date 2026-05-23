package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AssistantDashboardView extends JFrame {
    private AssistantDashboardPresenter presenter;

    private JLabel searchLabel;
    private JTextField searchField;
    private JButton searchBtn;
    private JButton resetBtn;

    private JTable patientTable;
    private DefaultTableModel tableModel;

    private JPanel topPanel;
    private JPanel patientActionPanel;
    private JPanel exportActionPanel;

    private JButton addPatientBtn;
    private JButton deletePatientBtn;
    private JButton scheduleBtn;
    private JButton statsBtn;

    private JButton exportCsvBtn;
    private JButton exportJsonBtn;
    private JButton exportXmlBtn;
    private JButton exportDocBtn;
    private JButton logoutBtn;

    public AssistantDashboardView() {
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topContainer = new JPanel(new BorderLayout());

        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchLabel = new JLabel();
        searchField = new JTextField(20);
        searchBtn = new JButton();
        resetBtn = new JButton();

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(resetBtn);

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

        topContainer.add(topPanel, BorderLayout.WEST);
        topContainer.add(langPanel, BorderLayout.EAST);
        add(topContainer, BorderLayout.NORTH);

        String[] columns = {"ID", "Nume", "CNP", "Vârstă"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        patientTable = new JTable(tableModel);
        add(new JScrollPane(patientTable), BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new GridLayout(2, 1, 5, 5));

        patientActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPatientBtn = new JButton();
        deletePatientBtn = new JButton();
        scheduleBtn = new JButton();
        statsBtn = new JButton();

        patientActionPanel.add(addPatientBtn);
        patientActionPanel.add(deletePatientBtn);
        patientActionPanel.add(Box.createHorizontalStrut(20));
        patientActionPanel.add(scheduleBtn);
        patientActionPanel.add(Box.createHorizontalStrut(10));
        patientActionPanel.add(statsBtn);
        bottomContainer.add(patientActionPanel);

        exportActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportCsvBtn = new JButton();
        exportJsonBtn = new JButton();
        exportXmlBtn = new JButton();
        exportDocBtn = new JButton();
        logoutBtn = new JButton();

        exportActionPanel.add(exportCsvBtn);
        exportActionPanel.add(exportJsonBtn);
        exportActionPanel.add(exportXmlBtn);
        exportActionPanel.add(exportDocBtn);
        exportActionPanel.add(Box.createHorizontalStrut(30));
        exportActionPanel.add(logoutBtn);
        bottomContainer.add(exportActionPanel);

        add(bottomContainer, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> { if (presenter != null) presenter.onSearchClicked(searchField.getText()); });
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            if (presenter != null) presenter.loadPatients();
        });

        addPatientBtn.addActionListener(e -> { if (presenter != null) presenter.onAddPatientClicked(); });

        deletePatientBtn.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage(I18nManager.getString("ast.msg.select", "Te rog selectează un pacient din tabel."));
                return;
            }
            int patientId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            if (presenter != null) presenter.onDeletePatientClicked(patientId);
        });

        scheduleBtn.addActionListener(e -> { if (presenter != null) presenter.onScheduleAppointmentClicked(); });
        statsBtn.addActionListener(e -> { if (presenter != null) presenter.onStatisticsClicked(); });

        exportCsvBtn.addActionListener(e -> { if (presenter != null) presenter.onExportClicked("CSV"); });
        exportJsonBtn.addActionListener(e -> { if (presenter != null) presenter.onExportClicked("JSON"); });
        exportXmlBtn.addActionListener(e -> { if (presenter != null) presenter.onExportClicked("XML"); });
        exportDocBtn.addActionListener(e -> { if (presenter != null) presenter.onExportClicked("DOC"); });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            LoginView loginView = new LoginView();
            LoginPresenter loginPresenter = new LoginPresenter(loginView, new medicalcabinet.services.AuthRestClient());
            loginView.setPresenter(loginPresenter);
            loginView.setVisible(true);
        });

        updateLanguageTexts();
        setLocationRelativeTo(null);
    }

    private void changeLanguage(String lang, String country) {
        I18nManager.setLocale(lang, country);
        updateLanguageTexts();
    }

    private void updateLanguageTexts() {
        setTitle(I18nManager.getString("ast.title", "Cabinet Medical - Dashboard Asistent"));
        topPanel.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("ast.quick_search", "Căutare rapidă")));

        searchLabel.setText(I18nManager.getString("ast.search_name", "Nume Pacient:"));
        searchBtn.setText(I18nManager.getString("ast.btn_search", "Caută după Nume"));
        resetBtn.setText(I18nManager.getString("ast.btn_reset", "Resetează Lista"));

        patientActionPanel.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("ast.management", "Management Pacienți")));
        addPatientBtn.setText(I18nManager.getString("ast.btn_add", "Adaugă Pacient Nou"));
        deletePatientBtn.setText(I18nManager.getString("ast.btn_delete", "Șterge Pacient Selectat"));
        scheduleBtn.setText(I18nManager.getString("ast.btn_schedule", "Programează Consultație Nouă"));
        statsBtn.setText(I18nManager.getString("ast.btn_stats", "Generează Grafice"));

        exportActionPanel.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("ast.export", "Export Date")));
        exportCsvBtn.setText(I18nManager.getString("ast.btn_csv", "Export CSV"));
        exportJsonBtn.setText(I18nManager.getString("ast.btn_json", "Export JSON"));
        exportXmlBtn.setText(I18nManager.getString("ast.btn_xml", "Export XML"));
        exportDocBtn.setText(I18nManager.getString("ast.btn_doc", "Export DOC"));
        logoutBtn.setText(I18nManager.getString("ast.btn_logout", "Logout"));

        String[] columns = {
                I18nManager.getString("ast.col.id", "ID Pacient"),
                I18nManager.getString("ast.col.name", "Nume Complet"),
                I18nManager.getString("ast.col.cnp", "CNP"),
                I18nManager.getString("ast.col.age", "Vârstă (Ani)")
        };
        tableModel.setColumnIdentifiers(columns);
    }

    public void setPresenter(AssistantDashboardPresenter presenter) {
        this.presenter = presenter;
    }

    public void displayPatients(List<PatientDTO> patients) {
        tableModel.setRowCount(0);
        for (PatientDTO p : patients) {
            tableModel.addRow(new Object[]{ p.getId(), p.getFullName(), p.getCnp(), p.getAge() });
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public String promptForSaveFilePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(I18nManager.getString("ast.file_chooser", "Selectează unde dorești să salvezi fișierul"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}