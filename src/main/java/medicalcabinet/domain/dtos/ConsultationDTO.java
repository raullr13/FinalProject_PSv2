package medicalcabinet.domain.dtos;

import java.time.LocalDate;

public class ConsultationDTO {
    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate consultationDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;

    public ConsultationDTO() {
    }

    public ConsultationDTO(int id, int patientId, int doctorId, LocalDate consultationDate, String symptoms, String diagnosis, String treatment) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.consultationDate = consultationDate;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public LocalDate getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDate consultationDate) { this.consultationDate = consultationDate; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
}