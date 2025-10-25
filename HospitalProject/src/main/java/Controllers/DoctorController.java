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

@WebServlet(name = "DoctorController", urlPatterns = {"/doctors"})
public class DoctorController extends HttpServlet {

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
       DoctorDAO dao = new DoctorDAO();
        SpecializationDAO specDAO = new SpecializationDAO();

        String search = request.getParameter("search");
        Integer filterSpecialization = null;
        try {
            filterSpecialization = Integer.parseInt(request.getParameter("filterSpecialization"));
        } catch (NumberFormatException e) {
            // No filter
        }
        String sortBy = request.getParameter("sortBy");
        int page = 1;
        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            // Default to page 1
        }

        List<Doctor> doctors = dao.getAllDoctors(search, filterSpecialization, sortBy, page, PAGE_SIZE);
        int total = dao.getTotalDoctors(search, filterSpecialization);
        int totalPages = (int) Math.ceil((double) total / PAGE_SIZE);
        List<Specialization> specializations = specDAO.getAllSpecializations(null, "Active", null, 1, Integer.MAX_VALUE);

        request.setAttribute("doctors", doctors);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", page);
        request.setAttribute("search", search);
        request.setAttribute("filterSpecialization", filterSpecialization);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("specializations", specializations);

        request.getRequestDispatcher("./web-page/doctors.jsp").forward(request, response);
    }

    private void showDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        DoctorDAO dao = new DoctorDAO();

        Doctor doctor = dao.getDoctorById(id);

        request.setAttribute("doctor", doctor);

        request.getRequestDispatcher("./web-page/doctor-details.jsp").forward(request, response);
    }
}