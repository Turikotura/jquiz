<%@ page import="models.History" %>
<%@ page import="database.HistoryDatabase" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="database.Database" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="models.Quiz" %>
<%@ page import="models.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="database.QuestionDatabase" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 22.06.2024
  Time: 14:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    HistoryDatabase historydb = getDatabase(Database.HISTORY_DB,request);
    int userId = Integer.parseInt(request.getParameter("userId"));
    int quizId = Integer.parseInt(request.getParameter("quizId"));
    History lastHistory = null;
    try {
        lastHistory = historydb.getLastHistoryByUserAndQuizId(userId,quizId);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }

    QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,request);
    Quiz quiz = null;
    try {
        quiz = quizdb.getQuizById(lastHistory.getQuizId());
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }

    QuestionDatabase questiondb = getDatabase(Database.QUESTION_DB,request);
    List<Question> questionList = new ArrayList<Question>();
    try {
        questionList = questiondb.getQuestionsByQuizId(quiz.getId());
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }

    int totalScore = 0;
    for(Question question : questionList){
        totalScore += question.getScore();
    }
%>
<head>
    <title>Title</title>

    <link href="index.css" rel="stylesheet" type="text/css">
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
    <h3><%=lastHistory.getWritingTime()%> seconds</h3>

    <br>
    <br>
    <strong><a href="/">return</a></strong>

</main>

</body>
</html>
