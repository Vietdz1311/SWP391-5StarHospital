package Controllers;

import Dao.NotificationDAO;
import Dao.UserDAO;
import Model.Notification;
import Model.User;
import Utils.Email;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "NotificationController", urlPatterns = {"/notifications"})
public class NotificationController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "list";

        switch (action) {
            case "send":
                showSendForm(request, response);
                break;
            case "list":
                listNotifications(request, response);
                break;
            case "view":
                viewNotification(request, response);
                break;
            case "manage":
                manageNotifications(request, response);
                break;
            default:
                listNotifications(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "";

        switch (action) {
            case "send":
                sendNotification(request, response);
                break;
            case "updateStatus":
                updateNotificationStatus(request, response);
                break;
            case "markAllRead":
                markAllAsRead(request, response);
                break;
            default:
                response.sendRedirect("notifications?error=Invalid action");
        }
    }

    private void listNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NotificationDAO dao = new NotificationDAO();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("auth?error=Please login first");
            return;
        }

        Integer userId = null;
        // If user is not admin, only show their notifications
        if (user.getRoleId() != 1) { // Assuming 1 is admin role
            userId = user.getId();
        }

        String type = request.getParameter("type");
        String status = request.getParameter("status");
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            // Default to page 1
        }

        List<Notification> notifications = dao.getAllNotifications(userId, type, status, page, PAGE_SIZE);
        int total = dao.getTotalNotifications(userId, type, status);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        request.setAttribute("notifications", notifications);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("type", type);
        request.setAttribute("status", status);

        request.getRequestDispatcher("./web-page/notifications.jsp").forward(request, response);
    }

    private void viewNotification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("notifications?error=Invalid notification ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            NotificationDAO dao = new NotificationDAO();
            Notification notification = dao.getNotificationById(id);

            if (notification == null) {
                response.sendRedirect("notifications?error=Notification not found");
                return;
            }

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Check if user can view this notification (owner or admin)
            if (user != null && (user.getRoleId() == 1 || user.getId() == notification.getUserId())) {
                // Mark as read if unread
                if ("unread".equalsIgnoreCase(notification.getStatus())) {
                    dao.markAsRead(id, user.getId());
                }
                
                request.setAttribute("notification", notification);
                request.getRequestDispatcher("./web-page/notification-details.jsp").forward(request, response);
            } else {
                response.sendRedirect("notifications?error=You don't have permission to view this notification");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("notifications?error=Invalid notification ID");
        }
    }

    private void showSendForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("auth?error=Please login first");
            return;
        }

        // Only admin can send notifications
        if (user.getRoleId() != 1) {
            response.sendRedirect("notifications?error=You don't have permission to send notifications");
            return;
        }

        // Load users for selection
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        request.setAttribute("users", users);

        request.getRequestDispatcher("./web-page/send-notification.jsp").forward(request, response);
    }

    private void sendNotification(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("auth?error=Please login first");
            return;
        }

        // Only admin can send notifications
        if (user.getRoleId() != 1) {
            response.sendRedirect("notifications?error=You don't have permission to send notifications");
            return;
        }

        String userIdStr = request.getParameter("userId");
        String type = request.getParameter("type");
        String content = request.getParameter("content");
        String sendEmail = request.getParameter("sendEmail"); // Optional email notification

        if (userIdStr == null || userIdStr.isEmpty() || type == null || type.trim().isEmpty() || 
            content == null || content.trim().isEmpty()) {
            response.sendRedirect("notifications?action=send&error=All fields are required");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            UserDAO userDAO = new UserDAO();
            User targetUser = userDAO.getUserById(userId);

            if (targetUser == null) {
                response.sendRedirect("notifications?action=send&error=User not found");
                return;
            }

            NotificationDAO dao = new NotificationDAO();
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType(type.trim());
            notification.setContent(content.trim());
            notification.setSentAt(LocalDateTime.now());
            notification.setStatus("unread");
            notification.setCreatedAt(LocalDateTime.now());
            notification.setUpdatedAt(LocalDateTime.now());
            notification.setCreatedBy(user.getId());
            notification.setUpdatedBy(user.getId());

            int result = dao.addNotification(notification);

            if (result > 0) {
                // Send email if requested
                if ("yes".equalsIgnoreCase(sendEmail) && targetUser.getEmail() != null) {
                    Email emailService = new Email();
                    String emailBody = "<!DOCTYPE html>\n"
                            + "<html lang=\"vi\">\n"
                            + "<head>\n"
                            + "    <meta charset=\"UTF-8\">\n"
                            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                            + "    <title>Thông báo - Bệnh viện Tâm Đức</title>\n"
                            + "    <style>\n"
                            + "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f7fa; margin: 0; padding: 20px; }\n"
                            + "        .container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); }\n"
                            + "        .header { background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%); color: white; padding: 40px 30px; text-align: center; }\n"
                            + "        .header h1 { margin: 0; font-size: 28px; font-weight: bold; }\n"
                            + "        .content { padding: 40px 30px; }\n"
                            + "        .greeting { font-size: 20px; font-weight: 600; color: #1f2937; margin-bottom: 20px; }\n"
                            + "        .message { color: #4b5563; margin-bottom: 30px; white-space: pre-wrap; }\n"
                            + "        .footer { background: #f3f4f6; padding: 20px 30px; text-align: center; color: #6b7280; font-size: 14px; }\n"
                            + "    </style>\n"
                            + "</head>\n"
                            + "<body>\n"
                            + "    <div class=\"container\">\n"
                            + "        <div class=\"header\">\n"
                            + "            <h1>Bệnh viện Tâm Đức</h1>\n"
                            + "        </div>\n"
                            + "        <div class=\"content\">\n"
                            + "            <p class=\"greeting\">Kính gửi " + targetUser.getFullName() + ",</p>\n"
                            + "            <div class=\"message\">" + content.trim().replace("\n", "<br>") + "</div>\n"
                            + "        </div>\n"
                            + "        <div class=\"footer\">\n"
                            + "            <p>Bệnh viện Tâm Đức | Hotline: 1900-1234</p>\n"
                            + "        </div>\n"
                            + "    </div>\n"
                            + "</body>\n"
                            + "</html>";

                    emailService.sendEmail(targetUser.getEmail(), 
                                         "Thông báo từ Bệnh viện Tâm Đức - " + type, 
                                         emailBody, null);
                }

                response.sendRedirect("notifications?success=Notification sent successfully");
            } else {
                response.sendRedirect("notifications?action=send&error=Failed to send notification");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("notifications?action=send&error=Invalid user ID");
        }
    }

    private void manageNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("auth?error=Please login first");
            return;
        }

        NotificationDAO dao = new NotificationDAO();
        
        // Get user's notifications
        Integer userId = null;
        if (user.getRoleId() != 1) {
            userId = user.getId();
        }

        List<Notification> notifications = dao.getAllNotifications(userId, null, null, 1, 100);
        request.setAttribute("notifications", notifications);
        request.getRequestDispatcher("./web-page/manage-notifications.jsp").forward(request, response);
    }

    private void updateNotificationStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("auth?error=Please login first");
            return;
        }

        String idStr = request.getParameter("id");
        String status = request.getParameter("status");

        if (idStr == null || idStr.isEmpty() || status == null || status.trim().isEmpty()) {
            response.sendRedirect("notifications?error=Invalid parameters");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            NotificationDAO dao = new NotificationDAO();
            Notification notification = dao.getNotificationById(id);

            if (notification == null) {
                response.sendRedirect("notifications?error=Notification not found");
                return;
            }

            // Check if user can update this notification
            if (user.getRoleId() != 1 && user.getId() != notification.getUserId()) {
                response.sendRedirect("notifications?error=You don't have permission to update this notification");
                return;
            }

            // Validate status
            if (!status.equalsIgnoreCase("read") && !status.equalsIgnoreCase("unread") && 
                !status.equalsIgnoreCase("archived")) {
                response.sendRedirect("notifications?error=Invalid status");
                return;
            }

            int result = dao.updateNotificationStatus(id, status.toLowerCase(), user.getId());

            if (result > 0) {
                response.sendRedirect("notifications?success=Notification status updated successfully");
            } else {
                response.sendRedirect("notifications?error=Failed to update notification status");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("notifications?error=Invalid notification ID");
        }
    }

    private void markAllAsRead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("auth?error=Please login first");
            return;
        }

        NotificationDAO dao = new NotificationDAO();
        int result = dao.markAllAsRead(user.getId(), user.getId());

        if (result > 0) {
            response.sendRedirect("notifications?success=All notifications marked as read");
        } else {
            response.sendRedirect("notifications?info=No unread notifications");
        }
    }
}

