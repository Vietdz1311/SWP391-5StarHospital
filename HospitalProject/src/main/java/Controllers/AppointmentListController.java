package Controllers;

import DAO.AppointmentDAO;
import Model.Appointment;
import Model.User;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class AppointmentListController extends HttpServlet {

    private static final int PAGE_SIZE = 5; // số lịch hẹn mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Kiểm tra đăng nhập ---
        HttpSession session = request.getSession(false); // không tạo session mới
        User loggedUser = (User) (session != null ? session.getAttribute("user") : null);

        if (loggedUser == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return; // Dừng hoàn toàn, không xử lý tiếp
        }

        // --- Lấy username ---
        String username = loggedUser.getUsername();
        
        // --- Xác định trang hiện tại ---
        int page = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        String action = request.getParameter("action");
        // --- Gọi DAO ---
        AppointmentDAO dao = new AppointmentDAO();
        if ("details".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            Appointment appointment = dao.getAppointmentById(id);
            request.setAttribute("appointment", appointment);
            request.getRequestDispatcher("appointment-detail.jsp").forward(request, response);
            return;
        }
        int totalAppointments = dao.countAppointmentsByUsername(username);
        int totalPages = (int) Math.ceil((double) totalAppointments / PAGE_SIZE);

        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }
        
        List<Appointment> appointments = dao.getAppointmentsByUsername(username, page, PAGE_SIZE);

        // --- Gửi dữ liệu sang JSP ---
        request.setAttribute("appointments", appointments);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        System.out.println("Username: " + username + ", Total: " + totalAppointments);

        // --- Forward ---
        RequestDispatcher dispatcher = request.getRequestDispatcher("appointments.jsp");
        dispatcher.forward(request, response);
        
        

    }
}
