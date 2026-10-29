<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.aip.queue.model.Ticket,com.aip.queue.config.Html" %>
<%
    Ticket ticket = (Ticket) request.getAttribute("ticket");
%>
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Your Token | QueueEase</title><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css"></head>
<body>
<header class="topbar"><a class="brand" href="<%= request.getContextPath() %>/home"><span class="brand-mark">Q</span><span>Queue<span class="brand-light">Ease</span></span></a><nav><a href="<%= request.getContextPath() %>/home">Home</a><a class="nav-button" href="<%= request.getContextPath() %>/check-status">Check status</a></nav></header>
<main class="narrow-container">
    <section class="success-hero"><div class="success-check">✓</div><div class="eyebrow">TOKEN GENERATED</div><h1>You're in the queue.</h1><p>Keep this token number and check the queue board for updates.</p></section>
    <% if (ticket != null) { %>
    <section class="token-slip">
        <div class="slip-head"><span>DIGITAL QUEUE TOKEN</span><span class="status-pill status-waiting">WAITING</span></div>
        <div class="slip-number"><%= Html.escape(ticket.getDisplayToken()) %></div>
        <div class="slip-service"><%= Html.escape(ticket.getServiceType()) %></div>
        <div class="slip-divider"><span></span><span></span></div>
        <div class="slip-details"><div><small>NAME</small><strong><%= Html.escape(ticket.getCustomerName()) %></strong></div><div><small>ISSUED AT</small><strong><%= Html.escape(ticket.getCreatedAt()) %></strong></div></div>
        <div class="slip-note">Please watch the live queue board. A staff member will call your token when the counter is ready.</div>
        <div class="slip-actions"><a class="button button-primary" href="<%= request.getContextPath() %>/check-status">Check my status</a><a class="button button-outline" href="<%= request.getContextPath() %>/home">Back to home</a></div>
    </section>
    <% } else { %><div class="alert alert-error">Ticket details could not be displayed. Please return home and try again.</div><% } %>
</main>
<footer class="footer"><span>QueueEase · Digital Queue Management System</span><span>Academic mini-project</span></footer>
</body></html>
