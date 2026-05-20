package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.repositoryaccess.SqlConsultationDAO;
import medicalcabinet.repositoryaccess.SqlPatientDAO;
import medicalcabinet.services.ConsultationService;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssistantDashboardPresenter {
    private AssistantDashboardView view;
    private SqlPatientDAO patientDAO;
    private ConsultationService consultationService;

    public AssistantDashboardPresenter(AssistantDashboardView view) {
        this.view = view;
        this.patientDAO = new SqlPatientDAO();
        this.consultationService = new ConsultationService(new SqlConsultationDAO());
        loadPatients();
    }

    public void loadPatients() {
        List<PatientDTO> patients = patientDAO.findAll();
        view.displayPatients(patients);
    }

    public void onSearchClicked(String name) {
        if (name == null || name.trim().isEmpty()) {
            loadPatients();
            return;
        }
        List<PatientDTO> filtered = patientDAO.findAll().stream()
                .filter(p -> p.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        view.displayPatients(filtered);
    }

    public void onAddPatientClicked() {
        String name = JOptionPane.showInputDialog(view, "Nume Complet Pacient:");
        if (name == null || name.trim().isEmpty()) return;

        String cnp = JOptionPane.showInputDialog(view, "CNP:");
        if (cnp == null || cnp.trim().isEmpty()) return;

        String ageStr = JOptionPane.showInputDialog(view, "Vârstă:");
        if (ageStr == null) return;

        try {
            int age = Integer.parseInt(ageStr);
            PatientDTO newPatient = new PatientDTO(0, name, cnp, age);
            if (patientDAO.save(newPatient)) {
                view.showMessage("Pacient adăugat cu succes!");
                loadPatients();
            } else {
                view.showMessage("Eroare la salvarea pacientului.");
            }
        } catch (NumberFormatException ex) {
            view.showMessage("Vârsta trebuie să fie un număr valid.");
        }
    }

    public void onDeletePatientClicked(int id) {
        int confirm = JOptionPane.showConfirmDialog(view, "Sigur dorești să ștergi acest pacient?", "Confirmare ștergere", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (patientDAO.delete(id)) {
                view.showMessage("Pacient șters!");
                loadPatients();
            } else {
                view.showMessage("Eroare la ștergerea pacientului.");
            }
        }
    }

    public void onScheduleAppointmentClicked() {
        List<PatientDTO> allPatients = patientDAO.findAll();
        Map<Integer, String> allDoctors = new HashMap<>();

        try (java.sql.Connection conn = medicalcabinet.repositoryaccess.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT id, username FROM Users WHERE role = 'DOCTOR'");
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allDoctors.put(rs.getInt("id"), rs.getString("username"));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }

        ScheduleDialog dialog = new ScheduleDialog(view, allPatients, allDoctors);
        dialog.setVisible(true);

        ConsultationDTO newAppointment = dialog.getResult();
        if (newAppointment != null) {
            if (consultationService.addConsultation(newAppointment)) {
                view.showMessage("Succes! Consultația a fost programată în sistem.");
            } else {
                view.showMessage("Eroare la programarea consultației.");
            }
        }
    }

    public void onExportClicked(String format) {
        String path = view.promptForSaveFilePath();
        if (path == null) return;

        if (!path.toLowerCase().endsWith("." + format.toLowerCase())) {
            path += "." + format.toLowerCase();
        }

        List<PatientDTO> currentPatients = patientDAO.findAll();

        medicalcabinet.core.PluginManager manager = new medicalcabinet.core.PluginManager();
        java.util.List<medicalcabinet.domain.plugincontracts.IExportPlugin> plugins = manager.loadExportPlugins("plugins_folder");

        for (medicalcabinet.domain.plugincontracts.IExportPlugin plugin : plugins) {
            if (plugin.getFormatName().equalsIgnoreCase(format)) {
                view.showMessage("Export " + format + " realizat cu succes la adresa: " + path);
                return;
            }
        }
        view.showMessage("Plugin-ul pentru formatul " + format + " nu a fost găsit în folderul 'plugins_folder'.");
    }

    public void onStatisticsClicked() {
        List<PatientDTO> allPatients = patientDAO.findAll();

        List<ConsultationDTO> allConsultations = new java.util.ArrayList<>();
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

        medicalcabinet.core.PluginManager manager = new medicalcabinet.core.PluginManager();

        List<medicalcabinet.domain.plugincontracts.IStatisticsPlugin> statPlugins = manager.loadStatisticsPlugins("plugins_folder");

        if (statPlugins == null || statPlugins.isEmpty()) {
            view.showMessage("Nu a fost găsit niciun plugin de statistici (JAR) în folderul 'plugins_folder'!");
            return;
        }

        int chartsOpened = 0;
        for (medicalcabinet.domain.plugincontracts.IStatisticsPlugin plugin : statPlugins) {
            try {
                plugin.generatePatientStatisticsChart(allPatients, allConsultations);
                chartsOpened++;
            } catch (Exception e) {
                System.out.println("Eroare la generarea graficului pentru: " + plugin.getChartType());
                e.printStackTrace();
            }
        }

        if (chartsOpened > 0) {
            view.showMessage("Succes! Au fost generate " + chartsOpened + " grafice pe ecran.");
        } else {
            view.showMessage("Eroare: Nu s-a putut deschide niciun grafic. Verifică log-urile din consolă.");
        }
    }

}