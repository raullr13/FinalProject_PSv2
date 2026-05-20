package medicalcabinet.repositoryaccess;

import medicalcabinet.domain.daocontracts.IConsultationDAO;
import medicalcabinet.domain.dtos.ConsultationDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlConsultationDAO implements IConsultationDAO {

    public List<ConsultationDTO> getConsultationsByDoctorId(int doctorId) {
        List<ConsultationDTO> list = new ArrayList<>();
        String query = "SELECT * FROM Consultations WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ConsultationDTO> getConsultationsByPatientId(int patientId) {
        List<ConsultationDTO> list = new ArrayList<>();
        String query = "SELECT * FROM Consultations WHERE patient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean save(ConsultationDTO consultation) {
        String query = "INSERT INTO Consultations (patient_id, doctor_id, consultation_date, symptoms, diagnosis, treatment) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, consultation.getPatientId());
            stmt.setInt(2, consultation.getDoctorId());
            stmt.setDate(3, Date.valueOf(consultation.getConsultationDate()));
            stmt.setString(4, consultation.getSymptoms());
            stmt.setString(5, consultation.getDiagnosis());
            stmt.setString(6, consultation.getTreatment());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //permite Medicului să actualizeze simptomele, diagnosticul și tratamentul
    @Override
    public boolean update(ConsultationDTO consultation) {
        String query = "UPDATE Consultations SET symptoms = ?, diagnosis = ?, treatment = ? WHERE id = ? AND doctor_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, consultation.getSymptoms());
            stmt.setString(2, consultation.getDiagnosis());
            stmt.setString(3, consultation.getTreatment());
            stmt.setInt(4, consultation.getId());
            stmt.setInt(5, consultation.getDoctorId()); // Ensure they only update their OWN records
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String query = "DELETE FROM Consultations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method
    private ConsultationDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new ConsultationDTO(
                rs.getInt("id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"), // Extrage noua coloană
                rs.getDate("consultation_date").toLocalDate(),
                rs.getString("symptoms"),
                rs.getString("diagnosis"),
                rs.getString("treatment")
        );
    }

    @Override
    public List<ConsultationDTO> findAll() { return new ArrayList<>(); }

    @Override
    public ConsultationDTO findById(int id) { return null; }
}