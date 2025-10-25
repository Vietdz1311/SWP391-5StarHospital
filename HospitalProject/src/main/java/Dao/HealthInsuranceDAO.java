package Dao;

import DBConnection.DBConnection;
import Model.HealthInsurance;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HealthInsuranceDAO {
    private Connection conn;

    public HealthInsuranceDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public List<HealthInsurance> getHealthInsurancesByUserId(int userId) {
        List<HealthInsurance> insurances = new ArrayList<>();
        String query = "SELECT * FROM HealthInsurances WHERE user_id = ? AND status = 'Active'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                HealthInsurance insurance = new HealthInsurance();
                insurance.setId(resultSet.getInt("id"));
                insurance.setInsuranceNumber(resultSet.getString("insurance_number"));
                insurance.setProvider(resultSet.getString("provider"));
                insurance.setStatus(resultSet.getString("status"));
                insurances.add(insurance);
            }
        } catch (SQLException e) {
            System.out.println("Get health insurances error: " + e.getMessage());
        }
        return insurances;
    }
}