package medicalcabinet.network;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.services.PatientService;
import java.util.List;

public class PatientRestClient {

    private PatientService remoteBackendService;

    public PatientRestClient(PatientService remoteBackendService) {
        this.remoteBackendService = remoteBackendService;
    }

    public List<PatientDTO> getAllPatients() throws Exception {
        simulateNetworkLatency();
        return remoteBackendService.getAllPatients();
    }

    public void createPatient(PatientDTO patient) throws Exception {
        simulateNetworkLatency();
        boolean success = remoteBackendService.addPatient(patient);
        if (!success) {
            throw new Exception("409 Conflict: Could not save patient (Check CNP).");
        }
    }

    public void updatePatient(PatientDTO patient) throws Exception {
        simulateNetworkLatency();
        boolean success = remoteBackendService.updatePatient(patient);
        if (!success) {
            throw new Exception("400 Bad Request: Update failed.");
        }
    }

    public void deletePatient(String patientId) throws Exception {
        simulateNetworkLatency();
        try {
            int id = Integer.parseInt(patientId);
            boolean success = remoteBackendService.deletePatient(id);
            if (!success) {
                throw new Exception("404 Not Found: Patient does not exist.");
            }
        } catch (NumberFormatException e) {
            throw new Exception("400 Bad Request: Invalid ID format in URL.");
        }
    }

    public List<PatientDTO> searchPatients(String name) throws Exception {
        simulateNetworkLatency();
        return remoteBackendService.searchPatientByName(name);
    }

    private void simulateNetworkLatency() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}