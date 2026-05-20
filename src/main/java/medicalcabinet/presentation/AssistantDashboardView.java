package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AssistantDashboardView extends JFrame {
    private AssistantDashboardPresenter presenter;

    // Componente Căutare
    private JTextField searchField;
    private JButton searchBtn;
    private JButton resetBtn;

    // Componente Tabel
    private JTable patientTable;
    private DefaultTableModel tableModel;

    // Componente Acțiuni
    private JButton addPatientBtn;
    private JButton deletePatientBtn;
    private JButton scheduleBtn;

    // Componente Export (Plugin-uri)
    private JButton exportCsvBtn;
    private JButton exportJsonBtn;
    private JButton exportXmlBtn;
    private JButton exportDocBtn;
    private JButton logoutBtn;

    public AssistantDashboardView() {
        setTitle("Cabinet Medical - Dashboard Asistent");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ==========================================
        // 1. PANELUL DE SUS: Căutare Pacienți
        // ==========================================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Căutare rapidă"));

        searchField = new JTextField(20);
        searchBtn = new JButton("Caută după Nume");
        resetBtn = new JButton("Resetează Lista");

        topPanel.add(new JLabel("Nume Pacient:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(resetBtn);
        add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // 2. PANELUL CENTRAL: Tabelul cu Pacienți
        // ==========================================
        String[] columns = {"ID Pacient", "Nume Complet", "CNP", "Vârstă (Ani)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        patientTable = new JTable(tableModel);
        add(new JScrollPane(patientTable), BorderLayout.CENTER);

        // ==========================================
        // 3. PANELUL DE JOS: Acțiuni administrative & Exporturi
        // ==========================================
        JPanel bottomContainer = new JPanel(new GridLayout(2, 1, 5, 5));

        // Rândul 1 de butoane: Management Pacienți & Programări
        JPanel patientActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientActionPanel.setBorder(BorderFactory.createTitledBorder("Management Pacienți"));

        addPatientBtn = new JButton("Adaugă Pacient Nou");
        deletePatientBtn = new JButton("Șterge Pacient Selectat");
        scheduleBtn = new JButton("📅 Programează Consultație Nouă");

        patientActionPanel.add(addPatientBtn);
        patientActionPanel.add(deletePatientBtn);
        patientActionPanel.add(Box.createHorizontalStrut(30)); // Spațiere
        patientActionPanel.add(scheduleBtn);
        bottomContainer.add(patientActionPanel);

        // Rândul 2 de butoane: Exporturi rapoarte (Microkernel Plugins)
        JPanel exportActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportActionPanel.setBorder(BorderFactory.createTitledBorder("Export Date (Microkernel Plugins)"));

        exportCsvBtn = new JButton("Export CSV");
        exportJsonBtn = new JButton("Export JSON");
        exportXmlBtn = new JButton("Export XML");
        exportDocBtn = new JButton("Export DOC (Word)");
        logoutBtn = new JButton("Logout");

        exportActionPanel.add(exportCsvBtn);
        exportActionPanel.add(exportJsonBtn);
        exportActionPanel.add(exportXmlBtn);
        exportActionPanel.add(exportDocBtn);
        exportActionPanel.add(Box.createHorizontalStrut(50));
        exportActionPanel.add(logoutBtn);
        bottomContainer.add(exportActionPanel);

        add(bottomContainer, BorderLayout.SOUTH);

        // ==========================================
        // 4. EVENIMENTE BUTOANE (LISTENERS)
        // ==========================================

        searchBtn.addActionListener(e -> {
            if (presenter != null) presenter.onSearchClicked(searchField.getText());
        });

        resetBtn.addActionListener(e -> {
            searchField.setText("");
            if (presenter != null) presenter.loadPatients();
        });

        addPatientBtn.addActionListener(e -> {
            if (presenter != null) presenter.onAddPatientClicked();
        });

        deletePatientBtn.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage("Te rog selectează un pacient din tabel pentru a-l șterge.");
                return;
            }
            int patientId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            if (presenter != null) presenter.onDeletePatientClicked(patientId);
        });

        scheduleBtn.addActionListener(e -> {
            if (presenter != null) presenter.onScheduleAppointmentClicked();
        });

        // Event-uri Exporturi
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

        setLocationRelativeTo(null);
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
        fileChooser.setDialogTitle("Selectează unde dorești să salvezi fișierul");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }
}