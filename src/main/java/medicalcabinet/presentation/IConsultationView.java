package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import java.time.LocalDate;
import java.util.List;

public interface IConsultationView {
    LocalDate getFilterDate();
    String getFilterDiagnosis();

    ConsultationDTO getSelectedConsultation();
    ConsultationDTO showConsultationFormDialog(ConsultationDTO consultationToEdit, int patientId);

    void displayConsultations(List<ConsultationDTO> consultations);
    void showMessage(String message);

    String promptForSaveFilePath(String defaultExtension);
    void setPresenter(ConsultationPresenter presenter);
}
