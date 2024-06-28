<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.List" %>
<%@ page import="attempts.QuizAttempt" %>
<%@ page import="attempts.QuizAttemptsController" %>
<%@ page import="static listeners.SessionListener.getQuizAttemptsController" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="attempts.QuestionAttempt" %>
<%@ page import="models.*" %>
<%@ page import="java.util.Date" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/18/24
  Time: 10:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    int attemptId = Integer.parseInt(request.getParameter("attemptId"));

    UserDatabase userdb = getDatabase(Database.USER_DB,request);
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
            <li><a href="/">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
            <li><a href="/historySummary.jsp">History</a></li>
        </ul>
    </nav>
    <nav class="auth-nav">
        <%if(curUser == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <%} else { %>
        <ul>
            <li><a href="#"><%=curUser.getUsername()%></a></li>
            <li><a onclick="submitLogOut()">Log out</a></li>
            <form id="log-out-form" style="display: none" action="Login" method="get"></form>
        </ul>
        <%}%>

    </nav>
</header>

<main>
    <img src="<%=quizAttempt.getThumbnail()%>">
    <h1><%=quizAttempt.getTitle()%></h1>
    <h3><%=quizAttempt.getStartTime()%></h3>
    <h3 id="timer"></h3>
    <h4>Max Score : <%=quizAttempt.getMaxScore()%> pts</h4>
    <hr>

    <%
        if(quizAttempt.getShowAll()) {
    %>
    <div id="question-list">
        <%
            for(int i = 0; i < quizAttempt.getQuestions().size(); i++){
        %>
            <button onclick="scrollToDiv(<%=i%>)"><%=i%></button>
        <%
            }
        %>
    </div>
    <%
        }
    %>

    <%
        if(quizAttempt.getAutoCorrect()) {
    %>
    <div id="question-results">
        <%
            for(int i = 0; i < quizAttempt.getQuestions().size(); i++) {
                String toWrite = "Nan";
                if(quizAttempt.getQuestions().get(i).getWasGraded()){
                    toWrite = quizAttempt.getQuestions().get(i).evaluateAnswers() + " / " + quizAttempt.getQuestions().get(i).getMaxScore();
                }
        %>
        <p id="q-res-<%=i%>"><%=toWrite%></p>
        <%
            }
        %>
    </div>
    <%
        }
    %>

    <%
        for(int i = 0; i < quizAttempt.getQuestions().size(); i++){
            QuestionAttempt questionAttempt = quizAttempt.getQuestions().get(i);
            String active = "";
            if(quizAttempt.getShowAll() || i == quizAttempt.getOnQuestionIndex()){
                active = "active";
            }

            String disabled = "";
            if(quizAttempt.getQuestions().get(i).getWasGraded()){
                disabled = "disabled";
            }
    %>
    <div id="<%=i%>" class="question-box <%=active%>">
        <form action="PlayQuiz" method="post">
            <input name="userId" type="hidden" value="<%=userId%>">
            <input name="quizAttemptId" type="hidden" value="<%=attemptId%>">
            <input name="questionInd" type="hidden" value="<%=i%>">
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
                        String checked = "";
                        if(!questionAttempt.getWrittenAnswers().isEmpty() && questionAttempt.getAnswers().get(j).equals(questionAttempt.getWrittenAnswers().get(0))){
                            checked = "checked";
                        }
            %>
            <input <%=disabled%> <%=checked%> name="<%=i%>" id="<%=i%>-<%=j%>" type="radio" value="<%=questionAttempt.getAnswers().get(j).getText()%>">
            <label for="<%=i%>-<%=j%>"> <%=questionAttempt.getAnswers().get(j).getText()%></label><br>
            <%
                    }
                }
                else if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTI_ANS_MULTI_CHOICE){
                    for(int j = 0; j < questionAttempt.getAnswers().size(); j++){
                        String checked = "";
                        for(String wAns : questionAttempt.getWrittenAnswers()){
                            if(questionAttempt.getAnswers().get(j).equals(wAns)){
                                checked = "checked";
                            }
                        }
            %>
            <input <%=disabled%> <%=checked%> name="<%=i%>" id="<%=i%>-<%=j%>" type="checkbox" value="<%=questionAttempt.getAnswers().get(j).getText()%>">
            <label for="<%=i%>-<%=j%>"> <%=questionAttempt.getAnswers().get(j).getText()%></label><br>
            <%
                    }
                }
                else{
                    for(int j = 0; j < questionAttempt.getCorrectAnswersAmount(); j++){
                        String val = "";
                        if(!questionAttempt.getWrittenAnswers().isEmpty()){
                            val = questionAttempt.getWrittenAnswers().get(j);
                        }
            %>
            <input <%=disabled%> name="<%=i%>-<%=j%>" type="text" value="<%=val%>">
            <%
                    }
                }
            %>

            <%
                if(!quizAttempt.getShowAll() || quizAttempt.getAutoCorrect()) {
            %>
            <input <%=disabled%> type="submit" class="send-question" onclick="submitQuestion(<%=i+1%>)" value="Submit">
            <%
                }
            %>
        </form>
    </div>
    <%
        }
    %>
    <br>
    <form id="finish-form" action="PlayQuiz" method="post">
        <input name="userId" type="hidden" value="<%=userId%>">
        <input name="quizAttemptId" type="hidden" value="<%=attemptId%>">
        <input name="finishQuiz" type="hidden" value="true">
        <input id="quiz-submit-button" type="submit" value="Finish">
    </form>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script>
    function updateSingleQuestionResult(resp){
        <%
        if(quizAttempt.getAutoCorrect()) {
        %>
        var elem = document.getElementById("q-res-"+resp.qInd);
        elem.innerHTML = resp.grade + " / " + resp.maxGrade;

        var form = document.getElementById(resp.qInd).querySelector('form');
        if (form) {
            // Find the submit button with class 'send-question'
            var inps = form.querySelectorAll('input');
            for(var i = 0; i < inps.length; i++){
                inps[i].disabled = true;
            }
        }
        <%
        }
        %>
    }

    function scrollToDiv(sectionId) {
        var element = document.getElementById(sectionId);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth' });
        }
    }

    function submitQuestion(sectionId){
        if(sectionId == <%=quizAttempt.getQuestions().size()%> && <%=!quizAttempt.getShowAll()%>){
            submitQuiz();
            return;
        }

        if(<%=!quizAttempt.getShowAll()%>){
            var divs = document.querySelectorAll('.question-box');
            divs.forEach(function(div) {
                div.classList.remove('active');
            });

            var selectedDiv = document.getElementById(sectionId);
            if (selectedDiv) {
                selectedDiv.classList.add('active');
            }
        }
    }

    function submitQuiz(){
        var questionSubmits = document.querySelectorAll('.send-question');
        questionSubmits.forEach(function (qs) {
            qs.click();
        })

        var form = document.getElementById("finish-form");
        form.submit();
    }

    $(document).ready(function() {
        // Ajax request on clicking the div with class 'clickableDiv'
        $('.send-question').click(function(e) {
            e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();
            <%
            if(quizAttempt.getAutoCorrect()){
            %>
            formData.push({ name: 'eval', value: 'true' });
            <%
            }
            %>
            <%
            if(!quizAttempt.getShowAll()){
            %>
            formData.push({ name: 'nextQ', value: 'true'});
            <%
            }
            %>

            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: formData,
                success: function(response) {
                    console.log('Ajax request successful');
                    //console.log(response);
                    // Optionally, update UI or handle response
                    updateSingleQuestionResult(response);
                },
                error: function(xhr, status, error) {
                    console.error('Ajax request failed');
                    console.error(error);
                }
            });
        });

        $('.question-box').on('input', function(e) {
            e.preventDefault();

            var formElem = this.querySelector('form');
            var form = $(formElem).closest('form');
            var formData = form.serializeArray();

            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: formData,
                success: function(response) {
                    console.log('Ajax request successful');
                    //console.log(response);
                },
                error: function(xhr, status, error) {
                    console.error('Ajax request failed');
                    console.error(error);
                }
            });
        });
    });

    // Timer
    // Function to start the countdown timer
    function startTimer(duration, display) {
        var timer = duration, minutes, seconds;
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.textContent = minutes + ":" + seconds;
        if (--timer < 0) {
            timer = 0;
            // Submit the form when timer reaches 0
            submitQuiz();
        }
        setInterval(function () {
            minutes = parseInt(timer / 60, 10);
            seconds = parseInt(timer % 60, 10);

            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;

            display.textContent = minutes + ":" + seconds;
            if (--timer < 0) {
                timer = 0;
                // Submit the form when timer reaches 0
                submitQuiz();
            }
        }, 1000);
    }

    // Get the timer display element
    var display = document.getElementById("timer");

    <%
    long timeGone = ((new Date()).getTime()-quizAttempt.getStartTime().getTime())/1000;
    long timeLeft = quizAttempt.getTime()-timeGone;
    %>
    // Set the duration of the countdown in seconds
    var timerDuration = <%=timeLeft%>;

    // Start the timer when the page loads
    startTimer(timerDuration, display);
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