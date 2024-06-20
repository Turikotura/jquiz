<%@ page import="java.sql.SQLException" %>
<%@ page import="models.Quiz" %>
<%@ page import="models.Question" %>
<%@ page import="models.QuestionTypes" %>
<%@ page import="java.util.List" %>
<%@ page import="models.Answer" %>
<%@ page import="attempts.QuizAttempt" %>
<%@ page import="attempts.QuizAttemptsController" %>
<%@ page import="static listeners.SessionListener.getQuizAttemptsController" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="attempts.QuestionAttempt" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/18/24
  Time: 10:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    int attemptId = Integer.parseInt(request.getParameter("attemptId"));

    UserDatabase userdb = getDatabase(Database.USER_DB,request);
    int userId = -1;
    try {
        userId = userdb.getByUsername((String) request.getSession().getAttribute("curUser")).getId();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }

    QuizAttemptsController qac = getQuizAttemptsController(userId,request);
    QuizAttempt quizAttempt = qac.getQuizAttemptById(attemptId);
%>
<head>
    <title><%=quizAttempt.getTitle()%></title>
    <link href="index.css" rel="stylesheet" type="text/css">
</head>
<body>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="/index.jsp">Home</a></li>
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

<p><%=attemptId%></p>
<h><%=quizAttempt.getTitle()%></h>

<form action="PlayQuiz" method="post">
    <input name="userId" type="hidden" value="<%=userId%>">
    <input name="quizAttemptId" type="hidden" value="<%=attemptId%>">
<%
    for(int i = 0; i < quizAttempt.getQuestions().size(); i++){
        QuestionAttempt questionAttempt = quizAttempt.getQuestions().get(i);
%>



    <p><%=questionAttempt.getQuestion().getId()%></p>
    <p><%=questionAttempt.getQuestion().getText()%></p>

    <%
        for(int j = 0; j < questionAttempt.getCorrectAnswersAmount(); j++){
    %>
        <input name="<%=i%>-<%=j%>" type="text">
    <%
        }
    %>
<%
    }
%>
    <br>
    <input type="submit" value="Submit">
</form>

</body>
</html>