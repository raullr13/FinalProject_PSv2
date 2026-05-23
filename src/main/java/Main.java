import medicalcabinet.presentation.PublicAppView;
import medicalcabinet.presentation.PublicPresenter;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("DEBUG: Starting application..."); // Add this
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        PublicAppView publicView = new PublicAppView();
        System.out.println("DEBUG: View initialized."); // Add this

        PublicPresenter publicPresenter = new PublicPresenter(publicView);
        publicView.setPresenter(publicPresenter);
        publicView.setVisible(true);
        System.out.println("DEBUG: setVisible(true) called."); // Add this
    }
}