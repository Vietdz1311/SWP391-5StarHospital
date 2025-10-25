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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = (action != null) ? action : "";
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
        action = (action != null) ? action : "";
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
            String address = request.getParameter("address").trim();
            String username = request.getParameter("username").trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty() || idCardNumber.isEmpty()
                    || birthDateStr.isEmpty() || gender.isEmpty()
                    || address.isEmpty() || username.isEmpty()) {
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
            user.setAddress(address);
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

                    String emailBody = /* (đoạn HTML email giữ nguyên như bạn đã gửi) */ "";
                    emailService.sendEmail(user.getEmail(), "Xác nhận tài khoản - Bệnh viện Tâm Đức", emailBody, null);
                    response.sendRedirect("auth?success=Đăng ký thành công, vui lòng kiểm tra email để kích hoạt tài khoản");
                } else {
                    response.sendRedirect("auth?action=register&error=Failed to send activation email");
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
        String email = (request.getParameter("email") != null) ? request.getParameter("email").trim() : "";
        String password = (request.getParameter("password") != null) ? request.getParameter("password").trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            response.sendRedirect("auth?error=Please enter both email and password");
            return;
        }

        UserDAO userDAO = new UserDAO();
        // Nếu DB đang lưu password MD5, bật 2 dòng dưới và tắt login thường.
        // MD5Hashing md5 = new MD5Hashing();
        // User user = userDAO.login(email, md5.hashPassword(password));

        User user = userDAO.login(email, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user); // <-- đồng bộ với ViewProfileServlet
            String ctx = request.getContextPath();
            response.sendRedirect(ctx + "/home"); // an toàn theo context
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
            String emailBody = /* (đoạn HTML email giữ nguyên như bạn đã gửi) */ "";
            emailService.sendEmail(user.getEmail(), "Xác nhận mã OTP đặt lại mật khẩu - Bệnh viện Tâm Đức", emailBody, null);
            HttpSession session = request.getSession();
            session.setAttribute("resetUserId", user.getId());
            response.sendRedirect("auth?action=verifyOTP&success=OTP đã được gửi đến email của bạn");
        } else {
            response.sendRedirect("auth?action=forgetPassword&error=Failed to send OTP");
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
}
