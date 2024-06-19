<%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/15/24
  Time: 1:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
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
    <label>
        Username:
        <input type="input" name="user-name">
    </label>
    <p></p>
    <label>
        Password:
        <input type="password" name="password">
    </label>
    <p><%=message%></p>
    <input type="submit" value="Log in">
</form>
<span>New around here? <a href="register.jsp">Create new account</a></span>
</body>
</html>
