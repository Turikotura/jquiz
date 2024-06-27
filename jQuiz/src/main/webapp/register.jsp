<%@ page import="models.User" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/15/24
  Time: 1:53 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    String message = "";
    if(request.getServletContext().getAttribute("reg-message") != null) message = ((String) request.getServletContext().getAttribute("reg-message"));
%>
<head>
    <title>Register</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
</head>
<body>

<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="/">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
            <li><a href="/historySummary.jsp">History</a></li>
        </ul>
    </nav>
    <nav class="mail-nav">
        <ul>
            <li><a onclick="togglePanel()">Show Messages</a></li>
        </ul>
    </nav>
    <nav class="auth-nav">
        <%if(curUser == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <%} else { %>
        <ul>
            <li><a href="#"><%=curUser.getUsername()%></a></li>
            <li><a onclick="submitLogOut()">Log out</a></li>
            <form id="log-out-form" style="display: none" action="Login" method="get"></form>
        </ul>
        <%}%>

    </nav>
</header>

<main>
    <h1>Create New Account</h1>
    <p>Please enter proposed name and password.</p>
    <form action="Register" method="post">
        <label>
            Enter your email:
            <input type="email" name="email">
        </label>
        <p></p>
        <label>
            Username:
            <input type="input" name="user-name">
        </label>
        <p></p>
        <label>
            Password:
            <input type="password" name="password1">
        </label>
        <p></p>
        <label>
            Re-enter your Password:
            <input type="password" name="password2">
        </label>
        <p></p>
        <label>
            Link to your profile photo (Not required):
            <input type="input" name="profile-pic">
        </label>
        <p><%=message%></p>
        <input type="submit" value="Create">
    </form>
    <span>Already have an account? <a href="login.jsp">Log in</a></span>
</main>

<script src="script/general.js"></script>

</body>
</html>
