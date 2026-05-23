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

import medicalcabinet.repositoryaccess.SqlDoctorDAO;
import medicalcabinet.domain.dtos.DoctorDTO;
import java.util.ArrayList;

public class AssistantDashboardPresenter {
    private AssistantDashboardView view;
    private SqlPatientDAO patientDAO;
    private ConsultationService consultationService;
    private SqlDoctorDAO doctorDAO;

    public AssistantDashboardPresenter(AssistantDashboardView view) {
        this.view = view;
        this.patientDAO = new SqlPatientDAO();
        this.consultationService = new ConsultationService(new SqlConsultationDAO());
        this.doctorDAO = new SqlDoctorDAO();

        loadPatients();
        // loadDoctors(); // Uncomment when you add the UI table for doctors
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
        try (java.sql.Connection conn = medicalcabinet.repositoryaccess.DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Consultations");
             java.sql.ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                allConsultations.add(new ConsultationDTO(
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("consultation_date").toLocalDate(),
                        rs.getString("symptoms"),
                        rs.getString("diagnosis"),
                        rs.getString("treatment")
                ));
            }
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

            if (plugin.getClass().getName().toLowerCase().contains("audit")) {
                continue;
            }

            try {
                plugin.generatePatientStatisticsChart(allPatients, allConsultations);
                chartsOpened++;
            } catch (Exception e) {
                System.out.println("Eroare la generarea graficului pentru: " + plugin.getChartType());
                e.printStackTrace();
            }
        }

        if (chartsOpened > 0) {
            view.showMessage("Succes! Au fost generate " + chartsOpened + " grafice de statistici.");
        }
    }

    public void onAdvancedFilterClicked(String medicName, String diagnosis, String ageStr) {
        List<PatientDTO> allPatients = patientDAO.findAll();

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

            List<ConsultationDTO> allConsultations = new ArrayList<>();
            try (java.sql.Connection conn = medicalcabinet.repositoryaccess.DatabaseConnection.getConnection();
                 java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Consultations");
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allConsultations.add(new ConsultationDTO(
                            rs.getInt("id"), rs.getInt("patient_id"), rs.getInt("doctor_id"),
                            rs.getDate("consultation_date").toLocalDate(),
                            rs.getString("symptoms"), rs.getString("diagnosis"), rs.getString("treatment")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


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
                        DoctorDTO doctor = doctorDAO.findById(consult.getDoctorId());
                        matchesMedic = doctor != null &&
                                doctor.getFullName().toLowerCase().contains(medicName.toLowerCase());
                    }

                    if (matchesDiagnosis && matchesMedic) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }

        view.displayPatients(allPatients);
    }

    public void loadDoctors() {
    }

    public void onAddDoctorClicked() {
        String username = JOptionPane.showInputDialog(view, "Nume Medic:");
        if (username == null || username.trim().isEmpty()) return;

        String specialization = JOptionPane.showInputDialog(view, "Specializare:");
        if (specialization == null || specialization.trim().isEmpty()) return;

        String schedule = JOptionPane.showInputDialog(view, "Program (ex: Luni-Vineri 08:00-14:00):");

        boolean success = doctorDAO.insertDoctor(username, specialization, schedule);
        if (success) {
            view.showMessage("Medic adăugat cu succes!");
            loadDoctors();
        } else {
            view.showMessage("Eroare la adăugarea medicului.");
        }
    }

    public void onUpdateDoctorClicked(int doctorId) {
        String newSchedule = JOptionPane.showInputDialog(view, "Introduceți noul program pentru medic:");
        if (newSchedule == null || newSchedule.trim().isEmpty()) return;

        boolean success = doctorDAO.updateDoctorSchedule(doctorId, newSchedule);
        if (success) {
            view.showMessage("Programul medicului a fost actualizat!");
            loadDoctors();
        } else {
            view.showMessage("Eroare la actualizarea medicului.");
        }
    }

    public void onDeleteDoctorClicked(int doctorId) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Sigur dorești să ștergi acest medic din sistem?",
                "Confirmare ștergere", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(doctorId)) {
                view.showMessage("Medic șters cu succes!");
                loadDoctors();
            } else {
                view.showMessage("Eroare la ștergerea medicului.");
            }
        }
    }



}