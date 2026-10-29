<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.aip.queue.model.ServiceOption,com.aip.queue.model.Ticket,com.aip.queue.config.Html" %>
<%
    List<ServiceOption> services = (List<ServiceOption>) request.getAttribute("services");
    List<Ticket> queue = (List<Ticket>) request.getAttribute("queue");
    String selectedService = (String) request.getAttribute("selectedService");
    String message = (String) request.getAttribute("message");
    Ticket activeTicket = null;
    int waitingCount = 0;
    if (queue != null) {
        for (Ticket ticket : queue) {
            if ("CALLED".equals(ticket.getStatus())) activeTicket = ticket;
            if ("WAITING".equals(ticket.getStatus())) waitingCount++;
        }
    }
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Staff Queue Dashboard | QueueEase</title><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css"></head>
<body>
<header class="topbar"><a class="brand" href="<%= request.getContextPath() %>/staff"><span class="brand-mark">Q</span><span>Queue<span class="brand-light">Ease</span></span></a><nav><a href="<%= request.getContextPath() %>/home">Public board</a><form method="post" action="<%= request.getContextPath() %>/staff/logout" class="inline-form"><button class="nav-button nav-button-plain" type="submit">Log out</button></form></nav></header>
<main class="container staff-container">
    <div class="dashboard-title-row"><div><div class="eyebrow">STAFF CONSOLE</div><h1>Queue dashboard</h1><p>Call the next person and manage the service counter.</p></div><span class="admin-tag"><span class="online-dot"></span> Staff signed in</span></div>
    <% if (message != null) { %><div class="alert alert-info"><%= Html.escape(message) %></div><% } %>
    <section class="dashboard-filter panel">
        <form method="get" action="<%= request.getContextPath() %>/staff" class="filter-form"><div><label for="serviceType">Active service</label><select id="serviceType" name="serviceType" onchange="this.form.submit()"><% if (services != null) { for (ServiceOption service : services) { %><option value="<%= Html.escape(service.getName()) %>" <%= service.getName().equals(selectedService) ? "selected" : "" %>><%= Html.escape(service.getName()) %></option><% } } %></select></div><div class="filter-caption">Queue date: today</div></form>
    </section>

    <section class="dashboard-stats">
        <article class="stat-card"><span class="stat-label">NOW CALLING</span><strong class="stat-main"><%= activeTicket == null ? "—" : Html.escape(activeTicket.getDisplayToken()) %></strong><span class="stat-sub"><%= activeTicket == null ? "No active token" : Html.escape(activeTicket.getCustomerName()) %></span></article>
        <article class="stat-card"><span class="stat-label">WAITING TOKENS</span><strong class="stat-main"><%= waitingCount %></strong><span class="stat-sub">In this service queue</span></article>
        <article class="stat-card"><span class="stat-label">SERVICE DESK</span><strong class="stat-service"><%= Html.escape(selectedService) %></strong><span class="stat-sub">Selected department</span></article>
    </section>

    <section class="panel active-ticket-panel">
        <div class="panel-title-row"><div><div class="eyebrow">COUNTER CONTROL</div><h2>Call next token</h2><p>Only one token can be active at a time.</p></div><span class="large-control-icon">↗</span></div>
        <form method="post" action="<%= request.getContextPath() %>/staff/call-next"><input type="hidden" name="serviceType" value="<%= Html.escape(selectedService) %>"><button class="button button-primary" type="submit">Call next waiting token <span aria-hidden="true">→</span></button></form>
        <% if (activeTicket != null) { %>
            <div class="active-token-row"><div><small>CURRENTLY CALLED</small><strong><%= Html.escape(activeTicket.getDisplayToken()) %></strong><span><%= Html.escape(activeTicket.getCustomerName()) %></span></div><div class="action-buttons"><form method="post" action="<%= request.getContextPath() %>/staff/ticket-action"><input type="hidden" name="ticketId" value="<%= activeTicket.getId() %>"><input type="hidden" name="serviceType" value="<%= Html.escape(selectedService) %>"><input type="hidden" name="action" value="serve"><button class="button button-small button-success" type="submit">Mark served</button></form><form method="post" action="<%= request.getContextPath() %>/staff/ticket-action"><input type="hidden" name="ticketId" value="<%= activeTicket.getId() %>"><input type="hidden" name="serviceType" value="<%= Html.escape(selectedService) %>"><input type="hidden" name="action" value="skip"><button class="button button-small button-outline" type="submit">Skip</button></form></div></div>
        <% } else { %><div class="no-active-row"><span class="empty-ring">✓</span><span>No active token. Call the next waiting token when ready.</span></div><% } %>
    </section>

    <section class="panel queue-table-panel">
        <div class="panel-title-row"><div><div class="eyebrow">TODAY'S TOKENS</div><h2>Waiting queue</h2><p>Tokens are normally handled in order of issue.</p></div><span class="queue-count"><%= queue == null ? 0 : queue.size() %> open</span></div>
        <div class="table-wrap"><table class="queue-table"><thead><tr><th>Token</th><th>Name</th><th>Issued</th><th>Status</th></tr></thead><tbody>
        <% boolean foundWaiting = false; if (queue != null) { for (Ticket ticket : queue) { if ("WAITING".equals(ticket.getStatus())) { foundWaiting = true; %>
            <tr><td class="table-token"><%= Html.escape(ticket.getDisplayToken()) %></td><td><%= Html.escape(ticket.getCustomerName()) %></td><td><%= Html.escape(ticket.getCreatedAt()) %></td><td><span class="status-pill status-waiting">WAITING</span></td></tr>
        <% } } } %>
        <% if (!foundWaiting) { %><tr><td colspan="4" class="table-empty">No waiting tokens for this service.</td></tr><% } %>
        </tbody></table></div>
    </section>
</main>
<footer class="footer"><span>QueueEase · Staff console</span><span>Academic mini-project</span></footer>
</body></html>
