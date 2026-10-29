package com.aip.queue.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/staff/login")
public class StaffLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("staffUser") != null) {
            response.sendRedirect(request.getContextPath() + "/staff");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/staff-login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String expectedUser = setting("QUEUE_ADMIN_USER", "staff");
        String expectedPassword = setting("QUEUE_ADMIN_PASSWORD", "Queue@123");

        if (expectedUser.equals(username) && expectedPassword.equals(password)) {
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("staffUser", expectedUser);
            session.setMaxInactiveInterval(20 * 60);
            response.sendRedirect(request.getContextPath() + "/staff");
        } else {
            request.setAttribute("loginError", "Incorrect username or password.");
            request.getRequestDispatcher("/WEB-INF/views/staff-login.jsp").forward(request, response);
        }
    }

    private String setting(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
