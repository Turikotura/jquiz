<%@ page import="models.Quiz" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quiz List</title>
</head>
<body>
<h1>Quiz List</h1>
<div>
    <ul>
        <%
            List<Quiz> quizzes = (List<Quiz>) request.getAttribute("quizzes");
            if (quizzes != null) {
                for (Quiz quiz : quizzes) {
        %>
        <li>
            <strong>Title:</strong> <%= quiz.getTitle() %><br>
            <strong>Author:</strong> <%= quiz.getAuthor().getUsername() %><br>
            <strong>Created At:</strong> <%= quiz.getCreatedAt() %><br>
            <strong>Description:</strong> <%= quiz.getDescription() %><br>
        </li>
        <%
            }
        } else {
        %>
        <li>No quizzes available.</li>
        <%
            }
        %>
    </ul>
</div>
</body>
</html>
