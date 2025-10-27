package Dao;

import DBConnection.DBConnection;
import Model.TimeSlot;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAO {
    private Connection conn;

    public TimeSlotDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<TimeSlot> getAvailableSlotsByRoomDoctorAndDate(int roomId, int doctorId, LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        String query = "SELECT * FROM TimeSlots WHERE room_id = ? AND date = ? AND status = 'Available' ORDER BY start_time";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, roomId);
            statement.setObject(2, date);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                TimeSlot slot = new TimeSlot();
                slot.setId(resultSet.getInt("id"));
                slot.setRoomId(resultSet.getInt("room_id"));
                slot.setDate(resultSet.getObject("date", LocalDate.class));
                slot.setStartTime(resultSet.getObject("start_time", LocalTime.class));
                slot.setEndTime(resultSet.getObject("end_time", LocalTime.class));
                slot.setStatus(resultSet.getString("status"));
                slots.add(slot);
            }
        } catch (SQLException e) {
            System.out.println("Get slots error: " + e.getMessage());
        }
        return slots;
    }

    public boolean updateSlotStatus(int slotId, String status) {
        String query = "UPDATE TimeSlots SET status = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, slotId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Update slot status error: " + e.getMessage());
            return false;
        }
    }
}