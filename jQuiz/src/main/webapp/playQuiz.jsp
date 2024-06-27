<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="attempts.QuizAttempt" %>
<%@ page import="attempts.QuizAttemptsController" %>
<%@ page import="static listeners.SessionListener.getQuizAttemptsController" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="attempts.QuestionAttempt" %>
<%@ page import="models.*" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/18/24
  Time: 10:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);

    // Mail variables
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();

    if(curUser != null){
        // Get mails received by user
        mails = mailDB.getMailsByUserId(curUser.getId(),"RECEIVE");
        for(Mail mail : mails){
            // Get names of senders
            senderNames.add(userDB.getById(mail.getSenderId()).getUsername());
            if(mail.getType() == MailTypes.CHALLENGE){
                // Get max grade of senders for challenges
                History history = historyDB.getBestHistoryByUserAndQuizId(mail.getSenderId(),mail.getQuizId());
                int grade = (history == null) ? 0 : history.getGrade();
                maxGrades.put(mail.getId(),grade);
            }
        }
    }

    int attemptId = Integer.parseInt(request.getParameter("attemptId"));

    int userId = -1;
    userId = ((User) request.getSession().getAttribute("curUser")).getId();

    QuizAttemptsController qac = getQuizAttemptsController(userId,request);
    QuizAttempt quizAttempt = qac.getQuizAttemptById(attemptId);
%>
<%
    if(quizAttempt != null){
%>

<html>
<head>
    <title>Play quiz</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/playQuiz.css" rel="stylesheet" type="text/css">
</head>
<body>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="index.jsp">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
        </ul>
    </nav>
    <nav class="mail-nav">
        <button onclick="togglePanel()">Show Messages</button>
    </nav>
    <nav class="auth-nav">
        <%if(request.getSession().getAttribute("curUser") == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <%} else {
            String loggedInAs = ((User)request.getSession().getAttribute("curUser")).getUsername();%>
        <ul>
            <li><a href="profile.jsp?username=<%=loggedInAs%>"><%=loggedInAs%></a></li>
            <li><form action="Login" method="get">
                <input type="submit" value="Log out">
            </form></li>
        </ul>
        <%}%>
    </nav>
</header>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

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

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>
</body>
</html>