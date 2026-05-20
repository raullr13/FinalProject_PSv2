import medicalcabinet.presentation.PublicAppView;
import medicalcabinet.presentation.PublicPresenter;

public class Main {
    public static void main(String[] args) {
        PublicAppView publicView = new PublicAppView();
        PublicPresenter publicPresenter = new PublicPresenter(publicView);
        publicView.setPresenter(publicPresenter);
        publicView.setVisible(true);
    }
}