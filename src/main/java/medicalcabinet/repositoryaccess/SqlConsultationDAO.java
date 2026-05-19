package medicalcabinet.repositoryaccess;

import medicalcabinet.domain.daocontracts.IConsultationDAO;
import medicalcabinet.domain.dtos.ConsultationDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SqlConsultationDAO implements IConsultationDAO {

    @Override
    public List<ConsultationDTO> getConsultationsByPatientId(int patientId) {
        List<ConsultationDTO> consultations = new ArrayList<>();
        String sql = "SELECT * FROM Consultations WHERE PatientId = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ConsultationDTO dto = new ConsultationDTO();
                dto.setId(rs.getInt("Id"));
                dto.setPatientId(rs.getInt("PatientId"));
                dto.setConsultationDate(rs.getDate("ConsultationDate").toLocalDate());
                dto.setSymptoms(rs.getString("Symptoms"));
                dto.setDiagnosis(rs.getString("Diagnosis"));
                dto.setTreatment(rs.getString("Treatment"));
                consultations.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultations;
    }

    @Override
    public boolean insertConsultation(ConsultationDTO consultation) {
        String sql = "INSERT INTO Consultations (PatientId, ConsultationDate, Symptoms, Diagnosis, Treatment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, consultation.getPatientId());
            pstmt.setDate(2, Date.valueOf(consultation.getConsultationDate()));
            pstmt.setString(3, consultation.getSymptoms());
            pstmt.setString(4, consultation.getDiagnosis());
            pstmt.setString(5, consultation.getTreatment());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateConsultation(ConsultationDTO consultation) {
        String sql = "UPDATE Consultations SET ConsultationDate=?, Symptoms=?, Diagnosis=?, Treatment=? WHERE Id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(consultation.getConsultationDate()));
            pstmt.setString(2, consultation.getSymptoms());
            pstmt.setString(3, consultation.getDiagnosis());
            pstmt.setString(4, consultation.getTreatment());
            pstmt.setInt(5, consultation.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteConsultation(int id) {
        String sql = "DELETE FROM Consultations WHERE Id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ConsultationDTO> filterConsultations(int patientId, LocalDate date, String diagnosis) {
        List<ConsultationDTO> consultations = new ArrayList<>();

        StringBuilder sql = new StringBuilder("SELECT * FROM Consultations WHERE PatientId = ?");

        if (date != null) {
            sql.append(" AND ConsultationDate = ?");
        }
        if (diagnosis != null && !diagnosis.trim().isEmpty()) {
            sql.append(" AND Diagnosis LIKE ?");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            pstmt.setInt(paramIndex++, patientId);

            if (date != null) {
                pstmt.setDate(paramIndex++, Date.valueOf(date));
            }
            if (diagnosis != null && !diagnosis.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + diagnosis.trim() + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ConsultationDTO dto = new ConsultationDTO();
                    dto.setId(rs.getInt("Id"));
                    dto.setPatientId(rs.getInt("PatientId"));
                    dto.setConsultationDate(rs.getDate("ConsultationDate").toLocalDate());
                    dto.setSymptoms(rs.getString("Symptoms"));
                    dto.setDiagnosis(rs.getString("Diagnosis"));
                    dto.setTreatment(rs.getString("Treatment"));
                    consultations.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consultations;
    }
}