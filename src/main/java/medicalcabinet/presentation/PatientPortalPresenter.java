package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.repositoryaccess.SqlConsultationDAO;
import medicalcabinet.repositoryaccess.SqlPatientDAO;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PatientPortalPresenter {
    private PatientPortalView view;
    private UserDTO loggedInUser;
    private PatientDTO patientInfo;
    private List<ConsultationDTO> medicalHistory;

    private SqlPatientDAO patientDAO;
    private SqlConsultationDAO consultationDAO;

    public PatientPortalPresenter(PatientPortalView view, UserDTO loggedInUser) {
        this.view = view;
        this.loggedInUser = loggedInUser;
        this.patientDAO = new SqlPatientDAO();
        this.consultationDAO = new SqlConsultationDAO();

        loadMedicalRecord();
    }

    public void loadMedicalRecord() {
        // 1. Căutăm pacientul în baza de date folosind username-ul contului logat
        this.patientInfo = patientDAO.getPatientByUsername(loggedInUser.getUsername());

        if (patientInfo != null) {
            // 2. Extragem istoricul complet de consultații din tabela medicală
            this.medicalHistory = consultationDAO.getConsultationsByPatientId(patientInfo.getId());

            // 3. Trimitem datele reale către ferestrele din UI
            view.displayPatientInfo(patientInfo);
            view.displayMedicalHistory(medicalHistory);
        } else {
            view.showMessage("Eroare: Nu s-a găsit nicio fișă medicală asociată utilizatorului '" + loggedInUser.getUsername() + "'.");
        }
    }

    public void onExportToWordClicked() {
        if (patientInfo == null || medicalHistory == null) {
            view.showMessage("Nu există date medicale de exportat.");
            return;
        }

        // Generăm un nume unic pentru fișier bazat pe numele pacientului
        String fileName = "Fisa_Medicala_" + patientInfo.getFullName().replace(" ", "_") + ".doc";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Generăm structura fizică a documentului Word (.doc)
            writer.println("FIȘĂ MEDICALĂ PACIENT");
            writer.println("==================================================");
            writer.println("Nume Complet: " + patientInfo.getFullName());
            writer.println("CNP:          " + patientInfo.getCnp());
            writer.println("Vârstă:       " + patientInfo.getAge() + " ani");
            writer.println("==================================================");
            writer.println();
            writer.println("ISTORIC CONSULTAȚII:");
            writer.println("--------------------------------------------------");

            if (medicalHistory.isEmpty()) {
                writer.println("Nu există consultații înregistrate în sistem.");
            } else {
                for (ConsultationDTO c : medicalHistory) {
                    writer.println("Dată Consultație: " + c.getConsultationDate());
                    writer.println("Simptome:         " + (c.getSymptoms() != null ? c.getSymptoms() : "N/A"));
                    writer.println("Diagnostic:       " + (c.getDiagnosis() != null ? c.getDiagnosis() : "N/A"));
                    writer.println("Tratament:        " + (c.getTreatment() != null ? c.getTreatment() : "N/A"));
                    writer.println("--------------------------------------------------");
                }
            }

            view.showMessage("Succes! Fișa ta medicală a fost salvată în format Word: " + fileName);
        } catch (IOException e) {
            view.showMessage("Eroare la scrierea fișierului Word: " + e.getMessage());
        }
    }
}