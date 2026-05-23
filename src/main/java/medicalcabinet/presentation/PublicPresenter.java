package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.DoctorDTO;
// NEW IMPORT
import medicalcabinet.services.DoctorRestClient;

public class PublicPresenter {
    private IPublicView view;
    private DoctorRestClient doctorClient;

    public PublicPresenter(IPublicView view) {
        this.view = view;
        this.doctorClient = new DoctorRestClient();
        loadSpecializations();
    }

    public void loadSpecializations() {
        view.displaySpecializations(doctorClient.getAllSpecializations());
    }

    public void onSpecializationSelected(String specialization) {
        if (specialization != null) {
            view.displayDoctors(doctorClient.getDoctorsBySpecialization(specialization));
        }
    }

    public void onSearchClicked() {
        String query = view.getSearchQuery();
        if (query != null && !query.trim().isEmpty()) {
            view.displayDoctors(doctorClient.searchDoctorsByName(query));
        } else {
            view.showMessage("Please enter a name to search.");
        }
    }



}