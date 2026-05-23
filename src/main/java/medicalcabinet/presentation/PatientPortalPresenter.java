package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.domain.dtos.UserDTO;

// 1. SWAPPED IMPORTS: No more java.sql or DAO!
import medicalcabinet.services.PatientRestClient;
import medicalcabinet.services.ConsultationRestClient;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PatientPortalPresenter {
    private PatientPortalView view;
    private UserDTO loggedInUser;
    private PatientDTO patientInfo;
    private List<ConsultationDTO> medicalHistory;

    // 2. SWAPPED VARIABLES
    private PatientRestClient patientClient;
    private ConsultationRestClient consultationClient;

    public PatientPortalPresenter(PatientPortalView view, UserDTO loggedInUser) {
        this.view = view;
        this.loggedInUser = loggedInUser;

        // 3. SWAPPED INSTANTIATION
        this.patientClient = new PatientRestClient();
        this.consultationClient = new ConsultationRestClient();

        loadMedicalRecord();
    }

    public void loadMedicalRecord() {
        this.patientInfo = patientClient.getPatientById(loggedInUser.getId());

        if (patientInfo != null) {
            this.medicalHistory = consultationClient.getConsultationsByPatientId(patientInfo.getId());
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

        String fileName = "Fisa_Medicala_" + patientInfo.getFullName().replace(" ", "_") + ".doc";

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
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