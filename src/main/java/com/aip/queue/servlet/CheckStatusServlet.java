package com.aip.queue.servlet;

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

@WebServlet("/check-status")
public class CheckStatusServlet extends HttpServlet {
    private final QueueDao queueDao = new QueueDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("services", ServiceOption.all());
        request.getRequestDispatcher("/WEB-INF/views/status.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("services", ServiceOption.all());
        String service = request.getParameter("serviceType");
        String tokenText = request.getParameter("tokenNumber");

        if (!ServiceOption.isValid(service)) {
            request.setAttribute("formError", "Please select a valid service.");
            request.getRequestDispatcher("/WEB-INF/views/status.jsp").forward(request, response);
            return;
        }

        int tokenNumber;
        try {
            tokenNumber = Integer.parseInt(tokenText == null ? "" : tokenText.trim());
            if (tokenNumber < 1) {
                throw new NumberFormatException("Token must be positive");
            }
        } catch (NumberFormatException ex) {
            request.setAttribute("formError", "Enter the numeric part of your token, for example 12 for STA-012.");
            request.getRequestDispatcher("/WEB-INF/views/status.jsp").forward(request, response);
            return;
        }

        try {
            Ticket ticket = queueDao.findTicketForToday(service, tokenNumber);
            request.setAttribute("ticket", ticket);
            request.setAttribute("searchedService", service);
            request.setAttribute("searchedTokenNumber", tokenNumber);
            request.getRequestDispatcher("/WEB-INF/views/status.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Could not check ticket status", ex);
            request.setAttribute("errorMessage", "Could not check the token. Check the database connection and try again.");
            request.setAttribute("technicalMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
