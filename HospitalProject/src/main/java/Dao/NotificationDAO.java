package Dao;

import DBConnection.DBConnection;
import Model.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private Connection conn;

    public NotificationDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public Notification getNotificationById(int id) {
        String query = "SELECT * FROM Notifications WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapNotification(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Get notification by id error: " + e.getMessage());
        }
        return null;
    }

    public List<Notification> getAllNotifications(Integer userId, String type, String status, int page, int size) {
        List<Notification> notifications = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Notifications WHERE 1=1");
        
        if (userId != null) {
            query.append(" AND user_id = ?");
        }
        if (type != null && !type.trim().isEmpty()) {
            query.append(" AND type = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            query.append(" AND status = ?");
        }
        
        query.append(" ORDER BY sent_at DESC, created_at DESC");
        query.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (userId != null) {
                statement.setInt(paramIndex++, userId);
            }
            if (type != null && !type.trim().isEmpty()) {
                statement.setString(paramIndex++, type);
            }
            if (status != null && !status.trim().isEmpty()) {
                statement.setString(paramIndex++, status);
            }
            statement.setInt(paramIndex++, (page - 1) * size);
            statement.setInt(paramIndex, size);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                notifications.add(mapNotification(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Get all notifications error: " + e.getMessage());
        }
        return notifications;
    }

    public int getTotalNotifications(Integer userId, String type, String status) {
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM Notifications WHERE 1=1");
        
        if (userId != null) {
            query.append(" AND user_id = ?");
        }
        if (type != null && !type.trim().isEmpty()) {
            query.append(" AND type = ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            query.append(" AND status = ?");
        }

        try (PreparedStatement statement = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (userId != null) {
                statement.setInt(paramIndex++, userId);
            }
            if (type != null && !type.trim().isEmpty()) {
                statement.setString(paramIndex++, type);
            }
            if (status != null && !status.trim().isEmpty()) {
                statement.setString(paramIndex, status);
            }

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Get total notifications error: " + e.getMessage());
        }
        return 0;
    }

    public int addNotification(Notification notification) {
        String query = "INSERT INTO Notifications (user_id, type, content, sent_at, status, " +
                      "created_at, updated_at, created_by, updated_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            statement.setInt(i++, notification.getUserId());
            statement.setString(i++, notification.getType());
            statement.setString(i++, notification.getContent());
            statement.setObject(i++, notification.getSentAt());
            statement.setString(i++, notification.getStatus());
            statement.setObject(i++, notification.getCreatedAt());
            statement.setObject(i++, notification.getUpdatedAt());
            statement.setObject(i++, notification.getCreatedBy());
            statement.setObject(i++, notification.getUpdatedBy());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Add notification error: " + e.getMessage());
        }
        return 0;
    }

    public int updateNotificationStatus(int id, String status, Integer updatedBy) {
        String query = "UPDATE Notifications SET status = ?, updated_at = ?, updated_by = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setObject(2, LocalDateTime.now());
            statement.setObject(3, updatedBy);
            statement.setInt(4, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update notification status error: " + e.getMessage());
        }
        return 0;
    }

    public int updateNotification(Notification notification) {
        String query = "UPDATE Notifications SET user_id = ?, type = ?, content = ?, sent_at = ?, " +
                      "status = ?, updated_at = ?, updated_by = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            int i = 1;
            statement.setInt(i++, notification.getUserId());
            statement.setString(i++, notification.getType());
            statement.setString(i++, notification.getContent());
            statement.setObject(i++, notification.getSentAt());
            statement.setString(i++, notification.getStatus());
            statement.setObject(i++, LocalDateTime.now());
            statement.setObject(i++, notification.getUpdatedBy());
            statement.setInt(i++, notification.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update notification error: " + e.getMessage());
        }
        return 0;
    }

    public int deleteNotification(int id) {
        String query = "DELETE FROM Notifications WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Delete notification error: " + e.getMessage());
        }
        return 0;
    }

    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT * FROM Notifications WHERE user_id = ? AND status = 'unread' " +
                      "ORDER BY sent_at DESC, created_at DESC";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                notifications.add(mapNotification(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Get unread notifications error: " + e.getMessage());
        }
        return notifications;
    }

    public int markAsRead(int id, Integer updatedBy) {
        return updateNotificationStatus(id, "read", updatedBy);
    }

    public int markAsUnread(int id, Integer updatedBy) {
        return updateNotificationStatus(id, "unread", updatedBy);
    }

    public int markAllAsRead(int userId, Integer updatedBy) {
        String query = "UPDATE Notifications SET status = 'read', updated_at = ?, updated_by = ? " +
                      "WHERE user_id = ? AND status = 'unread'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setObject(1, LocalDateTime.now());
            statement.setObject(2, updatedBy);
            statement.setInt(3, userId);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Mark all as read error: " + e.getMessage());
        }
        return 0;
    }

    private Notification mapNotification(ResultSet resultSet) throws SQLException {
        Notification notification = new Notification();
        notification.setId(resultSet.getInt("id"));
        notification.setUserId(resultSet.getInt("user_id"));
        notification.setType(resultSet.getString("type"));
        notification.setContent(resultSet.getString("content"));
        notification.setSentAt(resultSet.getObject("sent_at", LocalDateTime.class));
        notification.setStatus(resultSet.getString("status"));
        notification.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        notification.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        notification.setCreatedBy(resultSet.getObject("created_by", Integer.class));
        notification.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
        return notification;
    }
}

