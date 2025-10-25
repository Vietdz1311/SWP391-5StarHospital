/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import DAO.UserDAO;
import Model.User;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sang
 */
@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/profile/update"})
@MultipartConfig

public class UpdateProfileServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(UpdateProfileServlet.class.getName());
    private final UserDAO userDAO = new UserDAO();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UpdateProfileServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateProfileServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        // 1) Lấy user từ session (đồng bộ với AuthController & ViewProfileServlet)
        HttpSession session = req.getSession(false);
        User sessionUser = (session != null) ? (User) session.getAttribute("user") : null;
        if (sessionUser == null) {
            resp.sendRedirect(req.getContextPath() + "/auth");
            return;
        }
        int userId = sessionUser.getId();

        // 2) Lấy user hiện tại từ DB
        User current = userDAO.getUserById(userId);
        if (current == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }

        // 3) Đọc input
        String fullName = req.getParameter("full_name");
        String phoneNumber = req.getParameter("phone_number");
        String birthDateStr = req.getParameter("birth_date");
        String gender = req.getParameter("gender");
        String address = req.getParameter("address");
        String email = req.getParameter("email");

        // 4) Parse ngày sinh
        LocalDate birthDate = null;
        if (birthDateStr != null && !birthDateStr.isBlank()) {
            try {
                birthDate = LocalDate.parse(birthDateStr); // yyyy-MM-dd
            } catch (Exception e) {
                req.setAttribute("error", "Ngày sinh không hợp lệ (yyyy-MM-dd).");
                req.setAttribute("user", current);
                req.getRequestDispatcher("/web-page/profile.jsp").forward(req, resp);
                return;
            }
        }

        // 5) Upload ảnh (nếu có)
        String profilePicturePath = current.getProfilePicture();
        Part avatar = req.getPart("profile_picture");
        if (avatar != null && avatar.getSize() > 0) {
            String uploadRoot = req.getServletContext().getRealPath("/uploads/avatars");
            File dir = new File(uploadRoot);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String submitted = avatar.getSubmittedFileName();
            String ext = "";
            int dot = (submitted != null) ? submitted.lastIndexOf('.') : -1;
            if (dot >= 0) {
                ext = submitted.substring(dot);
            }

            String filename = "u" + userId + "_" + System.currentTimeMillis() + ext;
            File file = new File(dir, filename);

            Files.copy(avatar.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            // Lưu đường dẫn tương đối (KHÔNG kèm context path)
            profilePicturePath = "uploads/avatars/" + filename;
        }

        // 6) Gán dữ liệu mới
        current.setFullName(z(fullName));
        current.setPhoneNumber(z(phoneNumber));
        current.setBirthDate(birthDate);
        current.setGender(z(gender)); // DB check Male/Female; để null nếu "--"
        current.setAddress(z(address));
        current.setEmail(z(email));
        current.setProfilePicture(profilePicturePath);

        // 7) Lưu DB + xử lý lỗi rõ ràng
        try {
            boolean ok = userDAO.updateProfile(current);
            if (!ok) {
                req.setAttribute("error", "Không có thay đổi nào được lưu.");
                req.setAttribute("user", current);
                req.getRequestDispatcher("/web-page/profile.jsp").forward(req, resp);
                return;
            }

            // 8) Cập nhật lại session user để UI hiển thị mới
            session.setAttribute("user", current);

            // 9) Redirect về ViewProfileServlet (đúng flow MVC, có success)
            resp.sendRedirect(req.getContextPath() + "/ViewProfileServlet?success=1");
            return;

        } catch (SQLException ex) {
            log.log(Level.SEVERE, "Update profile failed", ex);
            // Đoán nhanh lỗi UNIQUE (email/phone) để hiện thông báo thân thiện
            String msg = ex.getMessage();
            String friendly = "Có lỗi khi lưu dữ liệu.";
            if (msg != null) {
                String m = msg.toLowerCase();
                if (m.contains("unique") || m.contains("uq") || m.contains("duplicate")) {
                    if (m.contains("email")) {
                        friendly = "Email đã tồn tại. Vui lòng chọn email khác.";
                    } else if (m.contains("phone")) {
                        friendly = "Số điện thoại đã tồn tại. Vui lòng chọn số khác.";
                    } else {
                        friendly = "Dữ liệu trùng lặp. Vui lòng kiểm tra lại.";
                    }
                } else if (m.contains("check") && m.contains("gender")) {
                    friendly = "Giới tính không hợp lệ. Chỉ nhận Male hoặc Female.";
                }
            }
            req.setAttribute("error", friendly);
            req.setAttribute("user", current);
            req.getRequestDispatcher("/web-page/profile.jsp").forward(req, resp);
        }
    }

    private String z(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
