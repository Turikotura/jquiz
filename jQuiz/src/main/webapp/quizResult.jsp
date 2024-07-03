<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="database.*" %>
<%@ page import="models.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 22.06.2024
  Time: 14:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    QuestionDatabase questionDB = getDatabase(Database.QUESTION_DB,request);

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

    int userId = Integer.parseInt(request.getParameter("userId"));
    int quizId = Integer.parseInt(request.getParameter("quizId"));

    History lastHistory = null;
    Quiz quiz = null;
    List<Question> questionList = new ArrayList<Question>();
    int totalScore = 0;

    String bestHistoryName = "";
    History bestHistory = null;
    List<History> prevAttempts = new ArrayList<History>();
    List<String> friendNames = new ArrayList<String>();
    List<History> friendHistories = new ArrayList<History>();
    try {
        lastHistory = historyDB.getLastHistoryByUserAndQuizId(userId, quizId);
        quiz = quizDB.getById(lastHistory.getQuizId());
        questionList = questionDB.getQuestionsByQuizId(quiz.getId());
        for (Question question : questionList) {
            totalScore += question.getScore();
        }

        // others results
        bestHistory = historyDB.getBestScoreHistoryByQuizId(quizId);
        bestHistoryName = userDB.getById(bestHistory.getUserId()).getUsername();
        prevAttempts = historyDB.getHistoryByUserAndQuizId(userId, quizId);
        List<User> friends = new ArrayList<User>();
        friends = userDB.getFriendsByUserId(userId);
        for(User friend : friends){
            History frHistory = historyDB.getLastHistoryByUserAndQuizId(friend.getId(),quiz.getId());
            if(frHistory != null){
                friendNames.add(friend.getUsername());
                friendHistories.add(historyDB.getLastHistoryByUserAndQuizId(friend.getId(),quiz.getId()));
            }
        }
    }catch (SQLException e){
//        System.out.println("SQL ex");
    }catch (ClassNotFoundException e){
//        System.out.println("Class not found ex");
    }
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

    <h2><%=lastHistory.getGrade()%> / <%=totalScore%></h2>
    <h2><%=(double) lastHistory.getWritingTime() / 1000%> Seconds</h2>

    <br>
    <br>
    <strong><a href="/">Return</a></strong>

    <br><br><br>
    <h2>Previous Attempts</h2>
    <hr>
    <div class="scroll-container">
        <%
            for(int i = 0; i < prevAttempts.size(); i++){
        %>
        <div class="box">
            <h3><%=prevAttempts.get(i).getGrade()%> / <%=totalScore%></h3>
            <h3><%=(double) prevAttempts.get(i).getWritingTime() / 1000%> Seconds</h3>
            <h4><%=prevAttempts.get(i).getCompletedAt()%></h4>
        </div>
        <%
            }
        %>
    </div>

    <br>
    <h2>Top Score</h2>
    <hr>
    <div class="box">
        <h2><%=bestHistoryName%></h2>
        <h3><%=bestHistory.getGrade()%> / <%=totalScore%></h3>
        <h3><%=(double) bestHistory.getWritingTime() / 1000%> Seconds</h3>
        <h4><%=bestHistory.getCompletedAt()%></h4>
    </div>

    <br>
    <h2>Friends</h2>
    <hr>
    <div class="scroll-container">
        <%
            for(int i = 0; i < friendHistories.size(); i++){
        %>
        <div class="box">
            <h2><%=friendNames.get(i)%></h2>
            <h3><%=friendHistories.get(i).getGrade()%> / <%=totalScore%></h3>
            <h3><%=friendHistories.get(i).getWritingTime()%> Seconds</h3>
            <h4><%=friendHistories.get(i).getCompletedAt()%></h4>
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
