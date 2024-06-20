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
<%@ page import="static listeners.ContextListener.getDatabase" %><%--
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
</head>
<body>
<p><%=attemptId%></p>
<p></p>

</body>
</html>
