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

@WebServlet(name = "HomeController", urlPatterns = {"", "/home"})
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SpecializationDAO specDAO = new SpecializationDAO();
        DoctorDAO doctorDAO = new DoctorDAO();

        List<Specialization> featuredSpecs = specDAO.getFeaturedSpecializations(6);
        List<Doctor> featuredDoctors = doctorDAO.getFeaturedDoctors(4);

        request.setAttribute("featuredSpecializations", featuredSpecs);
        request.setAttribute("featuredDoctors", featuredDoctors);

        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}