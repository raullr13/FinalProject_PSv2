package medicalcabinet.services;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.domain.dtos.UserRole;

public class AuthRestClient {

    // Simulates a POST /api/auth/login request to the IAM Microservice
    public UserDTO authenticate(String username, String password) throws Exception {
        Thread.sleep(500);

        // Added ID (int) and Email (String) to match the new UserDTO constructor
        if (username.equals("admin") && password.equals("admin123")) {
            return new UserDTO(1, username, UserRole.ADMINISTRATOR, "admin@clinic.ro");
        } else if (username.equals("medic") && password.equals("medic123")) {
            return new UserDTO(2, username, UserRole.DOCTOR, "doctor@clinic.ro");
        } else if (username.equals("asistent") && password.equals("asistent123")) {
            return new UserDTO(3, username, UserRole.ASSISTANT, "asistent@clinic.ro");
        } else if (username.equals("pacient") && password.equals("pacient123")) {
            return new UserDTO(4, username, UserRole.PATIENT, "pacient@clinic.ro");
        }

        throw new Exception("Invalid username or password! (401 Unauthorized)");
    }
}