import medicalcabinet.presentation.LoginView;
import medicalcabinet.presentation.LoginPresenter;
import medicalcabinet.services.AuthRestClient;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize the UI (View)
        LoginView loginView = new LoginView();

        // 2. Initialize the Mock REST Client (Model/Service)
        AuthRestClient authClient = new AuthRestClient();

        // 3. Initialize the Presenter and wire them together
        LoginPresenter loginPresenter = new LoginPresenter(loginView, authClient);
        loginView.setPresenter(loginPresenter);

        // 4. Start the Application
        loginView.setVisible(true);
    }
}