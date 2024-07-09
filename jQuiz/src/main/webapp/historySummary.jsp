<%@ page import="database.*" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 26.06.2024
  Time: 12:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    // Mail variables
    List<Mail> mails = (List<Mail>) request.getAttribute("mails");
    List<String> senderNames = (List<String>) request.getAttribute("senderNames");
    Map<Integer,Integer> maxGrades = (Map<Integer, Integer>) request.getAttribute("maxGrades");
    // History variables
    List<History> histories = (List<History>) request.getAttribute("histories");
    Map<Integer,String> historyQuizNames = (Map<Integer, String>) request.getAttribute("historyQuizNames");
    Map<Integer,Integer> quizIdToMaxScore = (Map<Integer, Integer>) request.getAttribute("quizIdToMaxScore");
%>

<html>
<head>
    <title>Quiz summary</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/historySummary.css" rel="stylesheet" type="text/css">
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>
    <h1>History</h1>
    <hr><br>
    <div class="history-section">
    <%
        if(curUser != null){
            for(History history : histories){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String compAt = dateFormat.format(history.getCompletedAt());
    %>
    <%--history info--%>
    <div class="history-box">
        <h3 class="history-grade"><%=history.getGrade()%> / <%=quizIdToMaxScore.get(history.getQuizId())%> pts</h3>
        <h3 class="history-writing-time"><%=(double)history.getWritingTime()/1000%> Seconds</h3>
        <h4 class="history-completed-at"><%=compAt%></h4>
        <a class="history-quiz" href="quizInfo.jsp?quizId=<%=history.getQuizId()%>"><%=historyQuizNames.get(history.getId())%></a>
    </div>
    <%
            }
        }else{
    %>
    <p>Log in first</p>
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