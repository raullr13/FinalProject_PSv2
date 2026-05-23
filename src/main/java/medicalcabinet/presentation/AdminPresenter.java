package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.repositoryaccess.SqlUserDAO;
import medicalcabinet.services.NotificationService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class AdminPresenter {
    private IAdminView view;
    private NotificationService notificationService;
    private SqlUserDAO userDAO;
    private List<UserDTO> allUsersCache; // Păstrăm o listă locală pentru filtrare rapidă

    public AdminPresenter(IAdminView view) {
        this.view = view;
        this.notificationService = new NotificationService();
        this.userDAO = new SqlUserDAO();

        loadUsers();
    }

    public void loadUsers() {
        // Fetch real data from MySQL
        allUsersCache = userDAO.getAllUsers();
        view.displayUsers(allUsersCache);
    }

    public void onFilterRoleChanged(String selectedRole) {
        if (selectedRole == null || selectedRole.equalsIgnoreCase("ALL")) {
            view.displayUsers(allUsersCache);
        } else {
            List<UserDTO> filtered = allUsersCache.stream()
                    .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(selectedRole))
                    .collect(java.util.stream.Collectors.toList());
            view.displayUsers(filtered);
        }
    }

    public void onExportCsvClicked(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Username,Role,Email");

            for (UserDTO u : allUsersCache) {
                writer.println(u.getId() + "," + u.getUsername() + "," + u.getRole() + "," + u.getEmail());
            }
            view.showMessage("Succes! Lista de utilizatori a fost exportată în: " + filePath);
        } catch (IOException e) {
            view.showMessage("Eroare la exportul CSV: " + e.getMessage());
        }
    }

    public void onAddUserClicked(String username, String password, String role, String email) {
        if (username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            view.showMessage("Toate câmpurile sunt obligatorii!");
            return;
        }

        boolean success = userDAO.insertUser(username, password, role, email);
        if (success) {
            view.showMessage("Utilizatorul '" + username + "' a fost adăugat cu succes în baza de date.");
            loadUsers();
        } else {
            view.showMessage("Eroare: Nu s-a putut adăuga utilizatorul. Verificați dacă username-ul este duplicat.");
        }
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
            boolean success = userDAO.deleteUser(selected.getId());

            if (success) {
                view.showMessage("User deleted successfully from Database.");
                loadUsers();
            } else {
                view.showMessage("Error: Could not delete user from Database.");
            }
        } else {
            view.showMessage("Select a user to delete.");
        }
    }

    public void onUpdateUserClicked(String newUsername, String newPassword, String newRole, String newEmail) {
        UserDTO selectedUser = view.getSelectedUser();

        if (selectedUser == null) {
            view.showMessage("Te rog să selectezi un utilizator din listă pentru a-l actualiza.");
            return;
        }

        if (newUsername == null || newUsername.trim().isEmpty() ||
                newEmail == null || newEmail.trim().isEmpty() ||
                newRole == null || newRole.trim().isEmpty()) {
            view.showMessage("Câmpurile username, rol și email sunt obligatorii!");
            return;
        }

        boolean success = userDAO.updateUser(selectedUser.getId(), newUsername, newPassword, newRole, newEmail);

        if (success) {
            view.showMessage("Utilizatorul a fost actualizat cu succes!");
            loadUsers();

            medicalcabinet.services.UserNotificationManager notificationManager = new medicalcabinet.services.UserNotificationManager();

            String changeDetails = "Username: " + newUsername + ", Rol: " + newRole;
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                changeDetails += " (Parola a fost schimbată)";
            }

            notificationManager.notifyUserCredentialsChanged(newUsername, newEmail, changeDetails);

        } else {
            view.showMessage("Eroare la actualizarea utilizatorului în baza de date.");
        }
    }

}