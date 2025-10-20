package Controllers;

import DAO.DoctorDAO;
import DAO.SpecializationDAO;
import Model.Doctor;
import Model.Specialization;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SpecializationController", urlPatterns = {"/specializations"})
public class SpecializationController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        action = action != null ? action : "list";

        switch (action) {
            case "details":
                showDetails(request, response);
                break;
            default:
                showList(request, response);
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SpecializationDAO dao = new SpecializationDAO();

        String search = request.getParameter("search");
        String filterStatus = request.getParameter("filterStatus");
        String sortBy = request.getParameter("sortBy");
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
        }

        List<Specialization> specializations = dao.getAllSpecializations(search, filterStatus, sortBy, page, PAGE_SIZE);
        int total = dao.getTotalSpecializations(search, filterStatus);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);

        request.setAttribute("specializations", specializations);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("search", search);
        request.setAttribute("filterStatus", filterStatus);
        request.setAttribute("sortBy", sortBy);

        request.getRequestDispatcher("./web-page/departments.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        SpecializationDAO dao = new SpecializationDAO();
        DoctorDAO doctorDAO = new DoctorDAO();

        Specialization spec = dao.getSpecializationById(id);
        List<Doctor> doctors = doctorDAO.getDoctorsBySpecialization(id);
        request.setAttribute("specialization", spec);
        request.setAttribute("doctors", doctors);

        request.getRequestDispatcher("./web-page/department-details.jsp").forward(request, response);
    }
}