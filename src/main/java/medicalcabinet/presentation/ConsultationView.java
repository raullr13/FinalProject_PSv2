package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ConsultationView extends JDialog implements IConsultationView {
    private ConsultationPresenter presenter;
    private final int patientId;

    private JTextField filterDateField;
    private JTextField filterDiagField;
    private JTable consultationTable;
    private DefaultTableModel tableModel;

    private JLabel lblDate;
    private JLabel lblDiag;
    private JButton filterBtn;
    private JButton clearBtn;
    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private JButton exportCsvBtn;
    private JButton exportDocBtn;
    private JButton statsBtn;

    public ConsultationView(Frame parent, int patientId, String patientName) {
        super(parent, "Medical Record - " + patientName, true);
        this.patientId = patientId;
        setSize(800, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterDateField = new JTextField(10);
        filterDiagField = new JTextField(15);
        lblDate = new JLabel();
        lblDiag = new JLabel();
        filterBtn = new JButton();
        clearBtn = new JButton();

        filterPanel.add(lblDate);
        filterPanel.add(filterDateField);
        filterPanel.add(lblDiag);
        filterPanel.add(filterDiagField);
        filterPanel.add(filterBtn);
        filterPanel.add(clearBtn);

        String[] columns = {"ID", "Date", "Symptoms", "Diagnosis", "Treatment"};
        tableModel = new DefaultTableModel(columns, 0);
        consultationTable = new JTable(tableModel);
        consultationTable.setDefaultEditor(Object.class, null);

        JScrollPane scrollPane = new JScrollPane(consultationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Consultation History"));

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportCsvBtn = new JButton();
        exportDocBtn = new JButton();
        statsBtn = new JButton();
        addBtn = new JButton();
        updateBtn = new JButton();
        deleteBtn = new JButton();

        actionPanel.add(exportCsvBtn);
        actionPanel.add(exportDocBtn);
        actionPanel.add(statsBtn);
        actionPanel.add(new JSeparator(SwingConstants.VERTICAL));
        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        exportCsvBtn.addActionListener(e -> { if (presenter != null) presenter.onExportCsvClicked(); });
        exportDocBtn.addActionListener(e -> { if (presenter != null) presenter.onExportDocClicked(); });
        statsBtn.addActionListener(e -> { if (presenter != null) presenter.onStatsClicked(); });

        filterBtn.addActionListener(e -> { if (presenter != null) presenter.onFilterClicked(); });
        clearBtn.addActionListener(e -> {
            filterDateField.setText("");
            filterDiagField.setText("");
            if (presenter != null) presenter.onClearFilterClicked();
        });
        addBtn.addActionListener(e -> { if (presenter != null) presenter.onAddClicked(); });
        updateBtn.addActionListener(e -> { if (presenter != null) presenter.onUpdateClicked(); });
        deleteBtn.addActionListener(e -> { if (presenter != null) presenter.onDeleteClicked(); });

        updateUITexts();
    }

    private void updateUITexts() {
        lblDate.setText(I18nManager.getString("cons.date", "Date (YYYY-MM-DD):"));
        lblDiag.setText(I18nManager.getString("cons.diag", "Diagnosis:"));
        filterBtn.setText(I18nManager.getString("cons.filter", "Apply Filters"));
        clearBtn.setText(I18nManager.getString("cons.clear", "Clear Filters"));
        addBtn.setText(I18nManager.getString("cons.add", "Add Consultation"));
        updateBtn.setText(I18nManager.getString("cons.update", "Update Selected"));
        deleteBtn.setText(I18nManager.getString("cons.delete", "Delete Selected"));
        exportCsvBtn.setText(I18nManager.getString("cons.export_csv", "Export CSV"));
        exportDocBtn.setText(I18nManager.getString("cons.export_doc", "Export DOC"));
        statsBtn.setText(I18nManager.getString("cons.stats", "View Statistics"));

        String[] columns = {
                I18nManager.getString("table.id", "ID"),
                I18nManager.getString("table.date", "Date"),
                I18nManager.getString("table.symp", "Symptoms"),
                I18nManager.getString("table.diag", "Diagnosis"),
                I18nManager.getString("table.treat", "Treatment")
        };
        tableModel.setColumnIdentifiers(columns);
    }

    public void setPresenter(ConsultationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public LocalDate getFilterDate() {
        String text = filterDateField.getText().trim();
        if (text.isEmpty()) return null;
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            showMessage(I18nManager.getString("cons.error.date", "Invalid date format. Use YYYY-MM-DD."));
            return null;
        }
    }

    @Override
    public String getFilterDiagnosis() {
        return filterDiagField.getText().trim();
    }

    @Override
    public ConsultationDTO getSelectedConsultation() {
        int row = consultationTable.getSelectedRow();
        if (row < 0) return null;

        ConsultationDTO dto = new ConsultationDTO();
        dto.setId((int) tableModel.getValueAt(row, 0));
        dto.setPatientId(this.patientId);
        dto.setConsultationDate(LocalDate.parse(tableModel.getValueAt(row, 1).toString()));
        dto.setSymptoms((String) tableModel.getValueAt(row, 2));
        dto.setDiagnosis((String) tableModel.getValueAt(row, 3));
        dto.setTreatment((String) tableModel.getValueAt(row, 4));
        return dto;
    }

    @Override
    public ConsultationDTO showConsultationFormDialog(ConsultationDTO consultationToEdit, int pId) {
        ConsultationDialog dialog = new ConsultationDialog(this, consultationToEdit, pId);
        return dialog.showDialog();
    }

    @Override
    public void displayConsultations(List<ConsultationDTO> consultations) {
        tableModel.setRowCount(0);
        for (ConsultationDTO c : consultations) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getConsultationDate().toString(), c.getSymptoms(), c.getDiagnosis(), c.getTreatment()
            });
        }
    }

    @Override
    public String promptForSaveFilePath(String defaultExtension) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(I18nManager.getString("file.save", "Save Export File"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(defaultExtension)) {
                path += defaultExtension;
            }
            return path;
        }
        return null;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}