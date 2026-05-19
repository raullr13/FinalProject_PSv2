package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.network.PatientRestClient; // Import the REST Client
import java.util.List;

public class PatientPresenter {
    private IPatientView view;
    private PatientRestClient restClient; // Replaced PatientService

    public PatientPresenter(IPatientView view, PatientRestClient restClient) {
        this.view = view;
        this.restClient = restClient;
        loadAllPatients(); // Load data when presenter initializes
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
            // Open the dialog pre-filled with the selected patient's data
            PatientDTO updatedPatient = view.showPatientFormDialog(selectedPatient);

            if (updatedPatient != null) {
                try {
                    // Simulate PUT Request
                    restClient.updatePatient(updatedPatient);
                    view.showMessage("200 OK: Patient updated successfully!");
                    loadAllPatients(); // Refresh the table
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
                // Pass the ID as a String, simulating a URL parameter: /api/patients/5
                restClient.deletePatient(String.valueOf(selectedPatient.getId()));
                view.showMessage("200 OK: Patient deleted successfully!");
                loadAllPatients(); // Refresh the table
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
                // If the search bar is empty, just load everyone
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

    // --------------------------------------------------------
    // CROSS-MICROSERVICE & PLUGIN ROUTING METHODS
    // --------------------------------------------------------

    public void onViewMedicalRecordClicked() {
        PatientDTO selectedPatient = view.getSelectedPatient();

        if (selectedPatient != null) {
            // 1. We tell the user we are transitioning to the Medical Records Service
            view.showMessage("Accessing Medical Records Microservice for: " + selectedPatient.getFullName());

            // 2. Instantiate the Consultation MVP for this specific patient
            // (In a real production app, this would open ConsultationView.java)
            // medicalcabinet.presentation.ConsultationView consultView = new medicalcabinet.presentation.ConsultationView();
            // ... setup presenter ...
            // consultView.setVisible(true);

        } else {
            view.showMessage("Please select a patient from the table to view their medical record.");
        }
    }

    public void onDemographicsClicked() {
        // This simulates a call to the Reporting Microservice (Microkernel)
        try {
            view.showMessage("Connecting to Reporting Microservice...\nLoading AgeDemographicsPlugin.jar via ServiceLoader...");

            // Here you would normally pass the List of Patients to the PluginManager
            // PluginManager manager = new PluginManager("plugins_folder");
            // manager.loadPlugins();
            // manager.executeStatistics("Age Demographics", restClient.getAllPatients());

        } catch (Exception e) {
            view.showMessage("Microkernel Error: " + e.getMessage());
        }
    }

    public void onAuditClicked() {
        // This simulates a call to the Reporting Microservice (Microkernel)
        try {
            view.showMessage("Connecting to Reporting Microservice...\nLoading DataAuditPlugin.jar via ServiceLoader...");

            // Simulated Plugin Execution
            // PluginManager manager = new PluginManager("plugins_folder");
            // manager.executeAudit(restClient.getAllPatients());

        } catch (Exception e) {
            view.showMessage("Microkernel Error: " + e.getMessage());
        }
    }

}