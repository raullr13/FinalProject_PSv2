package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.services.AuthRestClient;
import medicalcabinet.presentation.utils.I18nManager; // Added for localized error messages

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
            view.showMessage(I18nManager.getString("login.error.empty", "Please enter both username and password."));
            return;
        }

        try {
            // This now makes a real HTTP POST call to your Spring Boot Backend
            UserDTO loggedInUser = authClient.authenticate(username, password);

            System.out.println("Login successful for user: " + username + " with role: " + loggedInUser.getRole());
            view.closeView();

            routeUserToDashboard(loggedInUser);

        } catch (Exception e) {
            view.showMessage(e.getMessage());
        }
    }

    private void routeUserToDashboard(UserDTO user) {
        switch (user.getRole()) {
            case ASSISTANT:
                AssistantDashboardView assistantView = new AssistantDashboardView();
                // Ensure this constructor is using your new AssistantDashboardPresenter with RestClients
                AssistantDashboardPresenter assistantPresenter = new AssistantDashboardPresenter(assistantView);
                assistantView.setPresenter(assistantPresenter);
                assistantView.setVisible(true);
                break;
            case ADMINISTRATOR:
                AdminDashboardView adminView = new AdminDashboardView();
                AdminPresenter adminPresenter = new AdminPresenter(adminView);
                adminView.setPresenter(adminPresenter);
                adminView.setVisible(true);
                break;
            case DOCTOR:
                DoctorDashboardView doctorView = new DoctorDashboardView(user.getUsername());
                DoctorPresenter doctorPresenter = new DoctorPresenter(doctorView, user);
                doctorView.setPresenter(doctorPresenter);
                doctorView.setVisible(true);
                break;
            case PATIENT:
                PatientPortalView portalView = new PatientPortalView();
                PatientPortalPresenter portalPresenter = new PatientPortalPresenter(portalView, user);
                portalView.setPresenter(portalPresenter);
                portalView.setVisible(true);
                break;
        }
    }
}