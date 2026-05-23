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


    public List<DoctorDTO> getAllDoctors() {
        List<DoctorDTO> doctors = new ArrayList<>();
        String query = "SELECT * FROM Doctors_Info";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public DoctorDTO findById(int id) {
        String query = "SELECT * FROM Doctors_Info WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new DoctorDTO(
                            rs.getInt("id"),
                            rs.getString("full_name"),
                            rs.getString("specialization"),
                            rs.getString("cv_text"),
                            rs.getString("photo_path"),
                            rs.getString("schedule")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertDoctor(String fullName, String specialization, String schedule) {
        // We set cv_text and photo_path to empty strings by default during creation
        String query = "INSERT INTO Doctors_Info (full_name, specialization, schedule, cv_text, photo_path) VALUES (?, ?, ?, '', '')";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, fullName);
            stmt.setString(2, specialization);
            stmt.setString(3, schedule);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDoctorSchedule(int id, String newSchedule) {
        String query = "UPDATE Doctors_Info SET schedule = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newSchedule);
            stmt.setInt(2, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDoctor(int id) {
        String query = "DELETE FROM Doctors_Info WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



}