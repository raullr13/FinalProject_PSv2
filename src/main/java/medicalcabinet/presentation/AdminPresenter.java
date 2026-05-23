package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.domain.dtos.UserRole;
import medicalcabinet.services.UserRestClient;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class AdminPresenter {
    private IAdminView view;
    private UserRestClient userClient;
    private List<UserDTO> allUsersCache;

    public AdminPresenter(IAdminView view) {
        this.view = view;
        this.userClient = new UserRestClient();
        loadUsers();
    }

    public void loadUsers() {
        allUsersCache = userClient.getAllUsers();
        view.displayUsers(allUsersCache);
    }

    public void onFilterRoleChanged(String selectedRole) {
        if (selectedRole == null || selectedRole.equalsIgnoreCase("ALL")) {
            view.displayUsers(allUsersCache);
        } else {
            List<UserDTO> filtered = allUsersCache.stream()
                    .filter(u -> u.getRole() != null && u.getRole().name().equalsIgnoreCase(selectedRole))
                    .collect(Collectors.toList());
            view.displayUsers(filtered);
        }
    }

    public void onExportCsvClicked(String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("ID,Username,Role,Email");
            for (UserDTO u : allUsersCache) {
                writer.println(u.getId() + "," + u.getUsername() + "," + u.getRole() + "," + u.getEmail());
            }
            view.showMessage(I18nManager.getString("admin.export.success", "Success!"));
        } catch (IOException e) {
            view.showMessage(I18nManager.getString("admin.export.error", "Error:") + e.getMessage());
        }
    }

    public void onAddUserClicked(String username, String password, String role, String email) {
        if (username.trim().isEmpty() || password.trim().isEmpty() || email.trim().isEmpty()) {
            view.showMessage("Fields are required!");
            return;
        }

        UserDTO newUser = new UserDTO(0, username, UserRole.valueOf(role.toUpperCase()), email);

        if (userClient.saveUser(newUser)) {
            loadUsers();
        }
    }

    public void onNotifyClicked() {
        UserDTO selectedUser = view.getSelectedUser();
        if (selectedUser != null) {
            if (userClient.triggerNotification(selectedUser.getId())) {
                view.showMessage(I18nManager.getString("admin.notify.success", "Notification dispatched via server."));
            } else {
                view.showMessage(I18nManager.getString("admin.notify.fail", "Failed to dispatch notification."));
            }
        } else {
            view.showMessage(I18nManager.getString("admin.select.error", "Please select a user."));
        }
    }

    public void onDeleteClicked() {
        UserDTO selected = view.getSelectedUser();
        if (selected != null) {
            if (userClient.deleteUser(selected.getId())) {
                view.showMessage(I18nManager.getString("admin.delete.success", "User deleted."));
                loadUsers();
            } else {
                view.showMessage(I18nManager.getString("admin.delete.error", "Error deleting user."));
            }
        } else {
            view.showMessage(I18nManager.getString("admin.select.error", "Please select a user."));
        }
    }

    public void onUpdateUserClicked(String newUsername, String newPassword, String newRole, String newEmail) {
        UserDTO selectedUser = view.getSelectedUser();
        if (selectedUser == null) return;

        selectedUser.setUsername(newUsername);
        if (!newPassword.isEmpty()) {
            selectedUser.setPassword(newPassword);
        }
        selectedUser.setRole(UserRole.valueOf(newRole.toUpperCase()));
        selectedUser.setEmail(newEmail);

        userClient.updateUser(selectedUser);
        loadUsers();
    }
}