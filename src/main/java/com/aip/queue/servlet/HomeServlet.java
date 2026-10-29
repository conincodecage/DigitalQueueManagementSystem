package com.aip.queue.servlet;

import com.aip.queue.dao.QueueDao;
import com.aip.queue.model.ServiceOption;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final QueueDao queueDao = new QueueDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("services", ServiceOption.all());
        try {
            request.setAttribute("summaries", queueDao.getServiceSummaries());
            request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
        } catch (SQLException ex) {
            getServletContext().log("Could not load the queue summary", ex);
            request.setAttribute("errorMessage", "Could not connect to the database. Check MySQL settings and run DATABASE_SETUP.sql.");
            request.setAttribute("technicalMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
