<%@ page import="models.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            <li><a href="#">Users</a></li>
            <li><a href="#">Achievements</a></li>
            <li><a href="#">Categories</a></li>
        </ul>
    </nav>
    <nav class="auth-nav">
        <ul>
            <li><a href="#">Login</a></li>
            <li><a href="#">Register</a></li>
        </ul>
    </nav>
</header>

<h1>Quiz List</h1>
<div class="quiz-boxes">
    <%
        List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
        if (quizzes != null) {
            for (Quiz quiz : quizzes) {
    %>
    <div class="quiz-box">
        <a href="#">
            <div class="quiz-box-top">
                <p class="quiz-box-name"><%=quiz.getTitle()%></p>
            </div>
            <div class="quiz-box-bot">
                <p><%=quiz.getAuthor().getUsername()%></p>
            </div>
        </a>
    </div>
    <%
        }
    } else {
    %>
    <li>No quizzes available.</li>
    <%
        }
    %>
</div>
</div>
</body>
</html>
