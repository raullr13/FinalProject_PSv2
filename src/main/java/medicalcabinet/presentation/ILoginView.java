package medicalcabinet.presentation;

public interface ILoginView {
    String getUsername();
    String getPassword();
    void showMessage(String message);
    void closeView();
    void setPresenter(LoginPresenter presenter);
}