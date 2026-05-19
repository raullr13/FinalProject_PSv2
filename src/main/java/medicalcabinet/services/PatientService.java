package medicalcabinet.services;

import java.util.List;
import java.util.ArrayList;
import medicalcabinet.domain.daocontracts.IPatientDAO;
import medicalcabinet.domain.dtos.PatientDTO;

public class PatientService {
    private final IPatientDAO patientDAO;


    public PatientService(IPatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    public List<PatientDTO> getAllPatients() {
        return patientDAO.getAllPatients();
    }

    public List<PatientDTO> searchPatientByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return patientDAO.getPatientsByName(name);
    }

    public boolean addPatient(PatientDTO patient) {
        if (patient.getCnp() == null || patient.getCnp().length() != 13) {
            throw new IllegalArgumentException("Invalid CNP length.");
        }
        return patientDAO.insertPatient(patient);
    }

    public boolean updatePatient(PatientDTO patient) {
        if (patient.getCnp() == null || patient.getCnp().length() != 13) {
            throw new IllegalArgumentException("Invalid CNP length.");
        }
        return patientDAO.updatePatient(patient);
    }

    public boolean deletePatient(int id) {
        return patientDAO.deletePatient(id);
    }

    public List<PatientDTO> searchPatientsByName(String name) {
        return patientDAO.getAllPatients().stream()
                .filter(p -> p.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
}