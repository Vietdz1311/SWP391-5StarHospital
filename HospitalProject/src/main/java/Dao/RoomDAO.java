package Dao;

import DBConnection.DBConnection;
import Model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private Connection conn;

    public RoomDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<Room> getRoomsBySpecializationAndArea(int specializationId, int areaId) {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM Rooms WHERE specialization_id = ? AND area_id = ? AND status = 'Available' ORDER BY room_name";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, specializationId);
            statement.setInt(2, areaId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Room room = new Room();
                room.setId(resultSet.getInt("id"));
                room.setRoomName(resultSet.getString("room_name"));
                room.setSpecializationId(resultSet.getInt("specialization_id"));
                room.setAreaId(resultSet.getInt("area_id"));
                room.setStatus(resultSet.getString("status"));
                room.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                room.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
                room.setCreatedBy(resultSet.getObject("created_by", Integer.class));
                room.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Get rooms error: " + e.getMessage());
        }
        return rooms;
    }
}