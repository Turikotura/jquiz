<%@ page import="database.MailDatabase" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="database.Database" %>
<%@ page import="models.User" %>
<%@ page import="models.Mail" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.MailTypes" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 25.06.2024
  Time: 20:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");
%>

<html>
<head>
    <title>Title</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/sendMail.css" rel="stylesheet" type="text/css">
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
    <form id="send-mail" action="SendMail" method="post">
        <label for="receiver">Receiver:</label>
        <input type="text" id="receiver" name="receiver" required><br><br>

        <label>Choose state:</label><br>
        <input type="radio" id="state1" name="mail-type" value="0">
        <label for="state1">Default</label><br>

        <input type="radio" id="state2" name="mail-type" value="1">
        <label for="state2">Friend request</label><br>

        <input type="radio" id="state3" name="mail-type" value="2">
        <label for="state3">Challenge</label><br><br>

        <div id="quiz-name-field" class="hidden">
            <label for="quiz-name">Quiz Name:</label>
            <input type="text" id="quiz-name" name="quiz-name">
        </div>

        <br>

        <label for="text">Text:</label>
        <input type="text" id="text" name="text" required><br><br>

        <button type="submit">Submit</button>
    </form>
</main>
<%
    if(request.getParameter("error-log") != null){
%>
<p id="error-text"><%=request.getParameter("error-log")%></p>
<%
    }
%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script>
    $(document).ready(function() {
        $('input[name="mail-type"]').change(function() {
            if ($(this).attr('id') === 'state3') {
                $('#quiz-name-field').removeClass('hidden');
                $('#quiz-name').attr('required', true);
            } else {
                $('#quiz-name-field').addClass('hidden');
                $('#quiz-name').attr('required', false).val('');
            }
        });
    });
</script>
<script src="script/general.js"></script>

</body>
</html>