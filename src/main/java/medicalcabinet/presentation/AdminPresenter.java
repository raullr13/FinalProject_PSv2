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
                    // CORECTURĂ: Am adăugat .name() pentru a transforma Enum-ul în String
                    .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(selectedRole))
                    .collect(java.util.stream.Collectors.toList());
            view.displayUsers(filtered);
        }
    }

    // --- CERINȚA: EXPORT ÎN FORMAT CSV ---
    public void onExportCsvClicked(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Scriere Header CSV
            writer.println("ID,Username,Role,Email");

            // Scriere date utilizatori
            for (UserDTO u : allUsersCache) {
                writer.println(u.getId() + "," + u.getUsername() + "," + u.getRole() + "," + u.getEmail());
            }
            view.showMessage("Succes! Lista de utilizatori a fost exportată în: " + filePath);
        } catch (IOException e) {
            view.showMessage("Eroare la exportul CSV: " + e.getMessage());
        }
    }

    // --- CERINȚA: CRUD - ADĂUGARE UTILIZATOR ---
    public void onAddUserClicked(String username, String password, String role, String email) {
        if (username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            view.showMessage("Toate câmpurile sunt obligatorii!");
            return;
        }

        boolean success = userDAO.insertUser(username, password, role, email);
        if (success) {
            view.showMessage("Utilizatorul '" + username + "' a fost adăugat cu succes în baza de date.");
            loadUsers(); // Refresh tabel
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
}