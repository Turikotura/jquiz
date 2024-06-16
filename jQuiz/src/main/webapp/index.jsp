<%@ page import="models.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="models.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    System.out.println("Starting JSP processing...");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz List</title>
    <link href="index.css" rel="stylesheet" type="text/css">
</head>
<body>
<div class="main">
    <header>
        <div class="logo">
            <img src="logo.png" alt="Website Logo">
        </div>
        <nav class="main-nav">
            <ul>
                <li><a href="#">Home</a></li>
                <li><a href="#">Users</a></li>
                <li><a href="#">Achievements</a></li>
                <li><a href="#">Categories</a></li>
            </ul>
        </nav>
        <nav class="auth-nav">
            <ul>
                <li><a href="login.jsp">Login</a></li>
                <li><a href="#">Register</a></li>
            </ul>
        </nav>
    </header>

    <div class="quiz-list-wrapper">
        <h2>Recently added quizzes</h2>
        <div class="quiz-boxes">
            <%
                try {
                    UserDatabase userDB = (UserDatabase) application.getAttribute("users");
                    List<Quiz> recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
                    List<Quiz> popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
                    List<Quiz> lastMonthPopularQuizzes = (List<Quiz>) request.getAttribute("lastMonthPopularQuizzes");

                    if (userDB == null) {
                        throw new Exception("User database not found in application.");
                    }
                    if (recentQuizzes == null || popularQuizzes == null) {
                        throw new Exception("Quizzes not found in request.");
                    }

                    for (Quiz quiz : recentQuizzes) {
                        User author = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="#">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                    }
            %>

        </div>
    </div>
    <div class="quiz-list-wrapper">
        <h2>All-time popular quizzes</h2>
        <div class="quiz-boxes">
        <%
                    for (Quiz quiz : popularQuizzes) {
                        User author = userDB.getById(quiz.getAuthorId());
                %>
                <div class="quiz-box">
                    <a href="#">
                        <div class="quiz-box-top">
                            <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                        </div>
                        <div class="quiz-box-bot">
                            <p><%= quiz.getTotalPlayCount()%></p>
                            <p><%= author.getUsername()%></p>
                        </div>
                    </a>
                </div>
                <%
                        }
                %>
        </div>
    </div>
    <div class="quiz-list-wrapper">
        <h2>Popular quizzes in the last month</h2>
        <div class="quiz-boxes">
            <%
                for (Quiz quiz : lastMonthPopularQuizzes) {
                    User author = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="#">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                }
            %>
        </div>
    </div>

    <%
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<p>Error loading quizzes: " + e.getMessage() + "</p>");
        }
    %>
</div>
</body>
</html>
