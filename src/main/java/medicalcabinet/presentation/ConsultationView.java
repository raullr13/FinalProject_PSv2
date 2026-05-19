package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;

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

    public ConsultationView(Frame parent, int patientId, String patientName) {
        super(parent, "Medical Record - " + patientName, true);
        this.patientId = patientId;
        setSize(800, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterDateField = new JTextField(10);
        filterDiagField = new JTextField(15);
        JButton filterBtn = new JButton("Apply Filters");
        JButton clearBtn = new JButton("Clear Filters");

        filterPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        filterPanel.add(filterDateField);
        filterPanel.add(new JLabel("Diagnosis:"));
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
        JButton addBtn = new JButton("Add Consultation");
        JButton updateBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");

        JButton exportCsvBtn = new JButton("Export CSV");
        JButton exportDocBtn = new JButton("Export DOC");
        JButton statsBtn = new JButton("View Statistics");

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
            showMessage("Invalid date format. Use YYYY-MM-DD.");
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
        fileChooser.setDialogTitle("Save Export File");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            if (!path.toLowerCase().endsWith(defaultExtension)) {
                path += defaultExtension;
            }
            return path;
        }
        return null; // User canceled the save dialog
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}