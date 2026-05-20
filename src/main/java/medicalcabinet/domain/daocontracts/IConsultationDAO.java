package medicalcabinet.domain.daocontracts;

import medicalcabinet.domain.dtos.ConsultationDTO;
import java.util.List;

public interface IConsultationDAO {
    boolean save(ConsultationDTO consultation);
    boolean update(ConsultationDTO consultation);
    boolean delete(int id);
    List<ConsultationDTO> findAll();
    ConsultationDTO findById(int id);

    List<ConsultationDTO> getConsultationsByDoctorId(int doctorId);
    List<ConsultationDTO> getConsultationsByPatientId(int patientId);
}