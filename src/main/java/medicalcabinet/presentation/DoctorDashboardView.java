package medicalcabinet.presentation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoctorDashboardView extends JFrame {
    private DoctorPresenter presenter;

    // Top Panel Components (Search & Filters)
    private JLabel doctorInfoLabel;
    private JTextField searchNameField;
    private JTextField filterDiagnosisField;
    private JTextField filterTreatmentField;

    // Center Components (Table)
    private JTable consultationsTable;
    private DefaultTableModel tableModel;

    // Bottom Components (Update Record)
    private JTextArea symptomsArea;
    private JTextArea diagnosisArea;
    private JTextArea treatmentArea;
    private JButton updateBtn;

    public DoctorDashboardView(String doctorName) {
        setTitle("Cabinet Medical - Doctor Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ==========================================
        // 1. TOP PANEL: Search & Filters
        // ==========================================
        JPanel topContainer = new JPanel(new BorderLayout());

        doctorInfoLabel = new JLabel("  Bun venit, Dr. " + doctorName + "!");
        doctorInfoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topContainer.add(doctorInfoLabel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Search by Name
        actionPanel.add(new JLabel("Caută Pacient:"));
        searchNameField = new JTextField(10);
        actionPanel.add(searchNameField);
        JButton searchBtn = new JButton("Caută");

        // Filter by Diagnosis/Treatment
        actionPanel.add(new JLabel(" | Diagnostic:"));
        filterDiagnosisField = new JTextField(10);
        actionPanel.add(filterDiagnosisField);

        actionPanel.add(new JLabel("Tratament:"));
        filterTreatmentField = new JTextField(10);
        actionPanel.add(filterTreatmentField);
        JButton filterBtn = new JButton("Filtrează");

        JButton resetBtn = new JButton("Reset");

        actionPanel.add(searchBtn);
        actionPanel.add(filterBtn);
        actionPanel.add(resetBtn);

        topContainer.add(actionPanel, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTER PANEL: The Table
        // ==========================================
        String[] columns = {"ID Fişă", "Nume Pacient", "Data", "Simptome", "Diagnostic", "Tratament"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Prevent direct table typing
        };
        consultationsTable = new JTable(tableModel);
        add(new JScrollPane(consultationsTable), BorderLayout.CENTER);

        // ==========================================
        // 3. BOTTOM PANEL: Update Record Form
        // ==========================================
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Actualizare Fișă Medicală (Selectează din tabel)"));

        JPanel formPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        symptomsArea = new JTextArea(4, 20);
        diagnosisArea = new JTextArea(4, 20);
        treatmentArea = new JTextArea(4, 20);

        formPanel.add(createTextAreaPanel("Simptome", symptomsArea));
        formPanel.add(createTextAreaPanel("Diagnostic", diagnosisArea));
        formPanel.add(createTextAreaPanel("Tratament", treatmentArea));

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        updateBtn = new JButton("Salvează Modificările");
        JButton logoutBtn = new JButton("Logout");
        btnPanel.add(updateBtn);
        btnPanel.add(logoutBtn);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // ==========================================
        // 4. EVENT LISTENERS
        // ==========================================

        // When clicking a row in the table, populate the edit boxes!
        consultationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && consultationsTable.getSelectedRow() != -1) {
                int row = consultationsTable.getSelectedRow();
                // Columns: 3=Symp, 4=Diag, 5=Treat
                symptomsArea.setText(getValueAtSafe(row, 3));
                diagnosisArea.setText(getValueAtSafe(row, 4));
                treatmentArea.setText(getValueAtSafe(row, 5));
            }
        });

        searchBtn.addActionListener(e -> {
            if (presenter != null) presenter.onSearchClicked(searchNameField.getText());
        });

        filterBtn.addActionListener(e -> {
            if (presenter != null) presenter.onFilterClicked(filterDiagnosisField.getText(), filterTreatmentField.getText());
        });

        resetBtn.addActionListener(e -> {
            searchNameField.setText("");
            filterDiagnosisField.setText("");
            filterTreatmentField.setText("");
            if (presenter != null) presenter.loadAllMyPatients();
        });

        updateBtn.addActionListener(e -> {
            int selectedRow = consultationsTable.getSelectedRow();
            if (selectedRow == -1) {
                showMessage("Te rog să selectezi o fișă din tabel pentru a o actualiza.");
                return;
            }
            if (presenter != null) {
                // MODIFICATION HERE: Safely parse the object to an integer
                int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

                presenter.onUpdateRecordClicked(id, symptomsArea.getText(), diagnosisArea.getText(), treatmentArea.getText());
            }
        });

        logoutBtn.addActionListener(e -> {
            this.dispose();
            LoginView loginView = new LoginView();


            LoginPresenter loginPresenter = new LoginPresenter(loginView, new medicalcabinet.services.AuthRestClient());

            loginView.setPresenter(loginPresenter);
            loginView.setVisible(true);
        });

        setLocationRelativeTo(null);
    }

    // Helper to create neat labeled text areas
    private JPanel createTextAreaPanel(String title, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JLabel(title), BorderLayout.NORTH);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    // Helper to avoid null pointer exceptions when selecting rows
    private String getValueAtSafe(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    public void setPresenter(DoctorPresenter presenter) {
        this.presenter = presenter;
    }

    public void refreshTable(Object[][] data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}