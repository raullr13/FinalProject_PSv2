package medicalcabinet.services;

import medicalcabinet.domain.daocontracts.IConsultationDAO;
import medicalcabinet.domain.dtos.ConsultationDTO;

import java.time.LocalDate;
import java.util.List;

public class ConsultationService {
    private final IConsultationDAO consultationDAO;

    public ConsultationService(IConsultationDAO consultationDAO) {
        this.consultationDAO = consultationDAO;
    }

    public List<ConsultationDTO> getPatientMedicalRecord(int patientId) {
        return consultationDAO.getConsultationsByPatientId(patientId);
    }

    public boolean addConsultation(ConsultationDTO consultation) {
        return consultationDAO.insertConsultation(consultation);
    }

    public boolean updateConsultation(ConsultationDTO consultation) {
        return consultationDAO.updateConsultation(consultation);
    }

    public boolean deleteConsultation(int id) {
        return consultationDAO.deleteConsultation(id);
    }

    public List<ConsultationDTO> filterConsultations(int patientId, LocalDate date, String diagnosis) {
        return consultationDAO.filterConsultations(patientId, date, diagnosis);
    }
}
