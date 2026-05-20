package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.DoctorDTO;
import java.util.List;

public interface IPublicView {
    void displaySpecializations(List<String> specs);
    void displayDoctors(List<DoctorDTO> doctors);
    void displayDoctorDetails(DoctorDTO doctor);
    String getSearchQuery();
    void showMessage(String message);
}