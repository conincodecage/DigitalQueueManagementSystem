package com.aip.queue.servlet;

import com.aip.queue.dao.QueueDao;
import com.aip.queue.model.ServiceOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/staff")
public class StaffDashboardServlet extends HttpServlet {
    private final QueueDao queueDao = new QueueDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("staffUser") == null) {
            response.sendRedirect(request.getContextPath() + "/staff/login");
            return;
        }

        String service = request.getParameter("serviceType");
        if (!ServiceOption.isValid(service)) {
            service = ServiceOption.all().get(0).getName();
        }
        request.setAttribute("selectedService", service);
        request.setAttribute("services", ServiceOption.all());
        request.setAttribute("queue", java.util.Collections.emptyList());
        request.setAttribute("message", messageFor(request.getParameter("message")));
        try {
            request.setAttribute("queue", queueDao.getOpenQueue(service));
            request.getRequestDispatcher("/WEB-INF/views/staff-dashboard.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Could not load staff dashboard", ex);
            request.setAttribute("errorMessage", "Could not load the staff queue. Check the database connection.");
            request.setAttribute("technicalMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    private String messageFor(String code) {
        if ("called".equals(code)) return "The next token has been called.";
        if ("active".equals(code)) return "A token is already being served. Mark it served or skip it first.";
        if ("empty".equals(code)) return "There are no waiting tokens for this service.";
        if ("served".equals(code)) return "Token marked as served.";
        if ("skipped".equals(code)) return "Token skipped.";
        if ("updated".equals(code)) return "Queue updated.";
        return null;
    }
}
