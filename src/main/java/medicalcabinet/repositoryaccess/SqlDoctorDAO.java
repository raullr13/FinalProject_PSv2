package medicalcabinet.repositoryaccess;

import medicalcabinet.domain.dtos.DoctorDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlDoctorDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<String> getAllSpecializations() {
        List<String> specs = new ArrayList<>();
        String query = "SELECT DISTINCT specialization FROM Doctors_Info";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                specs.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return specs;
    }

    public List<DoctorDTO> getDoctorsBySpecialization(String spec) {
        return fetchDoctors("SELECT * FROM Doctors_Info WHERE specialization = ?", spec);
    }

    public List<DoctorDTO> searchDoctorsByName(String name) {
        return fetchDoctors("SELECT * FROM Doctors_Info WHERE full_name LIKE ?", "%" + name + "%");
    }

    private List<DoctorDTO> fetchDoctors(String query, String param) {
        List<DoctorDTO> doctors = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, param);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    doctors.add(new DoctorDTO(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("specialization"),
                            rs.getString("cv_text"),
                            rs.getString("photo_path"),
                            rs.getString("schedule")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }
}