package medicalcabinet.services;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.services.PatientRestClient;

public class PatientService {

    private final PatientRestClient patientClient;

    public PatientService(PatientRestClient patientClient) {
        this.patientClient = patientClient;
    }

    public List<PatientDTO> getAllPatients() {
        return patientClient.getAllPatients();
    }

    public List<PatientDTO> searchPatientByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return patientClient.getAllPatients().stream()
                .filter(p -> p.getFullName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public boolean addPatient(PatientDTO patient) {
        if (patient.getCnp() == null || patient.getCnp().length() != 13) {
            throw new IllegalArgumentException("Invalid CNP length.");
        }
        return patientClient.savePatient(patient);
    }

    public boolean updatePatient(PatientDTO patient) {
        if (patient.getCnp() == null || patient.getCnp().length() != 13) {
            throw new IllegalArgumentException("Invalid CNP length.");
        }
        return patientClient.savePatient(patient);
    }

    public boolean deletePatient(int id) {
        return patientClient.deletePatient(id);
    }

    public List<PatientDTO> searchPatientsByName(String name) {
        return searchPatientByName(name);
    }
}