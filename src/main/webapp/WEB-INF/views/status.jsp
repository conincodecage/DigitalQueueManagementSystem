<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.aip.queue.model.ServiceOption,com.aip.queue.model.Ticket,com.aip.queue.config.Html" %>
<%
    List<ServiceOption> services = (List<ServiceOption>) request.getAttribute("services");
    Ticket ticket = (Ticket) request.getAttribute("ticket");
    String formError = (String) request.getAttribute("formError");
    String searchedService = (String) request.getAttribute("searchedService");
    Integer searchedTokenNumber = (Integer) request.getAttribute("searchedTokenNumber");
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Check Token Status | QueueEase</title><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css"></head>
<body>
<header class="topbar"><a class="brand" href="<%= request.getContextPath() %>/home"><span class="brand-mark">Q</span><span>Queue<span class="brand-light">Ease</span></span></a><nav><a href="<%= request.getContextPath() %>/home">Home</a><a class="nav-button" href="<%= request.getContextPath() %>/staff/login">Staff login</a></nav></header>
<main class="narrow-container status-page">
    <div class="eyebrow">TOKEN LOOKUP</div><h1 class="page-title">Check your status</h1><p class="page-intro">Enter the service and numeric part of your token, such as 12 for STA-012.</p>
    <% if (formError != null) { %><div class="alert alert-error"><%= Html.escape(formError) %></div><% } %>
    <section class="panel status-search-panel">
        <form method="post" action="<%= request.getContextPath() %>/check-status" class="token-form">
            <label for="serviceType">Service</label><select name="serviceType" id="serviceType" required><option value="">Choose a service</option>
            <% if (services != null) { for (ServiceOption service : services) { %><option value="<%= Html.escape(service.getName()) %>" <%= service.getName().equals(searchedService) ? "selected" : "" %>><%= Html.escape(service.getName()) %></option><% } } %></select>
            <label for="tokenNumber">Token number (digits only)</label><input id="tokenNumber" name="tokenNumber" type="number" min="1" step="1" placeholder="e.g. 12" value="<%= searchedTokenNumber == null ? "" : searchedTokenNumber %>" required>
            <button type="submit" class="button button-primary button-wide">Check status <span aria-hidden="true">→</span></button>
        </form>
    </section>
    <% if (request.getAttribute("searchedTokenNumber") != null && formError == null) { %>
        <% if (ticket != null) { %>
        <section class="status-result panel">
            <div class="result-top"><div><small>YOUR TOKEN</small><div class="result-token"><%= Html.escape(ticket.getDisplayToken()) %></div></div><span class="status-pill <%= "WAITING".equals(ticket.getStatus()) ? "status-waiting" : ("CALLED".equals(ticket.getStatus()) ? "status-called" : "status-done") %>"><%= Html.escape(ticket.getStatus()) %></span></div>
            <h3><%= Html.escape(ticket.getServiceType()) %></h3><p>Token holder: <strong><%= Html.escape(ticket.getCustomerName()) %></strong></p>
            <% if ("WAITING".equals(ticket.getStatus())) { %><p class="result-message">Your token is waiting. Please keep checking the live queue board.</p>
            <% } else if ("CALLED".equals(ticket.getStatus())) { %><p class="result-message">It's your turn. Please proceed to the service counter.</p>
            <% } else if ("SERVED".equals(ticket.getStatus())) { %><p class="result-message">This token has been served. Thank you.</p>
            <% } else { %><p class="result-message">This token was skipped. Please contact the service counter if you still need help.</p><% } %>
        </section>
        <% } else { %><div class="alert alert-warning">No token was found for that service and number today. Check the service and token number.</div><% } %>
    <% } %>
    <p class="back-link"><a href="<%= request.getContextPath() %>/home">← Back to home</a></p>
</main>
<footer class="footer"><span>QueueEase · Digital Queue Management System</span><span>Academic mini-project</span></footer>
</body></html>
