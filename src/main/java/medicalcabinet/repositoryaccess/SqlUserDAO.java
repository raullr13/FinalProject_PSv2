package medicalcabinet.repositoryaccess;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.domain.dtos.UserRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlUserDAO {
    // Uses your existing DatabaseConnection class
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String query = "SELECT id, username, role, email FROM Users";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                UserRole role = UserRole.valueOf(rs.getString("role")); // Must match exactly: ADMINISTRATOR, DOCTOR, etc.
                String email = rs.getString("email");

                users.add(new UserDTO(id, username, role, email));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(int id) {
        String query = "DELETE FROM Users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertUser(String username, String password, String role, String email) {
        String query = "INSERT INTO Users (username, password, role, email) VALUES (?, ?, ?, ?)";

        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role); // Salvează valoarea (ex: "DOCTOR") ca text
            stmt.setString(4, email);

            return stmt.executeUpdate() > 0;

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}