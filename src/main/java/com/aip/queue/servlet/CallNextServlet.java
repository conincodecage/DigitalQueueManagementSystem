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

@WebServlet("/staff/call-next")
public class CallNextServlet extends HttpServlet {
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
        if (!ServiceOption.isValid(service)) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }
        try {
            QueueDao.CallNextResult result = queueDao.callNext(service);
            response.sendRedirect(request.getContextPath() + "/staff?serviceType=" +
                    java.net.URLEncoder.encode(service, "UTF-8") + "&message=" + result.getMessageCode());
        } catch (SQLException | IllegalArgumentException ex) {
            getServletContext().log("Could not call the next token", ex);
            request.setAttribute("errorMessage", "Could not call the next token. Refresh the page and try again.");
            request.setAttribute("technicalMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
