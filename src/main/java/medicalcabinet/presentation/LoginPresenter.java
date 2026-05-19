package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.services.AuthRestClient;

public class LoginPresenter {
    private ILoginView view;
    private AuthRestClient authClient;

    public LoginPresenter(ILoginView view, AuthRestClient authClient) {
        this.view = view;
        this.authClient = authClient;
    }

    public void onLoginClicked() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showMessage("Please enter both username and password.");
            return;
        }

        try {
            // Attempt to authenticate via the REST Client
            UserDTO loggedInUser = authClient.authenticate(username, password);

            view.showMessage("Login Successful! Welcome " + loggedInUser.getRole().name());
            view.closeView();

            // Route to the correct Dashboard based on Role
            routeUserToDashboard(loggedInUser);

        } catch (Exception e) {
            view.showMessage(e.getMessage());
        }
    }

    private void routeUserToDashboard(UserDTO user) {
        switch (user.getRole()) {
            case ASSISTANT:
                PatientView patientView = new PatientView();

                // 1. Setup the "Backend Server"
                medicalcabinet.repositoryaccess.SqlPatientDAO patientDAO = new medicalcabinet.repositoryaccess.SqlPatientDAO();
                medicalcabinet.services.PatientService patientService = new medicalcabinet.services.PatientService(patientDAO);

                // 2. Setup the "Network Layer"
                medicalcabinet.network.PatientRestClient restClient = new medicalcabinet.network.PatientRestClient(patientService);

                // 3. Inject into Presenter
                PatientPresenter patientPresenter = new PatientPresenter(patientView, restClient);
                patientView.setPresenter(patientPresenter);
                patientView.setVisible(true);
                break;
            case ADMINISTRATOR:
                AdminDashboardView adminView = new AdminDashboardView();
                AdminPresenter adminPresenter = new AdminPresenter(adminView);
                adminView.setPresenter(adminPresenter);
                adminView.setVisible(true);
                break;
            case DOCTOR:
                // Doctor gets the Medical Records Update Dashboard
                DoctorDashboardView doctorView = new DoctorDashboardView();
                doctorView.setVisible(true);
                break;
            case PATIENT:
                // Patient gets a highly restricted view of their own data
                PatientPortalView portalView = new PatientPortalView(user.getUsername());
                portalView.setVisible(true);
                break;
        }
    }
}