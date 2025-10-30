package Dao;

import DBConnection.DBConnection;
import Model.Area;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {
    private Connection conn;

    public AreaDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<Area> getAllAreas() {
        List<Area> areas = new ArrayList<>();
        String query = "SELECT * FROM Areas WHERE status = 'Active' ORDER BY area_name";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Area area = new Area();
                area.setId(resultSet.getInt("id"));
                area.setAreaName(resultSet.getString("area_name"));
                area.setStatus(resultSet.getString("status"));
                areas.add(area);
            }
        } catch (SQLException e) {
            System.out.println("Get areas error: " + e.getMessage());
        }
        return areas;
    }
}