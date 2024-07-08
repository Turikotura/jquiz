<%@ page import="models.*" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.util.*" %><%--
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
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);

    // Mail variables
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();

    if(curUser != null){
        // Get mails received by user
        mails = mailDB.getMailsByUserId(curUser.getId(),"RECEIVE");
        for(Mail mail : mails){
            // Get names of senders
            senderNames.add(userDB.getById(mail.getSenderId()).getUsername());
            if(mail.getType() == MailTypes.CHALLENGE){
                // Get max grade of senders for challenges
                History history = historyDB.getBestHistoryByUserAndQuizId(mail.getSenderId(),mail.getQuizId());
                int grade = (history == null) ? 0 : history.getGrade();
                maxGrades.put(mail.getId(),grade);
            }
        }
    }
    String message = "";
    if(request.getServletContext().getAttribute("log-message") != null) message = ((String) request.getServletContext().getAttribute("log-message"));
%>
<head>
    <title>Log In</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>
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
        <p style="word-wrap: break-word"><%=message%></p>
        <input type="submit" value="Log in">
    </form>
    <span>New around here? <a href="register.jsp">Create new account</a></span>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>