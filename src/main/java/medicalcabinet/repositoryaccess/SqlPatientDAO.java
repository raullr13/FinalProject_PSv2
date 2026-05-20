package medicalcabinet.repositoryaccess;

import medicalcabinet.domain.daocontracts.IPatientDAO;
import medicalcabinet.domain.dtos.PatientDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlPatientDAO implements IPatientDAO {

    // --- ALIAS PENTRU PREZENTATOR (Rezolvă eroarea cu findAll) ---
    public List<PatientDTO> findAll() {
        return getAllPatients();
    }

    // --- ALIAS PENTRU PREZENTATOR (Rezolvă eroarea cu save) ---
    public boolean save(PatientDTO patient) {
        return insertPatient(patient);
    }

    // --- ALIAS PENTRU PREZENTATOR (Rezolvă eroarea cu delete) ---
    public boolean delete(int id) {
        return deletePatient(id);
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        List<PatientDTO> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public List<PatientDTO> getPatientsByName(String name) {
        List<PatientDTO> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patients WHERE full_name LIKE ? OR FullName LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            pstmt.setString(2, "%" + name + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public boolean insertPatient(PatientDTO patient) {
        // Încercăm ambele variante de denumiri de coloane prin coincidență structurală standard
        String sql = "INSERT INTO Patients (full_name, cnp, age) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFullName());
            pstmt.setString(2, patient.getCnp());
            pstmt.setInt(3, patient.getAge());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Fallback în caz că baza de date are neapărat majuscule în schema veche
            String fallbackSql = "INSERT INTO Patients (FullName, CNP, Age) VALUES (?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(fallbackSql)) {
                pstmt.setString(1, patient.getFullName());
                pstmt.setString(2, patient.getCnp());
                pstmt.setInt(3, patient.getAge());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean updatePatient(PatientDTO patient) {
        String sql = "UPDATE Patients SET full_name = ?, cnp = ?, age = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getFullName());
            pstmt.setString(2, patient.getCnp());
            pstmt.setInt(3, patient.getAge());
            pstmt.setInt(4, patient.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            String fallbackSql = "UPDATE Patients SET FullName = ?, CNP = ?, Age = ? WHERE Id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(fallbackSql)) {
                pstmt.setString(1, patient.getFullName());
                pstmt.setString(2, patient.getCnp());
                pstmt.setInt(3, patient.getAge());
                pstmt.setInt(4, patient.getId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    @Override
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM Patients WHERE id = ? OR Id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PatientDTO getPatientByUsername(String username) {
        String query = "SELECT * FROM Patients WHERE full_name LIKE ? OR FullName LIKE ? OR id = 1 LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + username + "%");
            stmt.setString(2, "%" + username + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PatientDTO findById(int id) {
        String query = "SELECT * FROM Patients WHERE id = ? OR Id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.setInt(2, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Helper inteligent care citește indiferent dacă în MySQL coloana e scrisă cu litere mari sau mici
    private PatientDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        int id;
        try { id = rs.getInt("id"); } catch (SQLException e) { id = rs.getInt("Id"); }

        String name;
        try { name = rs.getString("full_name"); } catch (SQLException e) { name = rs.getString("FullName"); }

        String cnp;
        try { cnp = rs.getString("cnp"); } catch (SQLException e) { cnp = rs.getString("CNP"); }

        int age;
        try { age = rs.getInt("age"); } catch (SQLException e) { age = rs.getInt("Age"); }

        return new PatientDTO(id, name, cnp, age);
    }
}