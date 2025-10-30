package Controllers;

import DAO.UserDAO;
import Model.User;
import Utils.MD5Hashing;   // ✅ thêm dòng này
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "UserManagerServlet",
        urlPatterns = {"/UserManagerServlet", "/admin/users"})
public class UserManagerServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private static final String VIEW_BASE = "/web-page/admin/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setupUtf8(request, response);
        String action = param(request, "action", "list");

        try {
            switch (action) {
                case "create":
                    forward(request, response, VIEW_BASE + "userForm.jsp");
                    break;
                case "edit":
                    handleEdit(request, response);
                    break;
                case "list":
                default:
                    handleList(request, response);
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            forward(request, response, VIEW_BASE + "userList.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        setupUtf8(request, response);
        String action = param(request, "action", "save");

        try {
            switch (action) {
                case "save":
                    handleSave(request, response);
                    break;
                case "delete":
                    handleDelete(request, response);
                    break;
                case "resetPassword":
                    handleResetPassword(request, response);
                    break;
                case "changeStatus":
                    handleChangeStatus(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/admin/users?action=list");
            }
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            String back = "save".equals(action) ? (VIEW_BASE + "userForm.jsp") : (VIEW_BASE + "userList.jsp");
            forward(request, response, back);
        }
    }

    // =================== DEBUG (giữ nguyên) ===================
    private static void debugListJsp(HttpServletRequest req) {
        var sc = req.getServletContext();
        var paths = sc.getResourcePaths("/web-page/admin/");
        System.out.println("[DEBUG] JSP under /web-page/admin/: " + paths);
        System.out.println("[DEBUG] realPath userList.jsp = " + sc.getRealPath("/web-page/admin/userList.jsp"));
    }

    /* ========================== LIST/EDIT FORMS ========================== */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String q = trimToNull(request.getParameter("q"));
        String roleName = trimToNull(request.getParameter("roleName"));
        String status = trimToNull(request.getParameter("status"));

        int page = parseInt(param(request, "page", "1"), 1);
        int size = parseInt(param(request, "size", "10"), 10);

        int total = userDAO.countUsers(q, roleName, status);
        List<User> users = userDAO.searchUsers(q, roleName, status, page, size);

        request.setAttribute("q", q);
        request.setAttribute("roleName", roleName);
        request.setAttribute("status", status);
        request.setAttribute("page", page);
        request.setAttribute("size", size);
        request.setAttribute("total", total);
        request.setAttribute("users", users);

        debugListJsp(request);
        forward(request, response, VIEW_BASE + "userList.jsp");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = parseInt(request.getParameter("id"), 0);
        if (id <= 0) {
            throw new IllegalArgumentException("Thiếu hoặc sai tham số id.");
        }

        User u = userDAO.getUserById(id);
        if (u == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng id=" + id);
        }

        request.setAttribute("user", u);
        forward(request, response, VIEW_BASE + "userForm.jsp");
    }

    /* ========================== CREATE/UPDATE ========================== */
    private void handleSave(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int id = parseInt(request.getParameter("id"), 0);

        String fullName = required(request.getParameter("fullName"), "Họ tên không được để trống.");
        String phone = trimToNull(request.getParameter("phoneNumber"));
        String birthDateS = trimToNull(request.getParameter("birthDate"));
        String gender = trimToNull(request.getParameter("gender"));
        String address = trimToNull(request.getParameter("address"));
        String email = required(request.getParameter("email"), "Email không được để trống.");
        String profilePic = trimToNull(request.getParameter("profilePicture"));

        String username = required(request.getParameter("username"), "Username không được để trống.");
        String idCard = trimToNull(request.getParameter("idCardNumber"));
        int roleId = parseInt(request.getParameter("roleId"), 0);
        String status = trimToNull(request.getParameter("status"));
        String password = trimToNull(request.getParameter("password"));

        if (gender != null && !(gender.equals("Male") || gender.equals("Female"))) {
            throw new IllegalArgumentException("Giới tính chỉ nhận Male/Female.");
        }
        LocalDate birthDate = (birthDateS != null && !birthDateS.isEmpty()) ? LocalDate.parse(birthDateS) : null;

        // Kiểm tra trùng lặp
        if (userDAO.isEmailExists(email, id)) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }
        if (phone != null && userDAO.isPhoneExists(phone, id)) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại.");
        }
        if (idCard != null && userDAO.isIdCardNumberExists(idCard, id)) {
            throw new IllegalArgumentException("CMND/CCCD đã tồn tại.");
        }
        if (userDAO.isUsernameExists(username, id)) {
            throw new IllegalArgumentException("Username đã tồn tại.");
        }

        // ========== TẠO MỚI ==========
        if (id <= 0) {
            if (password == null || password.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập mật khẩu khi tạo mới.");
            }

            // ✅ mã hóa giống AuthController
            MD5Hashing md5 = new MD5Hashing();

            User u = new User();
            u.setRoleId(roleId);
            u.setFullName(fullName);
            u.setIdCardNumber(idCard);
            u.setPhoneNumber(phone);
            u.setBirthDate(birthDate);
            u.setGender(gender);
            u.setAddress(address);
            u.setUsername(username);
            u.setProfilePicture(profilePic);
            u.setEmail(email);
            u.setPassword(md5.hashPassword(password)); // ✅ mã hóa
            u.setStatus(status == null ? "pending" : status);
            u.setCreatedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            u.setCreatedBy(null);
            u.setUpdatedBy(null);

            int newId = userDAO.addUser(u);
            if (newId <= 0) {
                throw new RuntimeException("Thêm người dùng thất bại.");
            }

            response.sendRedirect(request.getContextPath() + "/admin/users?action=list&created=1");
            return;
        }

        // ========== CẬP NHẬT ==========
        else {
            User u = new User();
            u.setId(id);
            u.setFullName(fullName);
            u.setPhoneNumber(phone);
            u.setBirthDate(birthDate);
            u.setGender(gender);
            u.setAddress(address);
            u.setEmail(email);
            u.setProfilePicture(profilePic);

            // cập nhật thông tin hồ sơ
            if (!userDAO.updateProfile(u)) {
                throw new RuntimeException("Cập nhật hồ sơ thất bại.");
            }

            // cập nhật các trường tài khoản (username, idCard, role)
            if (!userDAOUpdateAccountFields(u, username, idCard, roleId)) {
                // không ném lỗi cứng để tránh chặn toàn bộ cập nhật
            }

            // nếu admin nhập mật khẩu mới → mã hóa rồi lưu
            if (password != null && !password.isEmpty()) {
                MD5Hashing md5 = new MD5Hashing();
                int rows = userDAO.updatePassword(md5.hashPassword(password), id);  // ✅ mã hóa
                if (rows <= 0) {
                    throw new RuntimeException("Cập nhật mật khẩu thất bại.");
                }
            }

            // cập nhật trạng thái nếu có
            if (status != null && !status.isEmpty()) {
                boolean ok = userDAO.changeStatus(id, status, null);
                if (!ok) {
                    throw new RuntimeException("Đổi trạng thái thất bại.");
                }
            }

            response.sendRedirect(request.getContextPath() + "/admin/users?action=list&updated=1");
        }
    }

    /* ========================== DELETE / RESET / STATUS ========================== */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = parseInt(request.getParameter("id"), 0);
        if (id <= 0) {
            throw new IllegalArgumentException("Thiếu hoặc sai tham số id.");
        }

        boolean ok = userDAO.softDelete(id, null);
        if (!ok) {
            throw new RuntimeException("Xóa người dùng thất bại.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/users?action=list&deleted=1");
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = parseInt(request.getParameter("id"), 0);
        String newPassword = trimToNull(request.getParameter("newPassword"));
        if (id <= 0 || newPassword == null) {
            throw new IllegalArgumentException("Thiếu tham số id hoặc mật khẩu mới.");
        }

        // ✅ mã hóa trước khi update
        MD5Hashing md5 = new MD5Hashing();
        int rows = userDAO.updatePassword(md5.hashPassword(newPassword), id);
        if (rows <= 0) {
            throw new RuntimeException("Đổi mật khẩu thất bại.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/users?action=list&pwd_reset=1");
    }

    private void handleChangeStatus(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int id = parseInt(request.getParameter("id"), 0);
        String newStatus = trimToNull(request.getParameter("newStatus"));
        if (id <= 0 || newStatus == null) {
            throw new IllegalArgumentException("Thiếu tham số id hoặc trạng thái mới.");
        }

        boolean ok = userDAO.changeStatus(id, newStatus, null);
        if (!ok) {
            throw new RuntimeException("Đổi trạng thái thất bại.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/users?action=list&status_changed=1");
    }

    /* ========================== DAO PATCH WRAPPER ========================== */
    private boolean userDAOUpdateAccountFields(User u, String username, String idCard, int roleId) throws SQLException {
        User old = userDAO.getUserById(u.getId());
        boolean need = (old != null) && ((username != null && !username.equals(old.getUsername()))
                || (idCard != null && !idCard.equals(old.getIdCardNumber()))
                || (roleId > 0 && roleId != old.getRoleId()));
        if (!need) {
            return true;
        }
        return userDAO.updateAccountFields(u.getId(), username, idCard, roleId);
    }

    /* ========================== HELPERS ========================== */
    private static void setupUtf8(HttpServletRequest req, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        } catch (Exception ignore) {
        }
    }

    // FORWARD an toàn: kiểm tra WAR có JSP rồi mới forward, in ra real path để debug
    private static void forward(HttpServletRequest request, HttpServletResponse response, String jsp)
            throws ServletException, IOException {
        var sc = request.getServletContext();
        if (!jsp.startsWith("/")) {
            jsp = "/" + jsp; // bảo đảm absolute
        }
        if (sc.getResource(jsp) == null) {
            throw new ServletException("JSP không tồn tại trong WAR: " + jsp
                    + " | realPath=" + sc.getRealPath(jsp));
        }
        sc.getRequestDispatcher(jsp).forward(request, response);
    }

    private static String param(HttpServletRequest req, String name, String def) {
        String v = req.getParameter(name);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    private static String trimToNull(String v) {
        if (v == null) {
            return null;
        }
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private static String required(String v, String msg) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException(msg);
        }
        return v.trim();
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception ignore) {
            return def;
        }
    }
}
