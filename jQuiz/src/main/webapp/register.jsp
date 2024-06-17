<%--
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
String message = "";
if(request.getServletContext().getAttribute("reg-message") != null) message = ((String) request.getServletContext().getAttribute("reg-message"));
%>
<head>
    <title>Register</title>
</head>
<body>
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
</body>
</html>
