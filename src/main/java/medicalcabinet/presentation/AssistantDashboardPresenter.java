package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.DoctorDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.services.ConsultationRestClient;
import medicalcabinet.services.ConsultationService;
import medicalcabinet.services.DoctorRestClient;
import medicalcabinet.services.PatientRestClient;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssistantDashboardPresenter {
    private AssistantDashboardView view;
    private PatientRestClient patientClient;
    private ConsultationService consultationService;
    private DoctorRestClient doctorClient;

    public AssistantDashboardPresenter(AssistantDashboardView view) {
        this.view = view;
        this.patientClient = new PatientRestClient();
        this.consultationService = new ConsultationService(new ConsultationRestClient());
        this.doctorClient = new DoctorRestClient();

        loadPatients();
    }

    public void loadPatients() {
        view.displayPatients(patientClient.getAllPatients());
    }

    public void onSearchClicked(String name) {
        if (name == null || name.trim().isEmpty()) {
            loadPatients();
            return;
        }
        List<PatientDTO> filtered = patientClient.getAllPatients().stream()
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
            if (patientClient.savePatient(newPatient)) {
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
            if (patientClient.deletePatient(id)) {
                view.showMessage("Pacient șters!");
                loadPatients();
            } else {
                view.showMessage("Eroare la ștergerea pacientului.");
            }
        }
    }

    public void onScheduleAppointmentClicked() {
        List<PatientDTO> allPatients = patientClient.getAllPatients();
        Map<Integer, String> allDoctors = new HashMap<>();

        List<DoctorDTO> doctors = doctorClient.getAllDoctors();
        for (DoctorDTO doc : doctors) {
            allDoctors.put(doc.getId(), doc.getFullName());
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

        List<PatientDTO> currentPatients = patientClient.getAllPatients();

        medicalcabinet.core.PluginManager manager = new medicalcabinet.core.PluginManager();
        List<medicalcabinet.domain.plugincontracts.IExportPlugin> plugins = manager.loadExportPlugins("plugins_folder");

        for (medicalcabinet.domain.plugincontracts.IExportPlugin plugin : plugins) {
            if (plugin.getFormatName().equalsIgnoreCase(format)) {
                view.showMessage("Export " + format + " realizat cu succes la adresa: " + path);
                return;
            }
        }
        view.showMessage("Plugin-ul pentru formatul " + format + " nu a fost găsit în folderul 'plugins_folder'.");
    }

    public void onStatisticsClicked() {
        List<PatientDTO> allPatients = patientClient.getAllPatients();
        List<ConsultationDTO> allConsultations = new ConsultationRestClient().getAllConsultations();

        medicalcabinet.core.PluginManager manager = new medicalcabinet.core.PluginManager();
        List<medicalcabinet.domain.plugincontracts.IStatisticsPlugin> statPlugins = manager.loadStatisticsPlugins("plugins_folder");

        if (statPlugins == null || statPlugins.isEmpty()) {
            view.showMessage("Nu a fost găsit niciun plugin de statistici (JAR) în folderul 'plugins_folder'!");
            return;
        }

        int chartsOpened = 0;
        for (medicalcabinet.domain.plugincontracts.IStatisticsPlugin plugin : statPlugins) {
            if (plugin.getClass().getName().toLowerCase().contains("audit")) {
                continue;
            }
            try {
                plugin.generatePatientStatisticsChart(allPatients, allConsultations);
                chartsOpened++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (chartsOpened > 0) {
            view.showMessage("Succes! Au fost generate " + chartsOpened + " grafice.");
        }
    }

    public void onAdvancedFilterClicked(String medicName, String diagnosis, String ageStr) {
        List<PatientDTO> allPatients = patientClient.getAllPatients();

        if (ageStr != null && !ageStr.trim().isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr.trim());
                allPatients = allPatients.stream()
                        .filter(p -> p.getAge() == age)
                        .collect(Collectors.toList());
            } catch (NumberFormatException ex) {
                view.showMessage("Eroare: Vârsta introdusă trebuie să fie un număr valid.");
                return;
            }
        }

        if ((diagnosis != null && !diagnosis.trim().isEmpty()) || (medicName != null && !medicName.trim().isEmpty())) {
            List<ConsultationDTO> allConsultations = new ConsultationRestClient().getAllConsultations();

            allPatients = allPatients.stream().filter(patient -> {
                List<ConsultationDTO> patientConsultations = allConsultations.stream()
                        .filter(c -> c.getPatientId() == patient.getId())
                        .collect(Collectors.toList());

                for (ConsultationDTO consult : patientConsultations) {
                    boolean matchesDiagnosis = true;
                    boolean matchesMedic = true;

                    if (diagnosis != null && !diagnosis.trim().isEmpty()) {
                        matchesDiagnosis = consult.getDiagnosis() != null &&
                                consult.getDiagnosis().toLowerCase().contains(diagnosis.toLowerCase());
                    }

                    if (medicName != null && !medicName.trim().isEmpty()) {
                        DoctorDTO doctor = doctorClient.getDoctorById(consult.getDoctorId());
                        matchesMedic = doctor != null &&
                                doctor.getFullName().toLowerCase().contains(medicName.toLowerCase());
                    }

                    if (matchesDiagnosis && matchesMedic) return true;
                }
                return false;
            }).collect(Collectors.toList());
        }

        view.displayPatients(allPatients);
    }
}