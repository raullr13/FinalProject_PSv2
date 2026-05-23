package medicalcabinet.domain.dtos;

public class UserDTO {
    private int id;
    private String username;
    private UserRole role;
    private String email;
    private String password;

    public UserDTO() {}

    public UserDTO(int id, String username, UserRole role, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
    }

    // Add Getters and Setters for all of these!
    public int getId() { return id; }
    public String getUsername() { return username; }
    public UserRole getRole() { return role; }
    public String getEmail() { return email; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setRole(UserRole role) { this.role = role; }
    public void setEmail(String email) { this.email = email; }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}