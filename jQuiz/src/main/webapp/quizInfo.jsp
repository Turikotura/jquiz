<%@ page import="models.Quiz" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="database.Database" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="models.User" %>
<%@ page import="java.util.List" %><%--
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
    Quiz curQuiz = null;
    User author = null;
    try {
        curQuiz = quizDB.getQuizById(quizId);
        author = userDB.getById(curQuiz.getAuthorId());
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
    <link href="index.css" rel="stylesheet" type="text/css">
</head>
<body>
    <h1><%=curQuiz.getTitle()%></h1>
    <h3><%="Author: " + author.getUsername()%></h3>
    <h3><%="Created at: " + curQuiz.getCreatedAt().toString()%></h3>
    <h3><%="Time Limit: " + curQuiz.getMaxTime() + " minutes"%></h3>
    <p><%=curQuiz.getDescription()%></p>
    <% if(curQuiz.getAllowPractice()) { %>
        <a href = "playQuiz.jsp?practice=true&quizId=<%=curQuiz.getId()%>"><button type="button">Practice</button></a>
    <% } %>
    <a href = "playQuiz.jsp?practice=false&quizId=<%=curQuiz.getId()%>"><button type="button">Start Quiz</button></a>

    <div class="quiz-list-wrapper">
        <h2>Quizzes by the same author:</h2>
        <div class="quiz-boxes">
            <% try {
                    List<Quiz> quizzesBySameAuthor = quizDB.getQuizzesByAuthorId(author.getId());
                    if (quizzesBySameAuthor == null) {
                        throw new Exception("Quizzes not found in request.");
                    }

                    for (Quiz quiz : quizzesBySameAuthor) {
                        User curAuthor = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= curAuthor.getUsername()%></p>
                    </div>
                </a>
            </div>
            <% }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<p>Error loading quizzes: " + e.getMessage() + "</p>");
            } %>
        </div>
    </div>
</body>
</html>
