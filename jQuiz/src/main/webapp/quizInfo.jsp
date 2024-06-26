<%@ page import="models.Quiz" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="database.Database" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="models.User" %>
<%@ page import="java.util.List" %>
<%@ page import="models.History" %>
<%@ page import="database.HistoryDatabase" %><%--
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
    HistoryDatabase hisroryDB = (HistoryDatabase) application.getAttribute(Database.HISTORY_DB);
    Quiz curQuiz = null;
    User author = null;
    try {
        curQuiz = quizDB.getById(quizId);
        author = userDB.getById(curQuiz.getAuthorId());
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="main">
    <header>
        <div class="logo">
            <img src="logo.png" alt="Website Logo">
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="">Home</a></li>
                <li><a href="#">Users</a></li>
                <li><a href="#">Achievements</a></li>
                <li><a href="#">Categories</a></li>
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
                <li><a href="#"><%=((User)request.getSession().getAttribute("curUser")).getUsername()%></a></li>
                <li><form action="Login" method="get">
                    <input type="submit" value="Log out">
                </form></li>
            </ul>
            <%}%>

        </nav>
    </header>
    <h1><%=curQuiz.getTitle()%></h1>
    <h3><%="Author: " + author.getUsername()%></h3>
    <h3><%="Created at: " + curQuiz.getCreatedAt().toString()%></h3>
    <h3><%="Time Limit: " + curQuiz.getMaxTime() + " minutes"%></h3>
    <p><%=curQuiz.getDescription()%></p>
    <% if(curQuiz.getAllowPractice()) { %>
    <form action="QuizInfo" method="post">
        <input name="practice" type="hidden" value="true">
        <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
        <input type="submit" value="Practice">
    </form>
    <% } %>
    <form action="QuizInfo" method="post">
        <input name="practice" type="hidden" value="false">
        <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
        <input type="submit" value="Start Quiz">
    </form>

    <h3>Hall of fame</h3>
    <% try {
        List<History> topOfAllTime = hisroryDB.getTopPerformersByQuizId(curQuiz.getId(),10);
        if(topOfAllTime.isEmpty()) { %>
            <p>No one has taken this quiz yet. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : topOfAllTime) {
                    User topScorer = userDB.getById(curTry.getUserId());%>
                <li><a href="#"><%=topScorer.getUsername() %></a> <%=": " + curTry.getGrade() + " in " + curTry.getWritingTime() + "min"%></li>
                <% } %>
            </ol>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>


    <h3>Top performers of the last day</h3>
    <% try {
        List<History> topOfAllTime = hisroryDB.getTopInLastDayByQuizId(curQuiz.getId(),10);
        if(topOfAllTime.isEmpty()) { %>
            <p>No one has taken this quiz int the last day. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : topOfAllTime) {
                    User topScorer = userDB.getById(curTry.getUserId());%>
                <li><a href="#"><%=topScorer.getUsername() %></a> <%=": " + curTry.getGrade() + " in " + curTry.getWritingTime() + "min"%></li>
                <% } %>
            </ol>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>

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
                        if(quiz.getId() == curQuiz.getId()) continue;
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
</div>
</body>
</html>
