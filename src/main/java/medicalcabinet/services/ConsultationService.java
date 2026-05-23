package medicalcabinet.services;

import medicalcabinet.domain.dtos.ConsultationDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultationService {

    private final ConsultationRestClient consultationClient;

    public ConsultationService(ConsultationRestClient consultationClient) {
        this.consultationClient = consultationClient;
    }

    public List<ConsultationDTO> getPatientMedicalRecord(int patientId) {
        return consultationClient.getPatientMedicalRecord(patientId);
    }

    public List<ConsultationDTO> getDoctorConsultations(int doctorId) {
        return consultationClient.getDoctorConsultations(doctorId);
    }

    public boolean addConsultation(ConsultationDTO consultation) {
        return consultationClient.addConsultation(consultation);
    }

    public boolean updateConsultation(ConsultationDTO consultation) {
        return consultationClient.updateConsultation(consultation);
    }

    public boolean deleteConsultation(int id) {
        return consultationClient.deleteConsultation(id);
    }

    public List<ConsultationDTO> filterDoctorConsultations(int doctorId, String filterDiagnosis, String filterTreatment) {
        List<ConsultationDTO> allDocsConsultations = consultationClient.getDoctorConsultations(doctorId);

        return allDocsConsultations.stream()
                .filter(c -> filterDiagnosis == null || filterDiagnosis.isEmpty() ||
                        (c.getDiagnosis() != null && c.getDiagnosis().toLowerCase().contains(filterDiagnosis.toLowerCase())))
                .filter(c -> filterTreatment == null || filterTreatment.isEmpty() ||
                        (c.getTreatment() != null && c.getTreatment().toLowerCase().contains(filterTreatment.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<ConsultationDTO> filterConsultations(int patientId, LocalDate date, String diagnosis) {
        List<ConsultationDTO> patientConsultations = consultationClient.getPatientMedicalRecord(patientId);

        return patientConsultations.stream()
                .filter(c -> date == null || c.getConsultationDate().equals(date))
                .filter(c -> diagnosis == null || diagnosis.trim().isEmpty() ||
                        (c.getDiagnosis() != null && c.getDiagnosis().toLowerCase().contains(diagnosis.toLowerCase())))
                .collect(Collectors.toList());
    }
}