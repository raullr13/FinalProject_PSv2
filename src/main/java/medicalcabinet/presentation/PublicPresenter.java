package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.DoctorDTO;
import medicalcabinet.repositoryaccess.SqlDoctorDAO;

public class PublicPresenter {
    private IPublicView view;
    private SqlDoctorDAO doctorDAO;

    public PublicPresenter(IPublicView view) {
        this.view = view;
        this.doctorDAO = new SqlDoctorDAO();
        loadSpecializations();
    }

    public void loadSpecializations() {
        view.displaySpecializations(doctorDAO.getAllSpecializations());
    }

    public void onSpecializationSelected(String specialization) {
        if (specialization != null) {
            view.displayDoctors(doctorDAO.getDoctorsBySpecialization(specialization));
        }
    }

    public void onSearchClicked() {
        String query = view.getSearchQuery();
        if (query != null && !query.trim().isEmpty()) {
            view.displayDoctors(doctorDAO.searchDoctorsByName(query));
        } else {
            view.showMessage("Please enter a name to search.");
        }
    }
}