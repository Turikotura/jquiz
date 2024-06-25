<%@ page import="models.History" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.Quiz" %>
<%@ page import="models.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="database.*" %>
<%@ page import="models.User" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 22.06.2024
  Time: 14:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    HistoryDatabase historydb = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,request);
    QuestionDatabase questiondb = getDatabase(Database.QUESTION_DB,request);
    UserDatabase userdb = getDatabase(Database.USER_DB,request);

    int userId = Integer.parseInt(request.getParameter("userId"));
    int quizId = Integer.parseInt(request.getParameter("quizId"));

    History lastHistory = null;
    Quiz quiz = null;
    User user = null;
    List<Question> questionList = new ArrayList<Question>();
    int totalScore = 0;

    String bestHistoryName = "";
    History bestHistory = null;
    List<History> prevAttempts = new ArrayList<History>();
    List<String> friendNames = new ArrayList<String>();
    List<History> friendHistories = new ArrayList<History>();
    try {
        lastHistory = historydb.getLastHistoryByUserAndQuizId(userId, quizId);
        quiz = quizdb.getById(lastHistory.getQuizId());
        questionList = questiondb.getQuestionsByQuizId(quiz.getId());
        for (Question question : questionList) {
            totalScore += question.getScore();
        }

        // others results

        bestHistory = historydb.getBestScoreHistoryByQuizId(quizId);
        bestHistoryName = userdb.getById(bestHistory.getId()).getUsername();
        prevAttempts = historydb.getHistoryByUserAndQuizId(userId, quizId);
        List<User> friends = new ArrayList<User>();
        friends = userdb.getFriendsByUserId(userId);
        for(User friend : friends){
            friendNames.add(friend.getUsername());
            friendHistories.add(historydb.getLastHistoryByUserAndQuizId(friend.getId(),quiz.getId()));
        }
    }catch (SQLException e){
        System.out.println("SQL ex");
    }catch (ClassNotFoundException e){
        System.out.println("Class not found ex");
    }
%>

<html>
<head>
    <title>Title</title>

    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/quizResult.css" rel="stylesheet" type="text/css">
</head>
<body>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
        </ul>
    </nav>
    <nav class="auth-nav">
        <%if(request.getSession().getAttribute("curUser") == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <%} else { %>
        <ul>
            <li><a href="#"><%=(String)request.getSession().getAttribute("curUser")%></a></li>
            <li><form action="Login" method="get">
                <input type="submit" value="Log out">
            </form></li>
        </ul>
        <%}%>

    </nav>
</header>

<main>

    <h2><%=lastHistory.getGrade()%> / <%=totalScore%></h2>
    <h2><%=lastHistory.getWritingTime()%> Seconds</h2>

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
            <h3><%=prevAttempts.get(i).getWritingTime()%> Seconds</h3>
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
        <h3><%=bestHistory.getWritingTime()%> Seconds</h3>
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

</body>
</html>
