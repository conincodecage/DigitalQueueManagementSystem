<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List,com.aip.queue.model.ServiceOption,com.aip.queue.model.ServiceSummary,com.aip.queue.config.Html" %>
<%
    List<ServiceOption> services = (List<ServiceOption>) request.getAttribute("services");
    List<ServiceSummary> summaries = (List<ServiceSummary>) request.getAttribute("summaries");
    String formError = (String) request.getAttribute("formError");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Digital Queue | Home</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">
</head>
<body>
<header class="topbar">
    <a class="brand" href="<%= request.getContextPath() %>/home"><span class="brand-mark">Q</span><span>Queue<span class="brand-light">Ease</span></span></a>
    <nav><a href="#take-token">Get a token</a><a href="#live-queue">Live queue</a><a class="nav-button" href="<%= request.getContextPath() %>/staff/login">Staff login</a></nav>
</header>
<main class="container">
    <section class="hero">
        <div class="hero-copy">
            <div class="eyebrow"><span class="pulse-dot"></span> CAMPUS SERVICE QUEUE</div>
            <h1>Less waiting.<br><span>More doing.</span></h1>
            <p>Take a digital token for a campus service and check the queue before you head to the counter.</p>
            <a class="button button-primary" href="#take-token">Get your token <span aria-hidden="true">→</span></a>
            <div class="hero-note"><span class="check-icon">✓</span> Free, simple and no app installation needed</div>
        </div>
        <div class="hero-art" aria-label="Illustration of an electronic queue token">
            <div class="orbit orbit-one"></div><div class="orbit orbit-two"></div>
            <div class="ticket-art"><div class="ticket-art-top">NOW SERVING</div><div class="ticket-art-number">STA-012</div><div class="ticket-art-line"></div><div class="ticket-art-bottom"><span>Student Affairs</span><span class="mini-pill">Your turn soon</span></div></div>
            <div class="floating-card card-top"><span class="floating-icon">⌁</span><span><strong>Digital token</strong><small>Ready in seconds</small></span></div>
            <div class="floating-card card-bottom"><span class="online-dot"></span><span><strong>Live queue</strong><small>Check anytime</small></span></div>
        </div>
    </section>

    <section class="section" id="take-token">
        <div class="section-heading"><div><div class="eyebrow">01 / START HERE</div><h2>Get your queue token</h2><p>Enter your name and choose the service you need.</p></div><div class="step-bubble">1 <span>of</span> 2</div></div>
        <% if (formError != null) { %><div class="alert alert-error"><%= Html.escape(formError) %></div><% } %>
        <div class="panel form-panel">
            <form method="post" action="<%= request.getContextPath() %>/take-token" class="token-form">
                <label for="customerName">Your name</label>
                <input id="customerName" name="customerName" type="text" maxlength="100" placeholder="e.g. Amandeep Singh" required autocomplete="name">
                <label for="serviceType">Select campus service</label>
                <select id="serviceType" name="serviceType" required>
                    <option value="">Choose a service</option>
                    <% if (services != null) { for (ServiceOption service : services) { %>
                        <option value="<%= Html.escape(service.getName()) %>"><%= Html.escape(service.getName()) %></option>
                    <% } } %>
                </select>
                <button type="submit" class="button button-primary button-wide">Generate my token <span aria-hidden="true">→</span></button>
                <p class="form-hint"><span aria-hidden="true">ⓘ</span> Tokens reset for each service each day.</p>
            </form>
            <div class="form-side">
                <div class="side-icon">⌕</div><h3>Already have a token?</h3><p>Check whether your token is waiting, being served, or completed.</p>
                <a class="text-link" href="<%= request.getContextPath() %>/check-status">Check token status <span aria-hidden="true">→</span></a>
            </div>
        </div>
    </section>

    <section class="section" id="live-queue">
        <div class="section-heading"><div><div class="eyebrow">02 / LIVE OVERVIEW</div><h2>Campus queue board</h2><p>Current counter status for today's queues.</p></div><span class="live-label"><span class="online-dot"></span> Today's queues</span></div>
        <div class="service-grid">
            <% if (summaries != null) { for (ServiceSummary summary : summaries) { %>
                <article class="service-card">
                    <div class="service-card-top"><span class="service-symbol"><%= Html.escape(ServiceOption.prefixFor(summary.getServiceName()).substring(0, 1)) %></span><span class="queue-label"><%= summary.getWaitingCount() %> waiting</span></div>
                    <h3><%= Html.escape(summary.getServiceName()) %></h3>
                    <div class="now-serving-label">NOW SERVING</div>
                    <div class="now-serving-number"><%= Html.escape(summary.getCurrentTokenDisplay()) %></div>
                    <div class="service-card-footer"><span>Estimated wait varies</span><span class="small-arrow">↗</span></div>
                </article>
            <% } } else { %>
                <div class="empty-state">Queue information is currently unavailable.</div>
            <% } %>
        </div>
    </section>
    <section class="bottom-cta"><div><div class="eyebrow">A SIMPLER CAMPUS VISIT</div><h2>Your time matters.</h2><p>Take a token before joining the queue.</p></div><a class="button button-light" href="#take-token">Get a token <span aria-hidden="true">→</span></a></section>
</main>
<footer class="footer"><span>QueueEase · Digital Queue Management System</span><span>Academic mini-project · Java Servlet, JSP & JDBC</span></footer>
</body>
</html>
