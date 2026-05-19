package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.repositoryaccess.SqlUserDAO;
import medicalcabinet.services.NotificationService;
import java.util.List;

public class AdminPresenter {
    private IAdminView view;
    private NotificationService notificationService;
    private SqlUserDAO userDAO; // Replaced mockDatabase with real Database DAO

    public AdminPresenter(IAdminView view) {
        this.view = view;
        this.notificationService = new NotificationService();
        this.userDAO = new SqlUserDAO(); // Initialize the real database connection

        loadUsers(); // Fetch from MySQL immediately
    }

    public void loadUsers() {
        // Fetch real data from MySQL
        List<UserDTO> users = userDAO.getAllUsers();
        view.displayUsers(users);
    }

    public void onNotifyClicked() {
        UserDTO selectedUser = view.getSelectedUser();
        if (selectedUser != null) {
            try {
                view.showMessage("Sending actual email to: " + selectedUser.getEmail() + "...");

                String subject = "System Notification - Medical Cabinet";
                String body = "Hello " + selectedUser.getUsername() + ",\n\nYour account details have been updated by the Administrator.";

                // Real Email & Simulated SMS
                notificationService.sendEmail(selectedUser.getEmail(), subject, body);
                notificationService.sendSMS("0700000000", body);

                view.showMessage("200 OK: Email and SMS dispatched successfully!");
            } catch (Exception e) {
                view.showMessage("Failed to send email: " + e.getMessage() + "\n(Did you configure your App Password in NotificationService?)");
            }
        } else {
            view.showMessage("Please select a user to notify.");
        }
    }

    public void onDeleteClicked() {
        UserDTO selected = view.getSelectedUser();
        if (selected != null) {
            // Delete from real MySQL database
            boolean success = userDAO.deleteUser(selected.getId());

            if (success) {
                view.showMessage("User deleted successfully from Database.");
                loadUsers(); // Refresh the table from the DB
            } else {
                view.showMessage("Error: Could not delete user from Database.");
            }
        } else {
            view.showMessage("Select a user to delete.");
        }
    }
}