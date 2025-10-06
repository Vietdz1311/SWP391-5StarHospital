package DAO;

import DBConnection.DBConnection;
import Model.OTP;
import Model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private Connection conn;

    public UserDAO() {
        try {
            conn = DBConnection.connect();
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public User login(String email, String password) {
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("User login error: " + e.getMessage());
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM Users WHERE email = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Get user by email error: " + e.getMessage());
        }
        return null;
    }

    public User getUserById(int id) {
        String query = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Get user by id error: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Get all users error: " + e.getMessage());
        }
        return users;
    }

    public int addUser(User user) {
        String query = "INSERT INTO Users (role_id, full_name, id_card_number, phone_number, birth_date, gender, " +
                      "original_address, country, ethnicity, occupation, province_city, ward_commune, detailed_address, " +
                      "username, profile_picture, email, password, status, created_at, updated_at, created_by, updated_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, user.getRoleId());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getIdCardNumber());
            statement.setString(4, user.getPhoneNumber());
            statement.setObject(5, user.getBirthDate());
            statement.setString(6, user.getGender());
            statement.setString(7, user.getOriginalAddress());
            statement.setString(8, user.getCountry());
            statement.setString(9, user.getEthnicity());
            statement.setString(10, user.getOccupation());
            statement.setString(11, user.getProvinceCity());
            statement.setString(12, user.getWardCommune());
            statement.setString(13, user.getDetailedAddress());
            statement.setString(14, user.getUsername());
            statement.setString(15, user.getProfilePicture());
            statement.setString(16, user.getEmail());
            statement.setString(17, user.getPassword());
            statement.setString(18, user.getStatus());
            statement.setObject(19, user.getCreatedAt());
            statement.setObject(20, user.getUpdatedAt());
            statement.setObject(21, user.getCreatedBy());
            statement.setObject(22, user.getUpdatedBy());
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Add user error: " + e.getMessage());
        }
        return 0;
    }

    public int updatePassword(String password, int id) {
        String query = "UPDATE Users SET password = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, password);
            statement.setObject(2, LocalDateTime.now());
            statement.setInt(3, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update password error: " + e.getMessage());
        }
        return 0;
    }

    public boolean isEmailExists(String email, int excludeId) {
        String query = "SELECT * FROM Users WHERE email = ? AND id != ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setInt(2, excludeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Check email exists error: " + e.getMessage());
        }
        return false;
    }

    public boolean isPhoneExists(String phone, int excludeId) {
        String query = "SELECT * FROM Users WHERE phone_number = ? AND id != ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, phone);
            statement.setInt(2, excludeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Check phone exists error: " + e.getMessage());
        }
        return false;
    }
    
    public boolean isIdCardNumberExists(String idCardNumber, int excludeId) {
        String query = "SELECT * FROM Users WHERE id_card_number = ? AND id != ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, idCardNumber);
            statement.setInt(2, excludeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Check ID card number exists error: " + e.getMessage());
        }
        return false;
    }

    public boolean isUsernameExists(String username, int excludeId) {
        String query = "SELECT * FROM Users WHERE username = ? AND id != ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setInt(2, excludeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Check username exists error: " + e.getMessage());
        }
        return false;
    }

    public int addOTP(OTP otp) {
        String query = "INSERT INTO OTP (user_id, otp_code, purpose, expiry_time, status, created_at, updated_at, created_by, updated_by) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, otp.getUserId());
            statement.setString(2, otp.getOtpCode());
            statement.setString(3, otp.getPurpose());
            statement.setObject(4, otp.getExpiryTime());
            statement.setString(5, otp.getStatus());
            statement.setObject(6, otp.getCreatedAt());
            statement.setObject(7, otp.getUpdatedAt());
            statement.setObject(8, otp.getCreatedBy());
            statement.setObject(9, otp.getUpdatedBy());
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add OTP error: " + e.getMessage());
        }
        return 0;
    }
    
     public int activateUser(int id) {
        String query = "UPDATE Users SET status = 'active', updated_at = ? WHERE id = ? AND status = 'pending'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setObject(1, LocalDateTime.now());
            statement.setInt(2, id);
            return statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Activate user error: " + e.getMessage());
        }
        return 0;
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

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setRoleId(resultSet.getInt("role_id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setIdCardNumber(resultSet.getString("id_card_number"));
        user.setPhoneNumber(resultSet.getString("phone_number"));
        user.setBirthDate(resultSet.getObject("birth_date", LocalDate.class));
        user.setGender(resultSet.getString("gender"));
        user.setOriginalAddress(resultSet.getString("original_address"));
        user.setCountry(resultSet.getString("country"));
        user.setEthnicity(resultSet.getString("ethnicity"));
        user.setOccupation(resultSet.getString("occupation"));
        user.setProvinceCity(resultSet.getString("province_city"));
        user.setWardCommune(resultSet.getString("ward_commune"));
        user.setDetailedAddress(resultSet.getString("detailed_address"));
        user.setUsername(resultSet.getString("username"));
        user.setProfilePicture(resultSet.getString("profile_picture"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password"));
        user.setStatus(resultSet.getString("status"));
        user.setCreatedAt(resultSet.getObject("created_at", LocalDateTime.class));
        user.setUpdatedAt(resultSet.getObject("updated_at", LocalDateTime.class));
        user.setCreatedBy(resultSet.getObject("created_by", Integer.class));
        user.setUpdatedBy(resultSet.getObject("updated_by", Integer.class));
        return user;
    }
}