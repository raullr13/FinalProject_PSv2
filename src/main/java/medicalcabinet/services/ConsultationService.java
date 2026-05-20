package medicalcabinet.services;

import medicalcabinet.domain.daocontracts.IConsultationDAO;
import medicalcabinet.domain.dtos.ConsultationDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ConsultationService {
    private final IConsultationDAO consultationDAO;

    public ConsultationService(IConsultationDAO consultationDAO) {
        this.consultationDAO = consultationDAO;
    }

    // --- For the Patient Portal ---
    public List<ConsultationDTO> getPatientMedicalRecord(int patientId) {
        return consultationDAO.getConsultationsByPatientId(patientId);
    }

    // --- NEW: For the Doctor Dashboard ---
    public List<ConsultationDTO> getDoctorConsultations(int doctorId) {
        return consultationDAO.getConsultationsByDoctorId(doctorId);
    }

    // --- Standard CRUD Operations (Fixed method names to match interface) ---
    public boolean addConsultation(ConsultationDTO consultation) {
        return consultationDAO.save(consultation); // Changed from insertConsultation
    }

    public boolean updateConsultation(ConsultationDTO consultation) {
        return consultationDAO.update(consultation); // Changed from updateConsultation
    }

    public boolean deleteConsultation(int id) {
        return consultationDAO.delete(id); // Changed from deleteConsultation
    }

    public List<ConsultationDTO> filterDoctorConsultations(int doctorId, String filterDiagnosis, String filterTreatment) {
        List<ConsultationDTO> allDocsConsultations = consultationDAO.getConsultationsByDoctorId(doctorId);

        return allDocsConsultations.stream()
                .filter(c -> filterDiagnosis == null || filterDiagnosis.isEmpty() ||
                        (c.getDiagnosis() != null && c.getDiagnosis().toLowerCase().contains(filterDiagnosis.toLowerCase())))
                .filter(c -> filterTreatment == null || filterTreatment.isEmpty() ||
                        (c.getTreatment() != null && c.getTreatment().toLowerCase().contains(filterTreatment.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<ConsultationDTO> filterConsultations(int patientId, LocalDate date, String diagnosis) {
        // Get all consultations for this specific patient
        List<ConsultationDTO> patientConsultations = consultationDAO.getConsultationsByPatientId(patientId);

        // Filter them dynamically based on the provided inputs
        return patientConsultations.stream()
                .filter(c -> date == null || c.getConsultationDate().equals(date))
                .filter(c -> diagnosis == null || diagnosis.trim().isEmpty() ||
                        (c.getDiagnosis() != null && c.getDiagnosis().toLowerCase().contains(diagnosis.toLowerCase())))
                .collect(java.util.stream.Collectors.toList());
    }
}