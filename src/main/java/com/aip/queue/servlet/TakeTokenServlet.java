package com.aip.queue.servlet;

import com.aip.queue.config.Html;
import com.aip.queue.dao.QueueDao;
import com.aip.queue.model.ServiceOption;
import com.aip.queue.model.Ticket;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/take-token")
public class TakeTokenServlet extends HttpServlet {
    private final QueueDao queueDao = new QueueDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String name = request.getParameter("customerName");
        String service = request.getParameter("serviceType");

        try {
            Ticket ticket = queueDao.createTicket(name, service);
            request.setAttribute("ticket", ticket);
            request.getRequestDispatcher("/WEB-INF/views/ticket.jsp").forward(request, response);
        } catch (IllegalArgumentException ex) {
            request.setAttribute("formError", ex.getMessage());
            request.setAttribute("services", ServiceOption.all());
            try {
                request.setAttribute("summaries", queueDao.getServiceSummaries());
            } catch (SQLException dbEx) {
                getServletContext().log("Could not reload queue summary", dbEx);
            }
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Could not create a token", ex);
            request.setAttribute("errorMessage", "The token could not be created. Check the database connection and try again.");
            request.setAttribute("technicalMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
