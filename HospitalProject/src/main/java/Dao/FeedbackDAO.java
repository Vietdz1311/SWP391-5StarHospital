package Dao;

import DBConnection.DBConnection;
import Model.Feedback;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    private Connection conn;

    public FeedbackDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public Feedback getFeedbackById(int id) {
        String query = "SELECT * FROM Feedbacks WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapFeedback(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Get feedback by id error: " + e.getMessage());
        }
        return null;
    }

    public List<Feedback> getAllFeedbacks(Integer userId, Integer appointmentId, String status, int page, int size) {
        List<Feedback> feedbacks = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Feedbacks WHERE 1=1");
        
        if (userId != null) {
            query.append(" AND user_id = ?");
        }
        if (appointmentId != null) {
            query.append(" AND appointment_id = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            query.append(" AND status = ?");
        }
        
        query.append(" ORDER BY created_at DESC");
        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (userId != null) {
                statement.setInt(paramIndex++, userId);
            }
            if (appointmentId != null) {
                statement.setInt(paramIndex++, appointmentId);
            }
            if (status != null && !status.trim().isEmpty()) {
                statement.setString(paramIndex++, status);
            }
            statement.setInt(paramIndex++, (page - 1) * size);
            statement.setInt(paramIndex, size);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                feedbacks.add(mapFeedback(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Get all feedbacks error: " + e.getMessage());
        }
        return feedbacks;
    }

    public int getTotalFeedbacks(Integer userId, Integer appointmentId, String status) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM Feedbacks WHERE 1=1");
        
        if (userId != null) {
            query.append(" AND user_id = ?");
        }
        if (appointmentId != null) {
            query.append(" AND appointment_id = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            query.append(" AND status = ?");
        }

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (userId != null) {
                statement.setInt(paramIndex++, userId);
            }
            if (appointmentId != null) {
                statement.setInt(paramIndex++, appointmentId);
            }
            if (status != null && !status.trim().isEmpty()) {
                statement.setString(paramIndex, status);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Get total feedbacks error: " + e.getMessage());
        }
        return 0;
    }

    public int addFeedback(Feedback feedback) {
        String query = "INSERT INTO Feedbacks (user_id, appointment_id, rating, comment, status, " +
                      "created_at, updated_at, created_by, updated_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            statement.setInt(i++, feedback.getUserId());
            statement.setInt(i++, feedback.getAppointmentId());
            statement.setInt(i++, feedback.getRating());
            statement.setString(i++, feedback.getComment());
            statement.setString(i++, feedback.getStatus());
            statement.setObject(i++, feedback.getCreatedAt());
            statement.setObject(i++, feedback.getUpdatedAt());
            statement.setObject(i++, feedback.getCreatedBy());
            statement.setObject(i++, feedback.getUpdatedBy());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Add feedback error: " + e.getMessage());
        }
        return 0;
    }

    public int updateFeedback(Feedback feedback) {
        String query = "UPDATE Feedbacks SET rating = ?, comment = ?, status = ?, " +
                      "updated_at = ?, updated_by = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            int i = 1;
            statement.setInt(i++, feedback.getRating());
            statement.setString(i++, feedback.getComment());
            statement.setString(i++, feedback.getStatus());
            statement.setObject(i++, LocalDateTime.now());
            statement.setObject(i++, feedback.getUpdatedBy());
            statement.setInt(i++, feedback.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update feedback error: " + e.getMessage());
        }
        return 0;
    }

    public int deleteFeedback(int id) {
        String query = "DELETE FROM Feedbacks WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete feedback error: " + e.getMessage());
        }
        return 0;
    }

    private Feedback mapFeedback(ResultSet resultSet) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(resultSet.getInt("id"));
        feedback.setUserId(resultSet.getInt("user_id"));
        feedback.setAppointmentId(resultSet.getInt("appointment_id"));
        feedback.setRating(resultSet.getInt("rating"));
        feedback.setComment(resultSet.getString("comment"));
        feedback.setStatus(resultSet.getString("status"));
        feedback.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        feedback.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        feedback.setCreatedBy(resultSet.getObject("created_by", Integer.class));
        feedback.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
        
        // Load user and appointment if needed
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(feedback.getUserId());
            feedback.setUser(user);
        } catch (Exception e) {
            System.out.println("Error loading user for feedback: " + e.getMessage());
        }
        
        return feedback;
    }
}

