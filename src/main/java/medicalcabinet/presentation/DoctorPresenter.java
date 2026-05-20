package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.repositoryaccess.SqlConsultationDAO;
import medicalcabinet.repositoryaccess.SqlPatientDAO;
import medicalcabinet.services.ConsultationService;

import java.util.List;
import java.util.stream.Collectors;

public class DoctorPresenter {
    private DoctorDashboardView view;
    private UserDTO loggedInDoctor;

    private ConsultationService consultationService;
    private SqlPatientDAO patientDAO;

    private List<ConsultationDTO> currentDoctorConsultations;

    public DoctorPresenter(DoctorDashboardView view, UserDTO loggedInDoctor) {
        this.view = view;
        this.loggedInDoctor = loggedInDoctor;
        this.consultationService = new ConsultationService(new SqlConsultationDAO());
        this.patientDAO = new SqlPatientDAO();

        loadAllMyPatients();
    }

    public void loadAllMyPatients() {
        currentDoctorConsultations = consultationService.getDoctorConsultations(loggedInDoctor.getId());
        updateViewTable(currentDoctorConsultations);
    }

    public void onFilterClicked(String diagnosis, String treatment) {
        List<ConsultationDTO> filtered = consultationService.filterDoctorConsultations(loggedInDoctor.getId(), diagnosis, treatment);
        updateViewTable(filtered);
    }

    public void onSearchClicked(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) {
            loadAllMyPatients();
            return;
        }

        List<ConsultationDTO> searched = currentDoctorConsultations.stream()
                .filter(c -> {
                    PatientDTO p = patientDAO.findById(c.getPatientId());
                    return p != null && p.getFullName().toLowerCase().contains(patientName.toLowerCase());
                })
                .collect(Collectors.toList());

        updateViewTable(searched);

        if (searched.isEmpty()) {
            view.showMessage("Nu am găsit niciun pacient cu numele: " + patientName);
        }
    }

    public void onUpdateRecordClicked(int consultationId, String newSymptoms, String newDiagnosis, String newTreatment) {
        ConsultationDTO toUpdate = new ConsultationDTO();
        toUpdate.setId(consultationId);
        toUpdate.setDoctorId(loggedInDoctor.getId());
        toUpdate.setSymptoms(newSymptoms);
        toUpdate.setDiagnosis(newDiagnosis);
        toUpdate.setTreatment(newTreatment);

        if (consultationService.updateConsultation(toUpdate)) {
            view.showMessage("Fișa medicală a fost actualizată cu succes!");
            loadAllMyPatients();
        } else {
            view.showMessage("Eroare la actualizarea fișei.");
        }
    }

    private void updateViewTable(List<ConsultationDTO> consultations) {
        Object[][] tableData = new Object[consultations.size()][6];

        for (int i = 0; i < consultations.size(); i++) {
            ConsultationDTO c = consultations.get(i);
            PatientDTO p = patientDAO.findById(c.getPatientId());
            String patientName = (p != null) ? p.getFullName() : "Necunoscut";

            tableData[i][0] = c.getId();
            tableData[i][1] = patientName;
            tableData[i][2] = c.getConsultationDate().toString();
            tableData[i][3] = c.getSymptoms();
            tableData[i][4] = c.getDiagnosis();
            tableData[i][5] = c.getTreatment();
        }
        view.refreshTable(tableData);
    }
}