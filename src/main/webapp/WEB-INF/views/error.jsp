<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.aip.queue.config.Html" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String technicalMessage = (String) request.getAttribute("technicalMessage");
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Something went wrong | QueueEase</title><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css"></head>
<body><header class="topbar"><a class="brand" href="<%= request.getContextPath() %>/home"><span class="brand-mark">Q</span><span>Queue<span class="brand-light">Ease</span></span></a></header>
<main class="narrow-container error-page"><div class="error-icon">!</div><h1>Something went wrong.</h1><p><%= Html.escape(errorMessage == null ? "The requested action could not be completed." : errorMessage) %></p>
<% if (technicalMessage != null) { %><details class="technical-details"><summary>Technical details (for setup/debugging)</summary><code><%= Html.escape(technicalMessage) %></code></details><% } %>
<a class="button button-primary" href="<%= request.getContextPath() %>/home">Return to home</a></main><footer class="footer"><span>QueueEase · Digital Queue Management System</span><span>Academic mini-project</span></footer></body></html>
