<%@ page import="database.*" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 26.06.2024
  Time: 12:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);

    // Mail variables
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();
    // History variables
    List<History> histories = new ArrayList<History>();
    Map<Integer,String> historyQuizNames = new HashMap<Integer,String>();

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

        // Get history for panel
        histories = historyDB.getHistoryByUserId(curUser.getId());
        for(History history : histories){
            historyQuizNames.put(history.getId(),quizDB.getById(history.getQuizId()).getTitle());
        }
    }
%>

<html>
<head>
    <title>Title</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/historySummary.css" rel="stylesheet" type="text/css">
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>
    <%
        if(curUser != null){
            for(History history : histories){
                String isPractice = "";
                if(history.getIsPractice()){
                    isPractice = "practice";
                }
    %>
    <div class="history-box <%=isPractice%>">
        <h3 class="history-grade"><%=history.getGrade()%> Pts</h3>
        <h3 class="history-writing-time"><%=(double)history.getWritingTime()/1000%> Seconds</h3>
        <h4 class="history-completed-at"><%=history.getCompletedAt()%></h4>
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
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>