package medicalcabinet.presentation;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame implements ILoginView {
    private LoginPresenter presenter;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView() {
        setTitle("Medical Cabinet - Secure Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Header
        JLabel headerLabel = new JLabel("IAM Authentication System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        add(formPanel, BorderLayout.CENTER);

        // Login Button Panel
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login via IAM Service");
        buttonPanel.add(loginButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Wire up the button
        loginButton.addActionListener(e -> {
            if (presenter != null) presenter.onLoginClicked();
        });

        setLocationRelativeTo(null);
    }

    @Override public String getUsername() { return usernameField.getText(); }
    @Override public String getPassword() { return new String(passwordField.getPassword()); }
    @Override public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }
    @Override public void closeView() { this.dispose(); }
    @Override public void setPresenter(LoginPresenter presenter) { this.presenter = presenter; }
}