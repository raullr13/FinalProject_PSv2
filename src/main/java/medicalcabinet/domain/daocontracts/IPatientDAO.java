package medicalcabinet.domain.daocontracts;

import java.time.LocalDate;
import java.util.List;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.domain.dtos.ConsultationDTO;

public interface IPatientDAO {
    List<PatientDTO> getAllPatients();
    List<PatientDTO> getPatientsByName(String name);
    boolean insertPatient(PatientDTO patient);
    boolean updatePatient(PatientDTO patient);
    boolean deletePatient(int id);
}

