package medicalcabinet.domain.daocontracts;

import medicalcabinet.domain.dtos.ConsultationDTO;

import java.time.LocalDate;
import java.util.List;

public interface IConsultationDAO {
    List<ConsultationDTO> getConsultationsByPatientId(int patientId);
    List<ConsultationDTO> filterConsultations(int patientId, LocalDate date, String diagnosis);
    boolean insertConsultation(ConsultationDTO consultation);
    boolean updateConsultation(ConsultationDTO consultation);
    boolean deleteConsultation(int id);
}
