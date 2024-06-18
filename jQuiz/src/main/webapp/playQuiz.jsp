<%@ page import="java.sql.SQLException" %>
<%@ page import="models.Quiz" %>
<%@ page import="database.Database" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="models.Question" %>
<%@ page import="java.util.List" %>
<%@ page import="database.QuestionDatabase" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/18/24
  Time: 10:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<% int quizId = Integer.parseInt(request.getParameter("quizId"));
    QuizDatabase quizDB = (QuizDatabase) application.getAttribute(Database.QUIZ_DB);
    QuestionDatabase questionDB = (QuestionDatabase) application.getAttribute(Database.QUESTION_DB);
    Quiz curQuiz = null;
    List<Question> quizQuestions = null;
    try {
        curQuiz = quizDB.getQuizById(quizId);
        quizQuestions = questionDB.getQuestionsByQuizId(quizId);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
</head>
<body>
    <h1><%=curQuiz.getTitle()%></h1>

</body>
</html>
