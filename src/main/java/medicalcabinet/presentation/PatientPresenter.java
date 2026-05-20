package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.network.PatientRestClient; // Import the REST Client
import java.util.List;

public class PatientPresenter {
    private IPatientView view;
    private PatientRestClient restClient;

    public PatientPresenter(IPatientView view, PatientRestClient restClient) {
        this.view = view;
        this.restClient = restClient;
        loadAllPatients();
    }

    public void loadAllPatients() {
        try {
            List<PatientDTO> patients = restClient.getAllPatients();
            view.displayPatients(patients);
        } catch (Exception e) {
            view.showMessage("API Error: " + e.getMessage());
        }
    }

    public void onAddPatientClicked() {
        PatientDTO newPatient = view.showPatientFormDialog(null);
        if (newPatient != null) {
            try {
                restClient.createPatient(newPatient); // POST request
                view.showMessage("201 Created: Patient saved successfully!");
                loadAllPatients();
            } catch (Exception e) {
                view.showMessage(e.getMessage());
            }
        }
    }

    public void onUpdatePatientClicked() {
        PatientDTO selectedPatient = view.getSelectedPatient();
        if (selectedPatient != null) {
            PatientDTO updatedPatient = view.showPatientFormDialog(selectedPatient);

            if (updatedPatient != null) {
                try {
                    // Simulate PUT Request
                    restClient.updatePatient(updatedPatient);
                    view.showMessage("200 OK: Patient updated successfully!");
                    loadAllPatients();
                } catch (Exception e) {
                    view.showMessage(e.getMessage());
                }
            }
        } else {
            view.showMessage("Please select a patient to update.");
        }
    }

    public void onDeletePatientClicked() {
        PatientDTO selectedPatient = view.getSelectedPatient();
        if (selectedPatient != null) {
            try {
                restClient.deletePatient(String.valueOf(selectedPatient.getId()));
                view.showMessage("200 OK: Patient deleted successfully!");
                loadAllPatients();
            } catch (Exception e) {
                view.showMessage(e.getMessage());
            }
        } else {
            view.showMessage("Please select a patient to delete.");
        }
    }

    public void onSearchButtonClicked() {
        String searchText = view.getSearchText();
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                loadAllPatients();
            } else {
                // Simulate GET Request with query parameters
                List<PatientDTO> searchResults = restClient.searchPatients(searchText);
                view.displayPatients(searchResults);

                if (searchResults.isEmpty()) {
                    view.showMessage("No patients found matching: " + searchText);
                }
            }
        } catch (Exception e) {
            view.showMessage("API Error: " + e.getMessage());
        }
    }


    public void onViewMedicalRecordClicked() {
        PatientDTO selectedPatient = view.getSelectedPatient();

        if (selectedPatient != null) {
            view.showMessage("Accessing Medical Records Microservice for: " + selectedPatient.getFullName());


        } else {
            view.showMessage("Please select a patient from the table to view their medical record.");
        }
    }

    public void onDemographicsClicked() {
        try {
            view.showMessage("Connecting to Reporting Microservice...\nLoading AgeDemographicsPlugin.jar via ServiceLoader...");


        } catch (Exception e) {
            view.showMessage("Microkernel Error: " + e.getMessage());
        }
    }

    public void onAuditClicked() {
        try {
            view.showMessage("Connecting to Reporting Microservice...\nLoading DataAuditPlugin.jar via ServiceLoader...");


        } catch (Exception e) {
            view.showMessage("Microkernel Error: " + e.getMessage());
        }
    }

}