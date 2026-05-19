package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.PatientDTO;
import java.util.List;

public interface IPatientView {
    void setPresenter(PatientPresenter presenter);
    String getSearchText();
    PatientDTO showPatientFormDialog(PatientDTO patientToEdit);
    PatientDTO getSelectedPatient();
    void displayPatients(List<PatientDTO> patients);
    void showMessage(String message);
}