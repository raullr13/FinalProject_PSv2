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
            UserDTO loggedInUser = authClient.authenticate(username, password);

            view.showMessage("Login Successful! Welcome " + loggedInUser.getRole().name());
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