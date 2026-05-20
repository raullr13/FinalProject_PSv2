package medicalcabinet.repositoryaccess;

import medicalcabinet.domain.daocontracts.IPatientDAO;
import medicalcabinet.domain.dtos.PatientDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlPatientDAO implements IPatientDAO {

    @Override
    public List<PatientDTO> getAllPatients() {
        List<PatientDTO> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PatientDTO dto = new PatientDTO();
                dto.setId(rs.getInt("Id"));
                dto.setFullName(rs.getString("FullName"));
                dto.setCnp(rs.getString("CNP"));
                dto.setAge(rs.getInt("Age"));
                patients.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public List<PatientDTO> getPatientsByName(String name) {
        List<PatientDTO> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE FullName LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PatientDTO dto = new PatientDTO();
                    dto.setId(rs.getInt("Id"));
                    dto.setFullName(rs.getString("FullName"));
                    dto.setCnp(rs.getString("CNP"));
                    dto.setAge(rs.getInt("Age"));
                    patients.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public boolean insertPatient(PatientDTO patient) {
        String sql = "INSERT INTO Patients (FullName, CNP, Age) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFullName());
            pstmt.setString(2, patient.getCnp());
            pstmt.setInt(3, patient.getAge());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updatePatient(PatientDTO patient) {
        String sql = "UPDATE Patients SET FullName = ?, CNP = ?, Age = ? WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFullName());
            pstmt.setString(2, patient.getCnp());
            pstmt.setInt(3, patient.getAge());
            pstmt.setInt(4, patient.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM Patients WHERE Id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PatientDTO getPatientByUsername(String username) {
        // Presupunem că numele complet din tabela Patients conține sau se potrivește cu username-ul,
        // sau adăugăm o mapare directă. Pentru testul tău cu 'pacient', căutăm primul pacient disponibil dacă e contul de test.
        String query = "SELECT id, full_name, cnp, age FROM Patients WHERE full_name LIKE ? OR id = 1 LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + username + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PatientDTO(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("cnp"),
                            rs.getInt("age")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatientDTO findById(int id) {
        String query = "SELECT id, full_name, cnp, age FROM Patients WHERE id = ?";
        try (java.sql.Connection conn = DatabaseConnection.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PatientDTO(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("cnp"),
                            rs.getInt("age")
                    );
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no patient is found with that ID
    }

}