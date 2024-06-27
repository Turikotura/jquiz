<%@ page import="models.User" %>
<%@ page import="database.Database" %>
<%@ page import="database.UserDatabase" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/27/24
  Time: 8:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<% UserDatabase userDB = (UserDatabase) application.getAttribute(Database.USER_DB);
    String profileName = request.getParameter("username");
    User profileOf = userDB.getByUsername(profileName);
%>
<head>
    <title><%=profileName%></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/profile.css" rel="stylesheet" type="text/css">
</head>
<body>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="index.jsp">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
        </ul>
    </nav>
    <nav class="mail-nav">
        <button onclick="togglePanel()">Show Messages</button>
    </nav>
    <nav class="auth-nav">
        <%if(request.getSession().getAttribute("curUser") == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <%} else {
            String loggedInAs = ((User)request.getSession().getAttribute("curUser")).getUsername();%>
        <ul>
            <li><a href="profile.jsp?username=<%=loggedInAs%>"><%=loggedInAs%></a></li>
            <li><form action="Login" method="get">
                <input type="submit" value="Log out">
            </form></li>
        </ul>
        <%}%>
    </nav>
</header>
<main>
    <img class="profile-pic" src="<%=profileOf.getImage()%>" alt="profile-pic">
    <div class="profile-info">
    <h2><%=profileOf.getUsername()%></h2>
    <h3><%="Created at: " + profileOf.getCreated_at().toString()%></h3>
    </div>
</main>
</body>
</html>
