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

public class SpecializationDAO {

    private Connection conn;

    public SpecializationDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<Specialization> getAllSpecializations(String search, String filterStatus, String sortBy, int page, int size) {
        List<Specialization> specializations = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Specializations WHERE 1=1");
        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND specialization_name LIKE ?");
        }
        if (filterStatus != null && !filterStatus.trim().isEmpty()) {
            query.append(" AND status = ?");
        }
        // Always include ORDER BY to prevent OFFSET error
        query.append(" ORDER BY ");
        query.append((sortBy != null && !sortBy.trim().isEmpty()) ? sortBy : "id ASC");
        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                statement.setString(paramIndex++, "%" + search.trim() + "%");
            }
            if (filterStatus != null && !filterStatus.trim().isEmpty()) {
                statement.setString(paramIndex++, filterStatus.trim());
            }
            statement.setInt(paramIndex++, (page - 1) * size);
            statement.setInt(paramIndex, size);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                specializations.add(mapSpecialization(resultSet, null));
            }
        } catch (SQLException e) {
            System.out.println("Get all specializations error: " + e.getMessage());
        }
        return specializations;
    }

    public int getTotalSpecializations(String search, String filterStatus) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM Specializations WHERE 1=1");
        if (search != null && !search.trim().isEmpty()) {
            query.append(" AND specialization_name LIKE ?");
        }
        if (filterStatus != null && !filterStatus.trim().isEmpty()) {
            query.append(" AND status = ?");
        }

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (search != null && !search.trim().isEmpty()) {
                statement.setString(paramIndex++, "%" + search.trim() + "%");
            }
            if (filterStatus != null && !filterStatus.trim().isEmpty()) {
                statement.setString(paramIndex, filterStatus.trim());
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Get total specializations error: " + e.getMessage());
        }
        return 0;
    }

    public Specialization getSpecializationById(int id) {
        String query = "SELECT * FROM Specializations WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            UserDAO userDao = new UserDAO();
            if (resultSet.next()) {
                return mapSpecialization(resultSet,userDao);
            }
        } catch (SQLException e) {
            System.out.println("Get specialization by id error: " + e.getMessage());
        }
        return null;
    }

    public List<Specialization> getFeaturedSpecializations(int limit) {
        List<Specialization> specializations = new ArrayList<>();
        String query = "SELECT TOP (?) * FROM Specializations WHERE status = 'Active' ORDER BY id DESC";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, limit);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                specializations.add(mapSpecialization(resultSet, null));
            }
        } catch (SQLException e) {
            System.out.println("Get featured specializations error: " + e.getMessage());
        }
        return specializations;
    }
    
    public List<Specialization> getAllSpecializations() {
        List<Specialization> specializations = new ArrayList<>();
        String query = "SELECT * FROM Specializations WHERE status = 'Active' ORDER BY specialization_name";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Specialization spec = new Specialization();
                spec.setId(resultSet.getInt("id"));
                spec.setSpecializationName(resultSet.getString("specialization_name"));
                spec.setStatus(resultSet.getString("status"));
                specializations.add(spec);
            }
        } catch (SQLException e) {
            System.out.println("Get specializations error: " + e.getMessage());
        }
        return specializations;
    }

    private Specialization mapSpecialization(ResultSet resultSet, UserDAO userDao) throws SQLException {
        Specialization spec = new Specialization();
        User doctor = null;
        if(userDao != null) {
            doctor = userDao.getUserById(resultSet.getInt("head_doctor_id"));
        }
        spec.setHeadDoctor(doctor);
        spec.setId(resultSet.getInt("id"));
        spec.setSpecializationName(resultSet.getString("specialization_name"));
        spec.setDescription(resultSet.getString("description"));
        spec.setHeadDoctorId(resultSet.getInt("head_doctor_id"));
        spec.setStatus(resultSet.getString("status"));
        spec.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        spec.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        spec.setCreatedBy(resultSet.getObject("created_by", Integer.class));
        spec.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
        return spec;
    }
}