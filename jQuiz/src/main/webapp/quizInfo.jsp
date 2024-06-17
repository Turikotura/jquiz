<%@ page import="models.Quiz" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="database.Database" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="models.User" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/17/24
  Time: 9:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<% int quizId = Integer.parseInt(request.getParameter("quizId"));
    QuizDatabase quizDB = (QuizDatabase) application.getAttribute(Database.QUIZ_DB);
    UserDatabase userDB = (UserDatabase) application.getAttribute(Database.USER_DB);
    Quiz curQuiz = quizDB.getQuizById(quizId);
    User author = userDB.getById(curQuiz.getAuthorId());
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
</head>
<body>
    <h1><%=curQuiz.getTitle()%></h1>
    <h3><%="Created by: " + author.getUsername()%></h3>
    <h3><%="Created at: " + curQuiz.getCreatedAt().toString()%></h3>
    <h3><%="Time Limit: " + curQuiz.getMaxTime() + " minutes"%></h3>
    <p><%=curQuiz.getDescription()%></p>
    <button type="button">Start Quiz</button>
</body>
</html>
