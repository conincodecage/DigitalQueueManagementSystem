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
import java.net.URLEncoder;
import java.sql.SQLException;

@WebServlet("/staff/ticket-action")
public class TicketActionServlet extends HttpServlet {
    private final QueueDao queueDao = new QueueDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("staffUser") == null) {
            response.sendRedirect(request.getContextPath() + "/staff/login");
            return;
        }

        String service = request.getParameter("serviceType");
        String action = request.getParameter("action");
        long ticketId;
        try {
            ticketId = Long.parseLong(request.getParameter("ticketId"));
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }
        if (!ServiceOption.isValid(service) || !("serve".equals(action) || "skip".equals(action))) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }

        String newStatus = "serve".equals(action) ? "SERVED" : "SKIPPED";
        String messageCode;
        try {
            boolean updated = queueDao.updateCalledTicket(ticketId, service, newStatus);
            messageCode = updated ? ("serve".equals(action) ? "served" : "skipped") : "updated";
            response.sendRedirect(request.getContextPath() + "/staff?serviceType=" +
                    URLEncoder.encode(service, "UTF-8") + "&message=" + messageCode);
        } catch (SQLException | IllegalArgumentException ex) {
            getServletContext().log("Could not update ticket status", ex);
            request.setAttribute("errorMessage", "Could not update the token status. Refresh the page and try again.");
            request.setAttribute("technicalMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
