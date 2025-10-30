package Controllers;


import Dao.DoctorDAO;
import Dao.AppointmentDAO;
import Dao.OtpDAO;
import Model.Appointment;
import Model.Doctor;
import Model.OTP;
import Utils.AppConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "BookingConfirmController", urlPatterns = {"/booking-confirm"})
public class BookingConfirmController extends HttpServlet {
    private AppointmentDAO appointmentDAO;

    @Override
    public void init() throws ServletException {
        appointmentDAO = new AppointmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "";
        if ("confirm".equals(action)) {
            confirmBooking(request, response);
        } else if ("pre-confirm".equals(action)) {
            request.getRequestDispatcher("./web-page/booking_notification.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Invalid action");
            request.getRequestDispatcher("./web-page/error_confirm.jsp").forward(request, response);
        }
    }

    private void confirmBooking(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int bookingId = Integer.parseInt(request.getParameter("bookingId"));
            int userId = Integer.parseInt(request.getParameter("userId"));
            String token = request.getParameter("token");

            OtpDAO otpDao = new OtpDAO();
            OTP confirmOtp = otpDao.getValidOTP(userId, token, AppConstants.CONFIRM_BOOKING_PURPOSE);
            
            Appointment appointment = appointmentDAO.getAppointmentById(bookingId, userId);
            if (appointment == null || !appointment.getStatus().equals("Pending")) {
                request.setAttribute("error", "Invalid or non-pending appointment");
                request.getRequestDispatcher("./web-page/error_confirm.jsp").forward(request, response);
                return;
            }

            if (confirmOtp == null) {
                request.setAttribute("error", "Invalid confirmation token");
                request.getRequestDispatcher("./web-page/error_confirm.jsp").forward(request, response);
                return;
            }

            boolean updated = appointmentDAO.updateAppointmentStatus(bookingId, "Confirmed");
            if (!updated) {
                request.setAttribute("error", "Failed to confirm appointment");
                request.getRequestDispatcher("./web-page/error_confirm.jsp").forward(request, response);
                return;
            }
            
            otpDao.deactivateOTP(confirmOtp.getId());

            DoctorDAO doctorDao = new DoctorDAO();
            Doctor doctor = doctorDao.getHeaderDoctorById(appointment.getDoctorId());
            request.setAttribute("doctorName", doctor.getUser().getFullName());
            request.setAttribute("appointmentDate", appointment.getAppointmentDate().toString());
            request.setAttribute("appointmentTime", appointment.getAppointmentTime().toString());
            request.getRequestDispatcher("./web-page/booking_confirm.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid booking ID or user ID");
            request.getRequestDispatcher("./web-page/error_confirm.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setAttribute("error", "Method not supported");
        request.getRequestDispatcher("error.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles appointment confirmation";
    }
}