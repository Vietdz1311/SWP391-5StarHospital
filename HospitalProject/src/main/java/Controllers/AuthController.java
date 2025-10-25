package Controllers;

import DAO.UserDAO;
import Model.OTP;
import Model.User;
import Utils.ApiEndpoint;
import Utils.AppConstants;
import Utils.Email;
import Utils.MD5Hashing;
import Utils.Validation;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@WebServlet(name = "AuthController", urlPatterns = {"/auth"})
public class AuthController extends HttpServlet {

    private static final String OTP_PURPOSE = AppConstants.OTP_PURPOSE_CHANGE_PASSWORD;
    private static final int OTP_EXPIRY_MINUTES = AppConstants.OTP_EXPIRY_MINUTES;
    private static final int ACTIVATION_EXPIRY_HOURS = AppConstants.ACTIVATION_EXPIRY_HOURS;
    private static final String PROVINCES_API = AppConstants.Api.PROVINCES.getUrl();
    private static final String COMMUNES_API = AppConstants.Api.COMMUNES.getUrl();
    private static final String MALE_PROFILE_PIC = AppConstants.Avatar.MALE.getUrl();
    private static final String FEMALE_PROFILE_PIC = AppConstants.Avatar.FEMALE.getUrl();
    private static final String OTHER_PROFILE_PIC = AppConstants.Avatar.OTHER.getUrl();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "";
        switch (action) {
            case "register":
                try {
                List<Map<String, String>> provinces = fetchProvinces();
                request.setAttribute("provinces", provinces);
            } catch (Exception e) {
                System.out.println("Error fetching provinces: " + e.getMessage());
                request.setAttribute("error", "Failed to load provinces. Please try again.");
            }
            request.getRequestDispatcher("./web-page/register.jsp").forward(request, response);
            break;
            case "getCommunes":
                String provinceCode = request.getParameter("provinceCode");
                response.setContentType("application/json");
                try {
                    List<Map<String, String>> communes = fetchCommunes(provinceCode);
                    Gson gson = new Gson();
                    Map<String, List<Map<String, String>>> responseData = new HashMap<>();
                    responseData.put("communes", communes);
                    response.getWriter().write(gson.toJson(responseData));
                } catch (Exception e) {
                    System.out.println("Error fetching communes: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{\"error\": \"Failed to fetch communes\"}");
                }
                break;
            case "forgetPassword":
                request.getRequestDispatcher("./web-page/forgetPassword.jsp").forward(request, response);
                break;
            case "verifyOTP":
                request.getRequestDispatcher("./web-page/verifyOTP.jsp").forward(request, response);
                break;
            case "resetPassword":
                if (request.getSession().getAttribute("otpVerified") == null) {
                    response.sendRedirect("auth?action=forgetPassword&error=Please verify OTP first");
                    return;
                }
                request.getRequestDispatcher("./web-page/resetPassword.jsp").forward(request, response);
                break;
            case "activate":
                activateAccount(request, response);
                break;
            default:
                request.getRequestDispatcher("./web-page/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "";
        switch (action) {
            case "register":
                registerUser(request, response);
                break;
            case "login":
                loginUser(request, response);
                break;
            case "forgetPassword":
                sendOTP(request, response);
                break;
            case "verifyOTP":
                verifyOTP(request, response);
                break;
            case "resetPassword":
                resetPassword(request, response);
                break;
            default:
                loginUser(request, response);
                break;
        }
    }

    private void activateAccount(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userIdStr = request.getParameter("userId");
        String token = request.getParameter("token");

        if (userIdStr == null || token == null || userIdStr.isEmpty() || token.isEmpty()) {
            response.sendRedirect("auth?error=Invalid activation link");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("auth?error=Invalid user ID");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById(userId);
        if (user == null) {
            response.sendRedirect("auth?error=User not found");
            return;
        }

        if (!user.getStatus().toLowerCase().equals("pending")) {
            response.sendRedirect("auth?success=Account already activated, please login");
            return;
        }

        OTP activation = userDAO.getValidOTP(userId, token, "verify_registration");
        if (activation == null) {
            response.sendRedirect("auth?error=Invalid or expired activation token");
            return;
        }

        int result = userDAO.activateUser(userId);
        if (result > 0) {
            userDAO.deactivateOTP(activation.getId());
            response.sendRedirect("auth?success=Account activated successfully, please login");
        } else {
            response.sendRedirect("auth?error=Failed to activate account");
        }
    }

    private void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String fullName = request.getParameter("fullName").trim();
            String email = request.getParameter("email").trim();
            String password = request.getParameter("password").trim();
            String phoneNumber = request.getParameter("phoneNumber").trim();
            String idCardNumber = request.getParameter("idCardNumber").trim();
            String birthDateStr = request.getParameter("birthDate").trim();
            String gender = request.getParameter("gender").trim();
            String detailedAddress = request.getParameter("detailedAddress").trim();
            String username = request.getParameter("username").trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || idCardNumber.isEmpty()
                    || birthDateStr.isEmpty() || gender.isEmpty()
                    || detailedAddress.isEmpty() || username.isEmpty()) {
                response.sendRedirect("auth?action=register&error=Please fill all required fields");
                return;
            }

            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(birthDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                response.sendRedirect("auth?action=register&error=Invalid birth date format");
                return;
            }

            if (!gender.equals("Male") && !gender.equals("Female") && !gender.equals("Other")) {
                response.sendRedirect("auth?action=register&error=Invalid gender");
                return;
            }

            Validation validate = new Validation();
            if (!validate.isValidEmail(email)) {
                response.sendRedirect("auth?action=register&error=Invalid email format");
                return;
            }

            if (!validate.isValidPhoneNumber(phoneNumber)) {
                response.sendRedirect("auth?action=register&error=Invalid phone number format");
                return;
            }

            if (password.length() < 8 || password.contains(" ")) {
                response.sendRedirect("auth?action=register&error=Password must be at least 8 characters and contain no spaces");
                return;
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.isEmailExists(email, 0)) {
                response.sendRedirect("auth?action=register&error=Email already exists");
                return;
            }

            if (userDAO.isPhoneExists(phoneNumber, 0)) {
                response.sendRedirect("auth?action=register&error=Phone number already exists");
                return;
            }

            if (userDAO.isUsernameExists(username, 0)) {
                response.sendRedirect("auth?action=register&error=Username already exists");
                return;
            }

            if (userDAO.isIdCardNumberExists(idCardNumber, 0)) {
                response.sendRedirect("auth?action=register&error=ID card number already exists");
                return;
            }

            String profilePicture;
            switch (gender) {
                case "Male":
                    profilePicture = MALE_PROFILE_PIC;
                    break;
                case "Female":
                    profilePicture = FEMALE_PROFILE_PIC;
                    break;
                default:
                    profilePicture = OTHER_PROFILE_PIC;
            }
            MD5Hashing md5 = new MD5Hashing();
            User user = new User();
            user.setRoleId(2);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPassword(md5.hashPassword(password));
            user.setPhoneNumber(phoneNumber);
            user.setIdCardNumber(idCardNumber);
            user.setBirthDate(birthDate);
            user.setGender(gender);
            user.setProvinceCity("N/A");
            user.setWardCommune("N/A");
            user.setDetailedAddress(detailedAddress);
            user.setUsername(username);
            user.setProfilePicture(profilePicture);
            user.setStatus("pending");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            int userId = userDAO.addUser(user);
            if (userId > 0) {
                String activationToken = UUID.randomUUID().toString();
                OTP activation = new OTP();
                activation.setUserId(userId);
                activation.setOtpCode(activationToken);
                activation.setPurpose("verify_registration");
                activation.setExpiryTime(LocalDateTime.now().plusHours(ACTIVATION_EXPIRY_HOURS));
                activation.setStatus("unused");
                activation.setCreatedAt(LocalDateTime.now());
                activation.setUpdatedAt(LocalDateTime.now());

                int result = userDAO.addOTP(activation);
                if (result > 0) {
                    Email emailService = new Email();
                    String activationLink = ApiEndpoint.ACTIVATE + String.format("&userId=%d&token=%s", userId, activationToken);

                    String emailBody = "<!DOCTYPE html>\n"
                            + "<html lang=\"vi\">\n"
                            + "<head>\n"
                            + "    <meta charset=\"UTF-8\">\n"
                            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                            + "    <title>Xác nhận tài khoản - Bệnh viện Tâm Đức</title>\n"
                            + "    <style>\n"
                            + "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f7fa; margin: 0; padding: 20px; }\n"
                            + "        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); }\n"
                            + "        .header { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); color: white; padding: 40px 30px; text-align: center; }\n"
                            + "        .header h1 { margin: 0; font-size: 28px; font-weight: bold; }\n"
                            + "        .header p { margin: 10px 0 0; opacity: 0.9; }\n"
                            + "        .content { padding: 40px 30px; }\n"
                            + "        .greeting { font-size: 20px; font-weight: 600; color: #1f2937; margin-bottom: 20px; }\n"
                            + "        .message { color: #4b5563; margin-bottom: 30px; }\n"
                            + "        .activation-section { background: #eff6ff; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0; border: 1px solid #dbeafe; }\n"
                            + "        .activation-link { display: inline-block; background: #3b82f6; color: white; padding: 12px 24px; border-radius: 6px; text-decoration: none; font-weight: 600; margin: 10px 0; transition: background 0.3s; }\n"
                            + "        .activation-link:hover { background: #2563eb; }\n"
                            + "        .footer { background: #f3f4f6; padding: 20px 30px; text-align: center; color: #6b7280; font-size: 14px; }\n"
                            + "        .footer a { color: #3b82f6; text-decoration: none; }\n"
                            + "        @media (max-width: 600px) { .container { margin: 10px; } .content { padding: 20px 15px; } }\n"
                            + "    </style>\n"
                            + "</head>\n"
                            + "<body>\n"
                            + "    <div class=\"container\">\n"
                            + "        <div class=\"header\">\n"
                            + "            <h1>Bệnh viện Tâm Đức</h1>\n"
                            + "            <p>Chăm sóc sức khỏe toàn diện</p>\n"
                            + "        </div>\n"
                            + "        <div class=\"content\">\n"
                            + "            <p class=\"greeting\">Kính gửi " + user.getFullName() + ",</p>\n"
                            + "            <p class=\"message\">\n"
                            + "                Chào mừng bạn đến với Bệnh viện Tâm Đức! Để hoàn tất việc kích hoạt tài khoản, vui lòng nhấp vào liên kết bên dưới trong vòng 24 giờ.\n"
                            + "            </p>\n"
                            + "            \n"
                            + "            <div class=\"activation-section\">\n"
                            + "                <p style=\"margin-bottom: 15px; color: #1e40af;\">Kích hoạt tài khoản của bạn ngay bây giờ:</p>\n"
                            + "                <a href=\"" + activationLink + "\" class=\"activation-link\">Kích hoạt tài khoản</a>\n"
                            + "                <p style=\"font-size: 14px; color: #6b7280; margin-top: 15px;\">Nếu liên kết không hoạt động, hãy sao chép và dán vào trình duyệt: " + activationLink + "</p>\n"
                            + "            </div>\n"
                            + "            \n"
                            + "            <p class=\"message\">\n"
                            + "                Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi qua hotline: <strong>1900-1234</strong>.\n"
                            + "            </p>\n"
                            + "            <p style=\"color: #6b7280; font-style: italic;\">Trân trọng,<br>Đội ngũ Bệnh viện Tâm Đức</p>\n"
                            + "        </div>\n"
                            + "        <div class=\"footer\">\n"
                            + "            <p>Bạn nhận được email này vì bạn đã đăng ký tài khoản tại Bệnh viện Tâm Đức.</p>\n"
                            + "            <p><a href=\"https://tamduc-hospital.com\">www.tamduc-hospital.com</a> | Hotline: 1900-1234</p>\n"
                            + "        </div>\n"
                            + "    </div>\n"
                            + "</body>\n"
                            + "</html>";

                    emailService.sendEmail(user.getEmail(), "Xác nhận tài khoản - Bệnh viện Tâm Đức", emailBody, null);
                    String successMsg = "Đăng ký thành công, vui lòng kiểm tra email để kích hoạt tài khoản";
                    String encodedMsg = URLEncoder.encode(successMsg, StandardCharsets.UTF_8.toString());
                    response.sendRedirect("auth?success=" + encodedMsg);
                } else {
                    String errorMsg = "Không thể gửi email kích hoạt";
                    String encodedErr = URLEncoder.encode(errorMsg, StandardCharsets.UTF_8.toString());
                    response.sendRedirect("auth?action=register&error=" + encodedErr);
                }
            } else {
                response.sendRedirect("auth?action=register&error=Registration failed");
            }
        } catch (Exception e) {
            System.out.println("Register error: " + e.getMessage());
            response.sendRedirect("auth?action=register&error=An error occurred during registration");
        }
    }

    private void loginUser(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();

        if (email.isEmpty() || password.isEmpty()) {
            response.sendRedirect("auth?error=Please enter both email and password");
            return;
        }

        UserDAO userDAO = new UserDAO();
        MD5Hashing md5 = new MD5Hashing();
        User user = userDAO.login(email, md5.hashPassword(password));
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("home");
        } else {
            response.sendRedirect("auth?error=Invalid email or password");
        }
    }

    private void sendOTP(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email").trim();
        if (email.isEmpty()) {
            response.sendRedirect("auth?action=forgetPassword&error=Please enter your email");
            return;
        }

        Validation validate = new Validation();
        if (!validate.isValidEmail(email)) {
            response.sendRedirect("auth?action=forgetPassword&error=Invalid email format");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByEmail(email);
        if (user == null) {
            response.sendRedirect("auth?action=forgetPassword&error=Email not found");
            return;
        }

        String otpCode = generateOTP();
        OTP otp = new OTP();
        otp.setUserId(user.getId());
        otp.setOtpCode(otpCode);
        otp.setPurpose("change_password");
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
        otp.setStatus("unused");
        otp.setCreatedAt(LocalDateTime.now());
        otp.setUpdatedAt(LocalDateTime.now());

        int result = userDAO.addOTP(otp);
        if (result > 0) {
            Email emailService = new Email();

            String emailBody = "<!DOCTYPE html>\n"
                    + "<html lang=\"vi\">\n"
                    + "<head>\n"
                    + "    <meta charset=\"UTF-8\">\n"
                    + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                    + "    <title>Mã OTP Đặt Lại Mật Khẩu - Bệnh viện Tâm Đức</title>\n"
                    + "    <style>\n"
                    + "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f7fa; margin: 0; padding: 20px; }\n"
                    + "        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); }\n"
                    + "        .header { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); color: white; padding: 40px 30px; text-align: center; }\n"
                    + "        .header h1 { margin: 0; font-size: 28px; font-weight: bold; }\n"
                    + "        .header p { margin: 10px 0 0; opacity: 0.9; }\n"
                    + "        .content { padding: 40px 30px; }\n"
                    + "        .greeting { font-size: 20px; font-weight: 600; color: #1f2937; margin-bottom: 20px; }\n"
                    + "        .message { color: #4b5563; margin-bottom: 30px; }\n"
                    + "        .otp-section { background: #eff6ff; border-radius: 8px; padding: 30px; text-align: center; margin: 30px 0; border: 1px solid #dbeafe; }\n"
                    + "        .otp-code { display: inline-block; background: #3b82f6; color: white; padding: 20px 40px; border-radius: 8px; text-decoration: none; font-weight: 600; font-size: 32px; letter-spacing: 5px; margin: 10px 0; }\n"
                    + "        .footer { background: #f3f4f6; padding: 20px 30px; text-align: center; color: #6b7280; font-size: 14px; }\n"
                    + "        .footer a { color: #3b82f6; text-decoration: none; }\n"
                    + "        @media (max-width: 600px) { .container { margin: 10px; } .content { padding: 20px 15px; } .otp-code { font-size: 24px; padding: 15px 30px; } }\n"
                    + "    </style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "    <div class=\"container\">\n"
                    + "        <div class=\"header\">\n"
                    + "            <h1>Bệnh viện Tâm Đức</h1>\n"
                    + "            <p>Chăm sóc sức khỏe toàn diện</p>\n"
                    + "        </div>\n"
                    + "        <div class=\"content\">\n"
                    + "            <p class=\"greeting\">Kính gửi " + user.getFullName() + ",</p>\n"
                    + "            <p class=\"message\">\n"
                    + "                Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình. Để tiếp tục, vui lòng sử dụng mã OTP bên dưới. Mã này có hiệu lực trong vòng 10 phút.\n"
                    + "            </p>\n"
                    + "            \n"
                    + "            <div class=\"otp-section\">\n"
                    + "                <p style=\"margin-bottom: 15px; color: #1e40af; font-size: 18px;\">Mã OTP của bạn:</p>\n"
                    + "                <div class=\"otp-code\">" + otpCode + "</div>\n"
                    + "                <p style=\"font-size: 14px; color: #6b7280; margin-top: 15px;\">Nhập mã này vào trang web để hoàn tất quá trình đặt lại mật khẩu.</p>\n"
                    + "            </div>\n"
                    + "            \n"
                    + "            <p class=\"message\">\n"
                    + "                Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này hoặc liên hệ với chúng tôi qua hotline: <strong>1900-1234</strong>.\n"
                    + "            </p>\n"
                    + "            <p style=\"color: #6b7280; font-style: italic;\">Trân trọng,<br>Đội ngũ Bệnh viện Tâm Đức</p>\n"
                    + "        </div>\n"
                    + "        <div class=\"footer\">\n"
                    + "            <p>Bạn nhận được email này vì có hoạt động liên quan đến tài khoản của bạn tại Bệnh viện Tâm Đức.</p>\n"
                    + "            <p><a href=\"https://tamduc-hospital.com\">www.tamduc-hospital.com</a> | Hotline: 1900-1234</p>\n"
                    + "        </div>\n"
                    + "    </div>\n"
                    + "</body>\n"
                    + "</html>";

            emailService.sendEmail(user.getEmail(), "Xác nhận mã OTP đặt lại mật khẩu - Bệnh viện Tâm Đức", emailBody, null);
            HttpSession session = request.getSession();
            session.setAttribute("resetUserId", user.getId());
            String successMsg = "OTP đã được gửi đến email của bạn";
            String encodedMsg = URLEncoder.encode(successMsg, StandardCharsets.UTF_8.toString());
            response.sendRedirect("auth?action=verifyOTP&success=" + encodedMsg);
        } else {
            String successMsg = "Failed to send OTP";
            String encodedMsg = URLEncoder.encode(successMsg, StandardCharsets.UTF_8.toString());
            response.sendRedirect("auth?action=forgetPassword&error=" + encodedMsg);
        }
    }

    private void verifyOTP(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("resetUserId");
        if (userId == null) {
            response.sendRedirect("auth?action=forgetPassword&error=Session expired, please request OTP again");
            return;
        }

        String otpCode = request.getParameter("otp").trim();
        if (otpCode.isEmpty()) {
            response.sendRedirect("auth?action=verifyOTP&error=Please enter OTP");
            return;
        }

        UserDAO userDAO = new UserDAO();
        OTP otp = userDAO.getValidOTP(userId, otpCode, OTP_PURPOSE);
        if (otp != null) {
            userDAO.deactivateOTP(otp.getId());
            session.setAttribute("otpVerified", true);
            response.sendRedirect("auth?action=resetPassword&success=OTP verified, please reset your password");
        } else {
            response.sendRedirect("auth?action=verifyOTP&error=Invalid or expired OTP");
        }
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("resetUserId");
        Boolean otpVerified = (Boolean) session.getAttribute("otpVerified");
        if (userId == null || otpVerified == null || !otpVerified) {
            response.sendRedirect("auth?action=forgetPassword&error=Please verify OTP first");
            return;
        }

        String password = request.getParameter("password").trim();
        String confirmPassword = request.getParameter("confirmPassword").trim();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            response.sendRedirect("auth?action=resetPassword&error=Please enter both password and confirm password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            response.sendRedirect("auth?action=resetPassword&error=Passwords do not match");
            return;
        }

        if (password.length() < 8 || password.contains(" ")) {
            response.sendRedirect("auth?action=resetPassword&error=Password must be at least 8 characters and contain no spaces");
            return;
        }

        UserDAO userDAO = new UserDAO();
        int result = userDAO.updatePassword(password, userId);
        if (result > 0) {
            session.removeAttribute("resetUserId");
            session.removeAttribute("otpVerified");
            response.sendRedirect("auth?success=Password reset successful, please login");
        } else {
            response.sendRedirect("auth?action=resetPassword&error=Failed to reset password");
        }
    }

    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private List<Map<String, String>> fetchProvinces() throws Exception {
        String json = getJsonFromUrl(PROVINCES_API);
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(json, JsonObject.class);
        JsonArray arr = obj.getAsJsonArray("provinces");
        List<Map<String, String>> list = new ArrayList<>();
        for (var element : arr) {
            JsonObject p = element.getAsJsonObject();
            Map<String, String> map = new HashMap<>();
            map.put("code", p.get("code").getAsString());
            map.put("name", p.get("name").getAsString());
            list.add(map);
        }
        return list;
    }

    private List<Map<String, String>> fetchCommunes(String provinceCode) throws Exception {
        String url = String.format(COMMUNES_API, provinceCode);
        String json = getJsonFromUrl(url);
        Gson gson = new Gson();
        JsonObject obj = gson.fromJson(json, JsonObject.class);
        JsonArray arr = obj.getAsJsonArray("communes");
        List<Map<String, String>> list = new ArrayList<>();
        for (var element : arr) {
            JsonObject c = element.getAsJsonObject();
            Map<String, String> map = new HashMap<>();
            map.put("name", c.get("name").getAsString());
            list.add(map);
        }
        return list;
    }

    private boolean isValidProvince(String code) throws Exception {
        List<Map<String, String>> provinces = fetchProvinces();
        for (Map<String, String> p : provinces) {
            if (p.get("code").equals(code)) {
                return true;
            }
        }
        return false;
    }

    private String getProvinceName(String code) throws Exception {
        List<Map<String, String>> provinces = fetchProvinces();
        for (Map<String, String> p : provinces) {
            if (p.get("code").equals(code)) {
                return p.get("name");
            }
        }
        return null;
    }

    private boolean isValidCommune(String name, String provinceCode) throws Exception {
        List<Map<String, String>> communes = fetchCommunes(provinceCode);
        for (Map<String, String> c : communes) {
            if (c.get("name").equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String getJsonFromUrl(String urlStr) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(urlStr))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() != 200) {
            throw new Exception("Failed to fetch data from " + urlStr + ": Status " + res.statusCode());
        }
        return res.body();
    }

    @Override
    public String getServletInfo() {
        return "User management controller for registration, login, and password recovery";
    }
}
