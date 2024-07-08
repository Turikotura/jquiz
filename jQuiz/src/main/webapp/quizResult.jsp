<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="database.*" %>
<%@ page import="models.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 22.06.2024
  Time: 14:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    // Mail variables
    List<Mail> mails = (List<Mail>) request.getAttribute("mails");
    List<String> senderNames = (List<String>) request.getAttribute("senderNames");
    Map<Integer,Integer> maxGrades = (Map<Integer, Integer>) request.getAttribute("maxGrades");

    History lastHistory = (History) request.getAttribute("lastHistory");
    int totalScore = (Integer) request.getAttribute("totalScore");

    String bestHistoryName = (String) request.getAttribute("bestHistoryName");
    History bestHistory = (History) request.getAttribute("bestHistory");
    List<History> prevAttempts = (List<History>) request.getAttribute("prevAttempts");
    List<String> friendNames = (List<String>) request.getAttribute("friendNames");
    List<History> friendHistories = (List<History>) request.getAttribute("friendHistories");

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
%>

<html>
<head>
    <title>Title</title>

    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/quizResult.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>

    <h2>Result : <%=lastHistory.getGrade()%> / <%=totalScore%> pts</h2>
    <h2>Writing time : <%=(double) lastHistory.getWritingTime() / 1000%> Seconds</h2>

    <br>
    <br>
    <strong><a href="/">Return</a></strong>

    <br><br><br>
    <h2>Previous Attempts</h2>
    <hr>
    <div class="scroll-container">
        <%
            for(int i = 0; i < prevAttempts.size(); i++){
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String prevCompAt = dateFormat.format(prevAttempts.get(i).getCompletedAt());
        %>
        <div class="history-box">
            <h3><%=prevAttempts.get(i).getGrade()%> / <%=totalScore%></h3>
            <hr>
            <h3><%=(double) prevAttempts.get(i).getWritingTime() / 1000%> Seconds</h3>
            <h4><%=prevCompAt%></h4>
        </div>
        <%
            }
        %>
    </div>

    <br>
    <h2>Top Score</h2>
    <hr>
    <div class="history-box">
        <%
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String bestCompAt = dateFormat.format(bestHistory.getCompletedAt());
        %>
        <h2><a href="profile.jsp?username=<%=bestHistoryName%>"><%=bestHistoryName%></a></h2>
        <h3><%=bestHistory.getGrade()%> / <%=totalScore%></h3>
        <hr>
        <h3><%=(double) bestHistory.getWritingTime() / 1000%> Seconds</h3>
        <h4><%=bestCompAt%></h4>
    </div>

    <br>
    <h2>Friends</h2>
    <hr>
    <div class="scroll-container">
        <%
            for(int i = 0; i < friendHistories.size(); i++){
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String friendCompAt = dateFormat.format(friendHistories.get(i).getCompletedAt());
        %>
        <div class="history-box">
            <h2><a href="profile.jsp?username=<%=friendNames.get(i)%>"><%=friendNames.get(i)%></a></h2>
            <h3><%=friendHistories.get(i).getGrade()%> / <%=totalScore%></h3>
            <hr>
            <h3><%=friendHistories.get(i).getWritingTime()%> Seconds</h3>
            <h4><%=friendCompAt%></h4>
        </div>
        <%
            }
        %>
    </div>

</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>