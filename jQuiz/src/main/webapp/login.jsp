<%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/15/24
  Time: 1:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    String message = "";
    if(request.getServletContext().getAttribute("log-message") != null) message = ((String) request.getServletContext().getAttribute("log-message"));
%>
<head>
    <title>Log In</title>
</head>
<body>
<h1>Please log in.</h1>
<p>Enter your username and password:</p>
<form action="Login" method="post">
    <label>Username:</label>
    <input type="input" name="user-name">
    <p></p>
    <label>Password:</label>
    <input type="password" name="password">
    <p><%=message%></p>
    <input class="login" type="submit" value="Login">
</form>
<span>New around here? <a href="register.jsp">Create new account</a></span>
</body>
</html>
