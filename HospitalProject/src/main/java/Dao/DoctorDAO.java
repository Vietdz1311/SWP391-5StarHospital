package Dao;

import Dao.UserDAO;
import DBConnection.DBConnection;
import Model.Doctor;
import Model.Specialization;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    private Connection conn;

    public DoctorDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<Doctor> getAllDoctors(String search, Integer filterSpecialization, String sortBy, int page, int size) {
        List<Doctor> doctors = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Doctors WHERE 1=1");
        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND license_number LIKE ?");
        }
        if (filterSpecialization != null) {
            query.append(" AND specialization_id = ?");
        }
        query.append(" ORDER BY ");
        query.append((sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "id ASC");
        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                statement.setString(paramIndex++, "%" + search.trim() + "%");
            }
            if (filterSpecialization != null) {
                statement.setInt(paramIndex++, filterSpecialization);
            }
            statement.setInt(paramIndex++, (page - 1) * size);
            statement.setInt(paramIndex, size);

            ResultSet resultSet = statement.executeQuery();
            UserDAO userDao = new UserDAO();
             SpecializationDAO specializationDAO = new SpecializationDAO();
            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet, userDao, specializationDAO));
            }
        } catch (SQLException e) {
            System.out.println("Get all doctors error: " + e.getMessage());
        }
        return doctors;
    }

    public int getTotalDoctors(String search, Integer filterSpecialization) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM Doctors WHERE 1=1");
        if (search != null && !search.isEmpty()) {
            query.append(" AND license_number LIKE ?");
        }
        if (filterSpecialization != null) {
            query.append(" AND specialization_id = ?");
        }

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (search != null && !search.isEmpty()) {
                statement.setString(paramIndex++, "%" + search + "%");
            }
            if (filterSpecialization != null) {
                statement.setInt(paramIndex, filterSpecialization);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Get total doctors error: " + e.getMessage());
        }
        return 0;
    }

    public Doctor getDoctorById(int id) {
        String query = "SELECT * FROM Doctors WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            UserDAO userDao = new UserDAO();
            SpecializationDAO specializationDAO = new SpecializationDAO();
            if (resultSet.next()) {
                return mapDoctor(resultSet, userDao, specializationDAO);
            }
        } catch (SQLException e) {
            System.out.println("Get doctor by id error: " + e.getMessage());
        }
        return null;
    }
    
     public Doctor getHeaderDoctorById(int id) {
        String query = "SELECT * FROM Doctors WHERE user_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            UserDAO userDao = new UserDAO();
            SpecializationDAO specializationDAO = new SpecializationDAO();

            if (resultSet.next()) {
                return mapDoctor(resultSet, userDao, specializationDAO);
            }
        } catch (SQLException e) {
            System.out.println("Get doctor by id error: " + e.getMessage());
        }
        return null;
    }

    public List<Doctor> getFeaturedDoctors(int limit) {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT TOP (?) * FROM Doctors WHERE status = 'Active' ORDER BY id DESC";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
            UserDAO userDao = new UserDAO();
            SpecializationDAO specializationDAO = new SpecializationDAO();
            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet, userDao, specializationDAO));
            }
        } catch (SQLException e) {
            System.out.println("Get featured doctors error: " + e.getMessage());
        }
        return doctors;
    }

    public List<Doctor> getDoctorsBySpecialization(int specializationId) {
        List<Doctor> doctors = new ArrayList<>();
        String query = "SELECT * FROM Doctors WHERE specialization_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, specializationId);
            ResultSet resultSet = statement.executeQuery();
            UserDAO userDao = new UserDAO();
            SpecializationDAO specializationDAO = new SpecializationDAO();
            while (resultSet.next()) {
                doctors.add(mapDoctor(resultSet, userDao, specializationDAO));
            }
        } catch (SQLException e) {
            System.out.println("Get doctors by specialization error: " + e.getMessage());
        }
        return doctors;
    }
    
    public List<Doctor> getDoctorsByRoomAndSpecialization(int roomId, int specializationId) {
        List<Doctor> doctors = new ArrayList<>();
        String query = 
            "SELECT d.*, u.full_name, s.specialization_name " +
            "FROM Doctors d " +
            "JOIN Users u ON d.user_id = u.id " +
            "JOIN Specializations s ON d.specialization_id = s.id " +
            "WHERE d.specialization_id = ? AND d.status = 'Active'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, specializationId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(resultSet.getInt("id"));
                doctor.setUserId(resultSet.getInt("user_id"));
                doctor.setLicenseNumber(resultSet.getString("license_number"));
                doctor.setSpecializationId(resultSet.getInt("specialization_id"));
                doctor.setStatus(resultSet.getString("status"));
                User user = new User();
                user.setFullName(resultSet.getString("full_name"));
                doctor.setUser(user);
                Specialization spec = new Specialization();
                spec.setSpecializationName(resultSet.getString("specialization_name"));
                doctor.setSpecialization(spec);
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            System.out.println("Get doctors error: " + e.getMessage());
        }
        return doctors;
    }

    private Doctor mapDoctor(ResultSet resultSet, UserDAO userDao, SpecializationDAO specializationDAO) throws SQLException {
        User user = null;
        Doctor doctor = new Doctor();
        if (userDao != null) {
            user = userDao.getUserById(resultSet.getInt("user_id"));
            doctor.setUser(user);
        }
        doctor.setUser(user);

        Specialization specialization = null;
        if (specializationDAO != null) {
            specialization = specializationDAO.getSpecializationById(resultSet.getInt("specialization_id"));
        }

        doctor.setSpecialization(specialization);
        doctor.setId(resultSet.getInt("id"));
        doctor.setUserId(resultSet.getInt("user_id"));
        doctor.setLicenseNumber(resultSet.getString("license_number"));
        doctor.setSpecializationId(resultSet.getObject("specialization_id", Integer.class));
        doctor.setYearsOfExperience(resultSet.getObject("years_of_experience", Integer.class));
        doctor.setCertification(resultSet.getString("certification"));
        doctor.setBio(resultSet.getString("bio"));
        doctor.setStatus(resultSet.getString("status"));
        doctor.setVerified(resultSet.getBoolean("is_verified"));
        doctor.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        doctor.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        doctor.setCreatedBy(resultSet.getObject("created_by", Integer.class));
        doctor.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
        return doctor;
    }
}
