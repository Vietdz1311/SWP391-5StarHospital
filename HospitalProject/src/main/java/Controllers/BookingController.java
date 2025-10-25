package Controllers;

import DAO.DoctorDAO;
import DAO.SpecializationDAO;

import DAO.UserDAO;
import Dao.AppointmentDAO;
import Dao.AreaDAO;
import Dao.FamilyMemberDAO;
import Dao.RoomDAO;
import Dao.TimeSlotDAO;
import Model.*;
import Utils.ApiEndpoint;
import Utils.AppConstants;
import Utils.Email;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@WebServlet(name = "BookingController", urlPatterns = {"/booking"})
public class BookingController extends HttpServlet {

    private SpecializationDAO specializationDAO;
    private AreaDAO areaDAO;
    private RoomDAO roomDAO;
    private DoctorDAO doctorDAO;
    private TimeSlotDAO timeSlotDAO;
    private FamilyMemberDAO familyMemberDAO;
    private HealthInsuranceDAO healthInsuranceDAO;
    private AppointmentDAO appointmentDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        specializationDAO = new SpecializationDAO();
        areaDAO = new AreaDAO();
        roomDAO = new RoomDAO();
        doctorDAO = new DoctorDAO();
        timeSlotDAO = new TimeSlotDAO();
        familyMemberDAO = new FamilyMemberDAO();
        healthInsuranceDAO = new HealthInsuranceDAO();
        appointmentDAO = new AppointmentDAO();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                        -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context)
                        -> LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                        -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context)
                        -> LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context)
                        -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context)
                        -> LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
                .setPrettyPrinting()
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("auth?action=login");
            return;
        }

        String action = request.getParameter("action");
        if ("loadSpecializations".equals(action)) {
            loadSpecializations(request, response);
        } else if ("loadAreas".equals(action)) {
            loadAreas(request, response);
        } else if ("loadRooms".equals(action)) {
            loadRooms(request, response);
        } else if ("loadDoctors".equals(action)) {
            loadDoctors(request, response);
        } else if ("loadSlots".equals(action)) {
            loadSlots(request, response);
        } else if ("loadFamilyMembers".equals(action)) {
            loadFamilyMembers(request, response);
        } else if ("loadInsurances".equals(action)) {
            loadInsurances(request, response);
        } else if ("loadCurrentUser".equals(action)) {
            loadCurrentUser(request, response);
        } else {
            List<Specialization> specializations = specializationDAO.getAllSpecializations();
            request.setAttribute("specializations", specializations);
            request.getRequestDispatcher("./web-page/booking.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
            return;
        }

        String action = request.getParameter("action");
        if ("bookAppointment".equals(action)) {
            bookAppointment(request, response);
        }
    }

    private void loadSpecializations(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Specialization> specializations = specializationDAO.getAllSpecializations();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(specializations));
    }

    private void loadAreas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Area> areas = areaDAO.getAllAreas();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(areas));
    }

    private void loadRooms(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int specializationId = Integer.parseInt(request.getParameter("specializationId"));
        int areaId = Integer.parseInt(request.getParameter("areaId"));
        List<Room> rooms = roomDAO.getRoomsBySpecializationAndArea(specializationId, areaId);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(rooms));
    }

    private void loadDoctors(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int specializationId = Integer.parseInt(request.getParameter("specializationId"));
        List<Doctor> doctors = doctorDAO.getDoctorsByRoomAndSpecialization(0, specializationId);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(doctors));
    }

    private void loadSlots(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        String dateStr = request.getParameter("date");
        LocalDate date = LocalDate.parse(dateStr);
        List<TimeSlot> slots = timeSlotDAO.getAvailableSlotsByRoomDoctorAndDate(roomId, doctorId, date);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(slots));
    }

    private void loadFamilyMembers(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<FamilyMember> familyMembers = familyMemberDAO.getFamilyMembersByUserId(user.getId());
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(familyMembers));
    }

    private void loadInsurances(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        List<HealthInsurance> insurances = healthInsuranceDAO.getHealthInsurancesByUserId(user.getId());
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(insurances));
    }

    private void loadCurrentUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(user));
    }

    private void bookAppointment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        int patientId = Integer.parseInt(request.getParameter("patientId"));
        int specializationId = Integer.parseInt(request.getParameter("specializationId"));
        int roomId = Integer.parseInt(request.getParameter("roomId"));
        int doctorId = Integer.parseInt(request.getParameter("doctorId"));
        LocalDate appointmentDate = LocalDate.parse(request.getParameter("appointmentDate"));
        LocalTime appointmentTime = LocalTime.parse(request.getParameter("appointmentTime"));
        String healthInsuranceIdStr = request.getParameter("healthInsuranceId");
        Integer healthInsuranceId = healthInsuranceIdStr.isEmpty() ? null : Integer.parseInt(healthInsuranceIdStr);
        String reason = request.getParameter("reason");
        boolean isFollowUp = Boolean.parseBoolean(request.getParameter("isFollowUp"));
        String previousAppointmentIdStr = request.getParameter("previousAppointmentId");
        Integer previousAppointmentId = previousAppointmentIdStr.isEmpty() ? null : Integer.parseInt(previousAppointmentIdStr);

        Appointment appointment = new Appointment();
        appointment.setPatientId(patientId);
        appointment.setSpecializationId(specializationId);
        appointment.setRoomId(roomId);
        appointment.setDoctorId(doctorId);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setHealthInsuranceId(healthInsuranceId);
        appointment.setReason(reason);
        appointment.setPreviousAppointmentId(previousAppointmentId);
        appointment.setFollowUp(isFollowUp);
        appointment.setStatus("Pending");
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setCreatedBy(user.getId());
        int bookingId = appointmentDAO.createAppointment(appointment);
        if (bookingId > 0) {
            String activationToken = UUID.randomUUID().toString();
            OTP activation = new OTP();
            activation.setUserId(user.getId());
            activation.setOtpCode(activationToken);
            activation.setPurpose(AppConstants.CONFIRM_BOOKING_PURPOSE);
            activation.setExpiryTime(LocalDateTime.now().plusMinutes(AppConstants.CONFIRM_BOOKING_EXPIRY_MINUTES));
            activation.setStatus("unused");
            activation.setCreatedAt(LocalDateTime.now());
            activation.setUpdatedAt(LocalDateTime.now());
            UserDAO userDAO = new UserDAO();
            int result = userDAO.addOTP(activation);
            if (result > 0) {
                SpecializationDAO speDao = new SpecializationDAO();
                Doctor docterSend = doctorDAO.getDoctorById(doctorId);
                Specialization spe = speDao.getSpecializationById(specializationId);
                Email emailService = new Email();
                String activationLink = String.format(ApiEndpoint.COMFIRM_BOOKING + "&userId=%d&bookingId=%d&token=%s", user.getId(),bookingId, activationToken);

                String emailBody = "<!DOCTYPE html>\n" +
    "<html lang=\"vi\">\n" +
    "<head>\n" +
    "    <meta charset=\"UTF-8\">\n" +
    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
    "    <title>Xác nhận lịch hẹn - Bệnh viện Tâm Đức</title>\n" +
    "    <style>\n" +
    "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f7fa; margin: 0; padding: 20px; }\n" +
    "        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); }\n" +
    "        .header { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); color: white; padding: 40px 30px; text-align: center; }\n" +
    "        .header h1 { margin: 0; font-size: 28px; font-weight: bold; }\n" +
    "        .header p { margin: 10px 0 0; opacity: 0.9; }\n" +
    "        .content { padding: 40px 30px; }\n" +
    "        .greeting { font-size: 20px; font-weight: 600; color: #1f2937; margin-bottom: 20px; }\n" +
    "        .message { color: #4b5563; margin-bottom: 30px; }\n" +
    "        .appointment-details { background: #f8fafc; border-radius: 8px; padding: 20px; margin: 20px 0; border-left: 4px solid #3b82f6; }\n" +
    "        .details-row { display: flex; justify-content: space-between; margin-bottom: 10px; }\n" +
    "        .details-label { font-weight: 600; color: #374151; }\n" +
    "        .details-value { color: #1f2937; }\n" +
    "        .activation-section { background: #eff6ff; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; border: 1px solid #dbeafe; }\n" +
    "        .activation-link { display: inline-block; background: #3b82f6; color: white; padding: 12px 24px; border-radius: 6px; text-decoration: none; font-weight: 600; margin: 10px 0; transition: background 0.3s; }\n" +
    "        .activation-link:hover { background: #2563eb; }\n" +
    "        .footer { background: #f3f4f6; padding: 20px 30px; text-align: center; color: #6b7280; font-size: 14px; }\n" +
    "        .footer a { color: #3b82f6; text-decoration: none; }\n" +
    "        @media (max-width: 600px) { .container { margin: 10px; } .content { padding: 20px 15px; } }\n" +
    "    </style>\n" +
    "</head>\n" +
    "<body>\n" +
    "    <div class=\"container\">\n" +
    "        <div class=\"header\">\n" +
    "            <h1>Bệnh viện Tâm Đức</h1>\n" +
    "            <p>Chăm sóc sức khỏe toàn diện</p>\n" +
    "        </div>\n" +
    "        <div class=\"content\">\n" +
    "            <p class=\"greeting\">Kính gửi " + user.getFullName() + ",</p>\n" +
    "            <p class=\"message\">\n" +
    "                Cảm ơn bạn đã tin tưởng và đặt lịch hẹn tại Bệnh viện Tâm Đức. Chúng tôi rất vui mừng được phục vụ bạn. Để hoàn tất việc xác nhận lịch hẹn, vui lòng nhấp vào liên kết bên dưới trong vòng 24 giờ.\n" +
    "            </p>\n" +
    "            \n" +
    "            <div class=\"appointment-details\">\n" +
    "                <h3 style=\"margin-top: 0; color: #1f2937; font-size: 18px;\">Chi tiết lịch hẹn</h3>\n" +
    "                <div class=\"details-row\">\n" +
    "                    <span class=\"details-label\">Bác sĩ:</span>\n" +
    "                    <span class=\"details-value\">" + docterSend.getUser().getFullName() + "</span>\n" +
    "                </div>\n" +
    "                <div class=\"details-row\">\n" +
    "                    <span class=\"details-label\">Chuyên khoa:</span>\n" +
    "                    <span class=\"details-value\">" + spe.getSpecializationName() + "</span>\n" +
    "                </div>\n" +
    "                <div class=\"details-row\">\n" +
    "                    <span class=\"details-label\">Ngày hẹn:</span>\n" +
    "                    <span class=\"details-value\">" + appointmentDate + "</span>\n" +
    "                </div>\n" +
    "                <div class=\"details-row\">\n" +
    "                    <span class=\"details-label\">Giờ hẹn:</span>\n" +
    "                    <span class=\"details-value\">" + appointmentTime + "</span>\n" +
    "                </div>\n" +
    "                <div class=\"details-row\">\n" +
    "                    <span class=\"details-label\">Địa điểm:</span>\n" +
    "                    <span class=\"details-value\">Bệnh viện Tâm Đức, TP. Hồ Chí Minh</span>\n" +
    "                </div>\n" +
    "            </div>\n" +
    "            \n" +
    "            <div class=\"activation-section\">\n" +
    "                <p style=\"margin-bottom: 15px; color: #1e40af;\">Xác nhận lịch hẹn của bạn ngay bây giờ:</p>\n" +
    "                <a href=\"" + activationLink + "\" class=\"activation-link\">Xác nhận lịch hẹn</a>\n" +
    "                <p style=\"font-size: 14px; color: #6b7280; margin-top: 15px;\">Nếu liên kết không hoạt động, hãy sao chép và dán vào trình duyệt: " + activationLink + "</p>\n" +
    "            </div>\n" +
    "            \n" +
    "            <p class=\"message\">\n" +
    "                Nếu bạn không đặt lịch hẹn này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi qua hotline: <strong>1900-1234</strong>.\n" +
    "            </p>\n" +
    "            <p style=\"color: #6b7280; font-style: italic;\">Trân trọng,<br>Đội ngũ Bệnh viện Tâm Đức</p>\n" +
    "        </div>\n" +
    "        <div class=\"footer\">\n" +
    "            <p>Bạn nhận được email này vì bạn đã đăng ký hoặc đặt lịch tại Bệnh viện Tâm Đức.</p>\n" +
    "            <p><a href=\"https://tamduc-hospital.com\">www.tamduc-hospital.com</a> | Hotline: 1900-1234</p>\n" +
    "        </div>\n" +
    "    </div>\n" +
    "</body>\n" +
    "</html>";

                emailService.sendEmail(user.getEmail(), "Xác nhận lịch hẹn - Bệnh viện Tâm Đức", emailBody, null);
            } 
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(new BookingResponse(bookingId > 0, bookingId > 0 ? "Appointment booked successfully" : "Failed to book appointment")));
    }

    private static class BookingResponse {

        boolean success;
        String message;

        BookingResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
