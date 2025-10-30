/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Dao;

import DBConnection.DBConnection;
import Model.OTP;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author 3DO TECH
 */
public class OtpDAO {

    private Connection conn;

    public OtpDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
    
    public OTP getValidOTP(int userId, String otpCode, String purpose) {
        String query = "SELECT * FROM OTP WHERE user_id = ? AND otp_code = ? AND purpose = ? AND status = 'unused' AND expiry_time > ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, otpCode);
            statement.setString(3, purpose);
            statement.setObject(4, LocalDateTime.now());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                OTP otp = new OTP();
                otp.setId(resultSet.getInt("id"));
                otp.setUserId(resultSet.getInt("user_id"));
                otp.setOtpCode(resultSet.getString("otp_code"));
                otp.setPurpose(resultSet.getString("purpose"));
                otp.setExpiryTime(resultSet.getObject("expiry_time", LocalDateTime.class));
                otp.setStatus(resultSet.getString("status"));
                otp.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
                otp.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
                otp.setCreatedBy(resultSet.getObject("created_by", Integer.class));
                otp.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
                return otp;
            }
        } catch (SQLException e) {
            System.out.println("Get valid OTP error: " + e.getMessage());
        }
        return null;
    }

    public int deactivateOTP(int otpId) {
        String query = "UPDATE OTP SET status = 'used', updated_at = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setObject(1, LocalDateTime.now());
            statement.setInt(2, otpId);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Deactivate OTP error: " + e.getMessage());
        }
        return 0;
    }
}
