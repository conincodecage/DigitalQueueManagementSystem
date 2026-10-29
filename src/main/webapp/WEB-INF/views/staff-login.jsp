<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.aip.queue.config.Html" %>
<%
    String loginError = (String) request.getAttribute("loginError");
%>
<!DOCTYPE html>
<html lang="en"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>Staff Login | QueueEase</title><link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css"></head>
<body class="login-background">
<header class="topbar"><a class="brand" href="<%= request.getContextPath() %>/home"><span class="brand-mark">Q</span><span>Queue<span class="brand-light">Ease</span></span></a><nav><a href="<%= request.getContextPath() %>/home">Public home</a></nav></header>
<main class="login-container">
    <div class="login-icon">⌘</div><div class="eyebrow">STAFF AREA</div><h1>Welcome back.</h1><p class="page-intro">Sign in to manage your service counter queue.</p>
    <% if (loginError != null) { %><div class="alert alert-error"><%= Html.escape(loginError) %></div><% } %>
    <section class="panel login-panel">
        <form method="post" action="<%= request.getContextPath() %>/staff/login" class="token-form">
            <label for="username">Username</label><input id="username" name="username" type="text" required autocomplete="username" placeholder="Enter staff username">
            <label for="password">Password</label><input id="password" name="password" type="password" required autocomplete="current-password" placeholder="Enter password">
            <button class="button button-primary button-wide" type="submit">Sign in <span aria-hidden="true">→</span></button>
        </form>
    </section>
    <p class="demo-hint">Classroom demo credentials are listed in README.md. Change them before any non-demo use.</p>
</main>
<footer class="footer"><span>QueueEase · Staff console</span><span>Academic mini-project</span></footer>
</body></html>
