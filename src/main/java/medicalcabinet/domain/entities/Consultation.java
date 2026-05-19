package medicalcabinet.domain.entities;

import java.time.LocalDate;

public class Consultation {
    private int id;
    private LocalDate consultationDate;
    private String symptoms;
    private String diagnosis;
    private String treatment;

    public Consultation(int id, LocalDate consultationDate, String symptoms, String diagnosis, String treatment) {
        this.id = id;
        this.consultationDate = consultationDate;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDate getConsultationDate() { return consultationDate; }
    public void setConsultationDate(LocalDate consultationDate) { this.consultationDate = consultationDate; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
}