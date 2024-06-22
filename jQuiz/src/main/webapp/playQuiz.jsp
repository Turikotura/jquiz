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
<%
    if(quizAttempt != null){
%>
<head>
    <title><%=quizAttempt.getTitle()%></title>
    <link href="index.css" rel="stylesheet" type="text/css">
    <link href="style/playQuiz.css" rel="stylesheet" type="text/css">
</head>
<body>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="">Home</a></li>
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

<main>
    <img src="<%=quizAttempt.getThumbnail()%>">
    <h1><%=quizAttempt.getTitle()%></h1>
    <h3><%=quizAttempt.getStartTime()%></h3>
    <%
        int first = quizAttempt.getTime()/3600;
        int second = (quizAttempt.getTime()%3600)/60;
        int third = quizAttempt.getTime()%60;

        String firstStr = String.format("%" + 2 + "s",Integer.toString(first)).replace(' ','0');
        String secondStr = String.format("%" + 2 + "s",Integer.toString(second)).replace(' ','0');
        String thirdStr = String.format("%" + 2 + "s",Integer.toString(third)).replace(' ','0');
    %>
    <h3><%=firstStr%>:<%=secondStr%>:<%=thirdStr%></h3>
    <h4>Max Score : <%=quizAttempt.getMaxScore()%> pts</h4>
    <hr>

    <div id="question-list">
        <%
            for(int i = 0; i < quizAttempt.getQuestions().size(); i++){
        %>
            <button onclick="scrollToDiv(<%=i%>)"><%=i%></button>
        <%
            }
        %>
    </div>

    <form action="PlayQuiz" method="post">
        <input name="userId" type="hidden" value="<%=userId%>">
        <input name="quizAttemptId" type="hidden" value="<%=attemptId%>">
    <%
        for(int i = 0; i < quizAttempt.getQuestions().size(); i++){
            QuestionAttempt questionAttempt = quizAttempt.getQuestions().get(i);
            String active = "";
            if(quizAttempt.getShowAll() || i == quizAttempt.getOnQuestionIndex()){
                active = "active";
            }
    %>

        <div id="<%=i%>" class="question-box <%=active%>">

            <%
                if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.PIC_RESPONSE){
            %>
            <img src="<%=questionAttempt.getQuestion().getImage()%>">
            <%
                }
            %>
            <p style="float: left;"><%=questionAttempt.getQuestion().getText()%></p>
            <p style="float: right"><%=questionAttempt.getMaxScore()%> pts</p>
            <br>
            <br>
            <hr>
            <br>


            <%
                if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTIPLE_CHOICE){
                    for(int j = 0; j < questionAttempt.getAnswers().size(); j++){
            %>
            <input name="<%=i%>" id="<%=i%>-<%=j%>" type="radio" value="<%=questionAttempt.getAnswers().get(j).getText()%>">
            <label for="<%=i%>-<%=j%>"> <%=questionAttempt.getAnswers().get(j).getText()%></label><br>
            <%
                    }
                }
                else if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTI_ANS_MULTI_CHOICE){
                    for(int j = 0; j < questionAttempt.getAnswers().size(); j++){
            %>
            <input name="<%=i%>" id="<%=i%>-<%=j%>" type="checkbox" value="<%=questionAttempt.getAnswers().get(j).getText()%>">
            <label for="<%=i%>-<%=j%>"> <%=questionAttempt.getAnswers().get(j).getText()%></label><br>
            <%
                    }
                }
                else{
                    for(int j = 0; j < questionAttempt.getCorrectAnswersAmount(); j++){
            %>
            <input name="<%=i%>-<%=j%>" type="text">
            <%
                    }
                }
            %>
        </div>
    <%
        }
    %>
        <br>
        <input id="quiz-submit-button" type="submit" value="Submit">
    </form>
</main>

<script>
    function scrollToDiv(sectionId) {
        <%
            if(!quizAttempt.getShowAll()) {
        %>
        // Hide all divs
        var divs = document.querySelectorAll('.question-box');
        divs.forEach(function(div) {
            div.classList.remove('active');
        });

        // Show the selected div
        var selectedDiv = document.getElementById(sectionId);
        if (selectedDiv) {
            selectedDiv.classList.add('active');
        }
        <%
            }
        %>

        var element = document.getElementById(sectionId);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth' });
        }
    }
</script>
<%
    }else{
%>
<p>no quiz attempt</p>
<%
    }
%>
</body>
</html>