package Controllers;

import Dao.FeedbackDAO;
import Model.Feedback;
import Model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "FeedbackController", urlPatterns = {"/feedback"})
public class FeedbackController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "list";

        switch (action) {
            case "view":
                viewFeedback(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "list":
                listFeedbacks(request, response);
                break;
            default:
                listFeedbacks(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "";

        switch (action) {
            case "edit":
                updateFeedback(request, response);
                break;
            case "delete":
                deleteFeedback(request, response);
                break;
            default:
                response.sendRedirect("feedback?error=Invalid action");
        }
    }

    private void listFeedbacks(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        FeedbackDAO dao = new FeedbackDAO();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        Integer userId = null;
        String status = request.getParameter("status");
        
        // If user is patient, only show their feedbacks. Admin can see all
        if (user != null && user.getRoleId() == 2) { // Assuming 2 is patient role
            userId = user.getId();
        }

        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            // Default to page 1
        }

        java.util.List<Feedback> feedbacks = dao.getAllFeedbacks(userId, null, status, page, PAGE_SIZE);
        int total = dao.getTotalFeedbacks(userId, null, status);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        request.setAttribute("feedbacks", feedbacks);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("status", status);

        request.getRequestDispatcher("./web-page/feedbacks.jsp").forward(request, response);
    }

    private void viewFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            FeedbackDAO dao = new FeedbackDAO();
            Feedback feedback = dao.getFeedbackById(id);

            if (feedback == null) {
                response.sendRedirect("feedback?error=Feedback not found");
                return;
            }

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Check if user can view this feedback (owner or admin)
            if (user != null && (user.getRoleId() == 1 || user.getId() == feedback.getUserId())) {
                request.setAttribute("feedback", feedback);
                request.getRequestDispatcher("./web-page/feedback-details.jsp").forward(request, response);
            } else {
                response.sendRedirect("feedback?error=You don't have permission to view this feedback");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
        }
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            FeedbackDAO dao = new FeedbackDAO();
            Feedback feedback = dao.getFeedbackById(id);

            if (feedback == null) {
                response.sendRedirect("feedback?error=Feedback not found");
                return;
            }

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Check if user can edit this feedback (owner or admin)
            if (user != null && (user.getRoleId() == 1 || user.getId() == feedback.getUserId())) {
                request.setAttribute("feedback", feedback);
                request.getRequestDispatcher("./web-page/feedback-edit.jsp").forward(request, response);
            } else {
                response.sendRedirect("feedback?error=You don't have permission to edit this feedback");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
        }
    }

    private void updateFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            FeedbackDAO dao = new FeedbackDAO();
            Feedback existingFeedback = dao.getFeedbackById(id);

            if (existingFeedback == null) {
                response.sendRedirect("feedback?error=Feedback not found");
                return;
            }

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Check if user can edit this feedback
            if (user == null || (user.getRoleId() != 1 && user.getId() != existingFeedback.getUserId())) {
                response.sendRedirect("feedback?error=You don't have permission to edit this feedback");
                return;
            }

            String ratingStr = request.getParameter("rating");
            String comment = request.getParameter("comment");
            String status = request.getParameter("status");

            if (ratingStr == null || ratingStr.isEmpty()) {
                response.sendRedirect("feedback?action=edit&id=" + id + "&error=Rating is required");
                return;
            }

            try {
                int rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    response.sendRedirect("feedback?action=edit&id=" + id + "&error=Rating must be between 1 and 5");
                    return;
                }

                Feedback feedback = new Feedback();
                feedback.setId(id);
                feedback.setRating(rating);
                feedback.setComment(comment != null ? comment.trim() : "");
                
                // Only admin can change status
                if (user.getRoleId() == 1 && status != null && !status.trim().isEmpty()) {
                    feedback.setStatus(status.trim());
                } else {
                    feedback.setStatus(existingFeedback.getStatus());
                }
                
                feedback.setUpdatedAt(LocalDateTime.now());
                feedback.setUpdatedBy(user.getId());

                int result = dao.updateFeedback(feedback);
                if (result > 0) {
                    response.sendRedirect("feedback?action=view&id=" + id + "&success=Feedback updated successfully");
                } else {
                    response.sendRedirect("feedback?action=edit&id=" + id + "&error=Failed to update feedback");
                }
            } catch (NumberFormatException e) {
                response.sendRedirect("feedback?action=edit&id=" + id + "&error=Invalid rating format");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
        }
    }

    private void deleteFeedback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            FeedbackDAO dao = new FeedbackDAO();
            Feedback feedback = dao.getFeedbackById(id);

            if (feedback == null) {
                response.sendRedirect("feedback?error=Feedback not found");
                return;
            }

            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            
            // Check if user can delete this feedback (owner or admin)
            if (user == null || (user.getRoleId() != 1 && user.getId() != feedback.getUserId())) {
                response.sendRedirect("feedback?error=You don't have permission to delete this feedback");
                return;
            }

            int result = dao.deleteFeedback(id);
            if (result > 0) {
                response.sendRedirect("feedback?success=Feedback deleted successfully");
            } else {
                response.sendRedirect("feedback?error=Failed to delete feedback");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("feedback?error=Invalid feedback ID");
        }
    }
}

