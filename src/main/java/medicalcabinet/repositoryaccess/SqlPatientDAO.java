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

}