<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiz Website</title>

</head>
<body>
<header>
    <input type="text" placeholder="Search for quizzes...">
    <a href="#" class="category">General Knowledge</a>
    <a href="#" class="category">Science</a>
    <a href="#" class="category">History</a>
    <a href="#" class="category">Geography</a>
    <a href="#" class="category">Sports</a>
    <a href="#" class="category">Entertainment</a>
    <a class="category" onclick="">Start Random Quiz</a>
</header>
<div class="content">
    <%
//        ArrayList<Quiz> quizzes = application.getAttribute("recent_quizzes");
        ArrayList<String> quizzes = new ArrayList<String>();
        quizzes.add("quiz1");
        quizzes.add("quiz2");
        quizzes.add("quiz3");
        quizzes.add("quiz4");
    %>
</div>
<script>
    function startRandomQuiz() {
        alert('Starting a random quiz!');
    }
</script>
</body>
</html>
