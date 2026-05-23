package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.plugincontracts.IExportPlugin;
import medicalcabinet.domain.plugincontracts.IStatisticsPlugin;
import medicalcabinet.services.ConsultationService;
import medicalcabinet.presentation.utils.I18nManager;

import java.time.LocalDate;
import java.util.List;

public class ConsultationPresenter {
    private final IConsultationView view;
    private final ConsultationService service;
    private final int patientId;

    public ConsultationPresenter(IConsultationView view, ConsultationService service, int patientId) {
        this.view = view;
        this.service = service;
        this.patientId = patientId;
        refreshConsultationList();
    }

    public void onFilterClicked() {
        LocalDate date = view.getFilterDate();
        String diagnosis = view.getFilterDiagnosis();

        List<ConsultationDTO> filtered = service.filterConsultations(patientId, date, diagnosis);
        view.displayConsultations(filtered);
    }

    public void onClearFilterClicked() {
        refreshConsultationList();
    }

    public void onAddClicked() {
        ConsultationDTO newConsultation = view.showConsultationFormDialog(null, patientId);
        if (newConsultation != null) {
            if (service.addConsultation(newConsultation)) {
                view.showMessage(I18nManager.getString("cons.add.success", "Consultation added!"));
                refreshConsultationList();
            } else {
                view.showMessage(I18nManager.getString("cons.add.fail", "Failed to add consultation."));
            }
        }
    }

    public void onUpdateClicked() {
        ConsultationDTO selected = view.getSelectedConsultation();
        if (selected == null) {
            view.showMessage(I18nManager.getString("cons.select.update", "Select a consultation to update."));
            return;
        }

        ConsultationDTO updated = view.showConsultationFormDialog(selected, patientId);
        if (updated != null) {
            if (service.updateConsultation(updated)) {
                view.showMessage(I18nManager.getString("cons.update.success", "Consultation updated!"));
                refreshConsultationList();
            } else {
                view.showMessage(I18nManager.getString("cons.update.fail", "Failed to update consultation."));
            }
        }
    }

    public void onDeleteClicked() {
        ConsultationDTO selected = view.getSelectedConsultation();
        if (selected == null) {
            view.showMessage(I18nManager.getString("cons.select.delete", "Select a consultation to delete."));
            return;
        }

        if (service.deleteConsultation(selected.getId())) {
            view.showMessage(I18nManager.getString("cons.delete.success", "Consultation deleted!"));
            refreshConsultationList();
        } else {
            view.showMessage(I18nManager.getString("cons.delete.fail", "Failed to delete consultation."));
        }
    }

    private void refreshConsultationList() {
        List<ConsultationDTO> list = service.getPatientMedicalRecord(patientId);
        view.displayConsultations(list);
    }

    public void onExportCsvClicked() {
        handleExport("CSV", ".csv");
    }

    public void onExportDocClicked() {
        handleExport("DOC", ".doc");
    }

    private void handleExport(String formatName, String extension) {
        String path = view.promptForSaveFilePath(extension);
        if (path == null) return;

        List<ConsultationDTO> currentConsultations = service.getPatientMedicalRecord(patientId);

        medicalcabinet.core.PluginManager manager = new medicalcabinet.core.PluginManager();
        List<IExportPlugin> plugins = manager.loadExportPlugins("plugins_folder");

        for (IExportPlugin plugin : plugins) {
            if (plugin.getFormatName().equalsIgnoreCase(formatName)) {
                if (plugin.exportConsultations(path, currentConsultations)) {
                    view.showMessage(formatName + " " + I18nManager.getString("cons.export.success", "export successful!"));
                } else {
                    view.showMessage(formatName + " " + I18nManager.getString("cons.export.fail", "export failed. Check file permissions."));
                }
                return;
            }
        }
        view.showMessage(I18nManager.getString("cons.plugin.error", "Configuration Error: No plugin found for format: ") + formatName);
    }

    public void onStatsClicked() {
        List<ConsultationDTO> currentConsultations = service.getPatientMedicalRecord(patientId);

        if (currentConsultations.isEmpty()) {
            view.showMessage(I18nManager.getString("cons.stats.nodata", "Not enough data to generate statistics."));
            return;
        }

        medicalcabinet.core.PluginManager manager = new medicalcabinet.core.PluginManager();
        List<IStatisticsPlugin> statPlugins = manager.loadStatisticsPlugins("plugins_folder");

        boolean pluginFound = false;
        for (IStatisticsPlugin plugin : statPlugins) {
            if (plugin.getChartType().contains("Diagnosis") || plugin.getChartType().contains("Bar Chart")) {
                plugin.generatePatientStatisticsChart(null, currentConsultations);
                pluginFound = true;
                break;
            }
        }

        if (!pluginFound) {
            view.showMessage(I18nManager.getString("cons.stats.plugin.error", "Diagnosis statistics plugin not found in the plugins_folder."));
        }
    }
}