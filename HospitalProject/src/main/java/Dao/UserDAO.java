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
        try (PreparedStatement statement = conn.prepareStatement(query); ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Get all users error: " + e.getMessage());
        }
        return users;
    }

    public int addUser(User user) {
        String query = "INSERT INTO Users ("
                + "role_id, full_name, id_card_number, phone_number, birth_date, gender, "
                + "address, username, profile_picture, email, password, status, "
                + "created_at, updated_at, created_by, updated_by"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, user.getRoleId());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getIdCardNumber());
            statement.setString(4, user.getPhoneNumber());
            statement.setObject(5, user.getBirthDate());
            statement.setString(6, user.getGender());
            statement.setString(7, user.getAddress());            // chỉ 1 cột address
            statement.setString(8, user.getUsername());
            statement.setString(9, user.getProfilePicture());
            statement.setString(10, user.getEmail());
            statement.setString(11, user.getPassword());          // đã MD5 ở Controller
            statement.setString(12, user.getStatus());            // pending/active...
            statement.setObject(13, user.getCreatedAt());
            statement.setObject(14, user.getUpdatedAt());
            statement.setObject(15, user.getCreatedBy());
            statement.setObject(16, user.getUpdatedBy());

            int rows = statement.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = statement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Add user error: " + e.getMessage());
        }
        return 0;
    }

    public boolean updateProfile(User u) throws SQLException {
        String sql = "UPDATE Users SET full_name=?, phone_number=?, birth_date=?, gender=?, address=?, email=?, profile_picture=?, updated_at=SYSDATETIME() WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getPhoneNumber());
            if (u.getBirthDate() != null) {
                ps.setDate(3, java.sql.Date.valueOf(u.getBirthDate()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, u.getGender());
            ps.setString(5, u.getAddress());
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getProfilePicture());
            ps.setInt(8, u.getId());
            return ps.executeUpdate() > 0;
        }
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
        String query = "INSERT INTO OTP (user_id, otp_code, purpose, expiry_time, status, created_at, updated_at, created_by, updated_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

//admin
    // 1) Đếm tổng theo bộ lọc (q/roleName/status)
    public int countUsers(String q, String roleName, String status) {
        StringBuilder sb = new StringBuilder(
                "SELECT COUNT(*) FROM Users u JOIN Roles r ON u.role_id = r.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sb.append(" AND (u.full_name LIKE ? OR u.username LIKE ? OR u.email LIKE ? OR u.phone_number LIKE ?)");
            String like = "%" + q.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (roleName != null && !roleName.isBlank()) {
            sb.append(" AND r.role_name = ?");
            params.add(roleName);
        }
        if (status != null && !status.isBlank()) {
            sb.append(" AND u.status = ?");
            params.add(status);
        }

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.out.println("countUsers error: " + e.getMessage());
            return 0;
        }
    }

// 2) Tìm kiếm có phân trang
    public List<User> searchUsers(String q, String roleName, String status, int page, int size) {
        StringBuilder sb = new StringBuilder(
                "SELECT u.* FROM Users u JOIN Roles r ON u.role_id = r.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sb.append(" AND (u.full_name LIKE ? OR u.username LIKE ? OR u.email LIKE ? OR u.phone_number LIKE ?)");
            String like = "%" + q.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (roleName != null && !roleName.isBlank()) {
            sb.append(" AND r.role_name = ?");
            params.add(roleName);
        }
        if (status != null && !status.isBlank()) {
            sb.append(" AND u.status = ?");
            params.add(status);
        }

        sb.append(" ORDER BY u.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        int offset = Math.max(0, (page - 1) * size);
        params.add(offset);
        params.add(size);

        List<User> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("searchUsers error: " + e.getMessage());
        }
        return list;
    }

// 3) Đổi trạng thái (và ghi updated_by)
    public boolean changeStatus(int id, String newStatus, Integer adminId) {
        String sql = "UPDATE Users SET status=?, updated_at=SYSDATETIME(), updated_by=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            if (adminId != null) {
                ps.setInt(2, adminId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("changeStatus error: " + e.getMessage());
            return false;
        }
    }

// 4) Reset mật khẩu (overload có updated_by)
    public boolean resetPassword(int id, String passwordHash, Integer adminId) {
        String sql = "UPDATE Users SET password=?, updated_at=SYSDATETIME(), updated_by=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            if (adminId != null) {
                ps.setInt(2, adminId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("resetPassword error: " + e.getMessage());
            return false;
        }
    }

// 5) Xóa mềm (đặt inactive)
    public boolean softDelete(int id, Integer adminId) {
        return changeStatus(id, "inactive", adminId);
    }

// (Tuỳ chọn) Lấy user kèm role_name nếu bạn cần hiển thị tên vai trò
    public User getUserWithRoleById(int id) {
        String sql = "SELECT u.* FROM Users u JOIN Roles r ON u.role_id=r.id WHERE u.id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("getUserWithRoleById error: " + e.getMessage());
        }
        return null;
    }

    // 6) Cập nhật trường "tài khoản" cho Admin (username, id_card_number, role_id)
    public boolean updateAccountFields(int id, String username, String idCardNumber, int roleId) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE Users SET updated_at = SYSDATETIME()");
        // build động các trường cần cập nhật
        if (username != null) {
            sb.append(", username = ?");
        }
        if (idCardNumber != null) {
            sb.append(", id_card_number = ?");
        }
        if (roleId > 0) {
            sb.append(", role_id = ?");
        }
        sb.append(" WHERE id = ?");

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (username != null) {
                ps.setString(idx++, username);
            }
            if (idCardNumber != null) {
                ps.setString(idx++, idCardNumber);
            }
            if (roleId > 0) {
                ps.setInt(idx++, roleId);
            }
            ps.setInt(idx++, id);
            return ps.executeUpdate() > 0;
        }
    }

// 7) (Tuỳ chọn) Cập nhật cả status luôn nếu bạn muốn gộp
    public boolean updateAccountFieldsWithStatus(int id, String username, String idCardNumber, int roleId, String status) throws SQLException {
        StringBuilder sb = new StringBuilder("UPDATE Users SET updated_at = SYSDATETIME()");
        if (username != null) {
            sb.append(", username = ?");
        }
        if (idCardNumber != null) {
            sb.append(", id_card_number = ?");
        }
        if (roleId > 0) {
            sb.append(", role_id = ?");
        }
        if (status != null) {
            sb.append(", status = ?");
        }
        sb.append(" WHERE id = ?");

        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (username != null) {
                ps.setString(idx++, username);
            }
            if (idCardNumber != null) {
                ps.setString(idx++, idCardNumber);
            }
            if (roleId > 0) {
                ps.setInt(idx++, roleId);
            }
            if (status != null) {
                ps.setString(idx++, status);
            }
            ps.setInt(idx++, id);
            return ps.executeUpdate() > 0;
        }
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
        user.setAddress(resultSet.getString("address"));
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
