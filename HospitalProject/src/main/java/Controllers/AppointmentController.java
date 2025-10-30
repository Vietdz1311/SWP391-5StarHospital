package Controllers;

import Dao.AppointmentDAO;
import Dao.DoctorDAO;
import Model.Appointment;
import Model.Doctor;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AppointmentController", urlPatterns = {"/appointments"})
public class AppointmentController extends HttpServlet {
    private AppointmentDAO appointmentDAO;
    private static final int PAGE_SIZE = 10;

    @Override
    public void init() throws ServletException {
        appointmentDAO = new AppointmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("auth?action=login");
            return;
        }
        User user = (User) session.getAttribute("user");

        String action = request.getParameter("action");
        if ("details".equals(action)) {
            showDetails(request, response, user.getId());
        } else {
            showList(request, response, user.getId());
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        String search = request.getParameter("search");
        String filterStatus = request.getParameter("filterStatus");
        String sortBy = request.getParameter("sortBy");
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            
        }

        List<Appointment> appointments = appointmentDAO.getAppointmentsForUser(userId, search, filterStatus, sortBy, page, PAGE_SIZE);
        DoctorDAO doctorDao = new DoctorDAO();
        for (Appointment appointment : appointments) {
            Doctor doctor = doctorDao.getHeaderDoctorById(appointment.getDoctorId());
            appointment.setDoctor(doctor);
        }
        int total = appointmentDAO.getTotalAppointmentsForUser(userId, search, filterStatus);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        request.setAttribute("appointments", appointments);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("search", search);
        request.setAttribute("filterStatus", filterStatus);
        request.setAttribute("sortBy", sortBy);

        request.getRequestDispatcher("./web-page/appointments.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response, int userId)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Appointment appointment = appointmentDAO.getAppointmentById(id, userId);
        if (appointment == null) {
            request.setAttribute("error", "Appointment not found or unauthorized");
            request.getRequestDispatcher("./web-page/appointments.jsp").forward(request, response);
            return;
        }
        
        DoctorDAO doctorDao = new DoctorDAO();
        Doctor doctor = doctorDao.getHeaderDoctorById(appointment.getDoctorId());
        appointment.setDoctor(doctor);
        request.setAttribute("appointment", appointment);
        request.getRequestDispatcher("./web-page/appointment-details.jsp").forward(request, response);
    }
}