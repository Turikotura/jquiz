<%@ page import="attempts.QuizAttempt" %>
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

    QuizAttempt quizAttempt = (QuizAttempt) request.getAttribute("qa");
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

    <%--Quiz info--%>
    <img src="<%=quizAttempt.getThumbnail()%>">
    <h1><%=quizAttempt.getTitle()%></h1>
    <h3><%=quizAttempt.getStartTime()%></h3>
    <h3 id="timer"></h3>
    <h4>Max Score : <%=quizAttempt.getMaxScore()%> pts</h4>
    <hr>

    <%--Question jump panel--%>
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

    <%--Question results--%>
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

    <%--Question list--%>
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
            <%--Base question inputs--%>
            <input name="userId" type="hidden" value="<%=curUser.getId()%>">
            <input name="quizAttemptId" type="hidden" value="<%=attemptId%>">
            <input name="questionInd" type="hidden" value="<%=i%>">

            <%--Question info--%>
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
            <%--Multi choice question--%>
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
            <%--MAMC question--%>
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
            <%--Response question--%>
            <input <%=disabled%> name="<%=i%>-<%=j%>" type="text" value="<%=val%>">
            <%
                    }
                }
            %>

            <%
                if(!quizAttempt.getShowAll() || quizAttempt.getAutoCorrect()) {
            %>
            <%--Question submit button--%>
            <input <%=disabled%> type="submit" class="send-question" value="Submit">
            <%
                }
            %>
        </form>
    </div>
    <%
        }
    %>
    <br>

    <%--Finish Quiz--%>
    <form id="finish-form" action="PlayQuiz" method="post">
        <input name="userId" type="hidden" value="<%=curUser.getId()%>">
        <input name="quizAttemptId" type="hidden" value="<%=attemptId%>">
        <input name="finishQuiz" type="hidden" value="true">
        <input id="quiz-submit-button" type="submit" value="Finish">
    </form>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script>
    // Update the grade for a single question
    function updateSingleQuestionResult(resp){
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
    }

    // Scroll to question
    function scrollToDiv(sectionId) {
        var element = document.getElementById(sectionId);
        if (element) {
            element.scrollIntoView({ behavior: 'smooth' });
        }
    }

    // Move to next question on single question per page
    function moveToNext(sectionId){
        sectionId++;
        console.log(sectionId);
        if(sectionId == <%=quizAttempt.getQuestions().size()%>){
            submitQuiz();
            return;
        }

        var divs = document.querySelectorAll('.question-box');
        divs.forEach(function(div) {
            div.classList.remove('active');
        });

        var selectedDiv = document.getElementById(sectionId);
        if (selectedDiv) {
            selectedDiv.classList.add('active');
        }
    }

    // Finish quiz
    function submitQuiz(){
        // Submit every question answer
        var questionSubmits = document.querySelectorAll('.send-question');
        questionSubmits.forEach(function (qs) {
            qs.click();
        })

        // And then finish the quiz
        var form = document.getElementById("finish-form");
        form.submit();
    }



    // --- AJAX calls ---
    $(document).ready(function() {
        // Logic for question submit buttons
        $('.send-question').click(function(e) {

            if(<%=!quizAttempt.getShowAll()%>){
                moveToNext(this.parentNode.parentNode.id);
            }

            e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            // If question submits need to be auto-corrected
            <%
            if(quizAttempt.getAutoCorrect()){
            %>
            formData.push({ name: 'eval', value: 'true' });
            <%
            }
            %>

            // If quiz displays one question at a time
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
                    // Update the grade panel
                    <%
                    if(quizAttempt.getAutoCorrect()) {
                    %>
                    updateSingleQuestionResult(response);
                    <%
                    }
                    %>
                },
                error: function(xhr, status, error) {
                    console.error('Ajax request failed');
                    console.error(error);
                }
            });
        });

        // Logic to save answers automatically
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



    // --- Timer ---
    // Timer logic
    function timerLogic(timer,display){
        // Display remaining time
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.textContent = minutes + ":" + seconds;

        // Submit quiz if time is up
        if (--timer < 0) {
            timer = 0;
            // Submit the form when timer reaches 0
            submitQuiz();
        }
    }

    // Start the timer
    function startTimer(duration, display) {
        var timer = duration, minutes, seconds;
        timerLogic(timer--,display);
        setInterval(function () {
            timerLogic(timer--,display);
        }, 1000);
    }

    // Get timer element
    var display = document.getElementById("timer");

    <%
    long timeGone = ((new Date()).getTime()-quizAttempt.getStartTime().getTime())/1000;
    long timeLeft = quizAttempt.getTime()-timeGone;
    %>
    // Get time left
    var timerDuration = <%=timeLeft%>;

    // Start timer when page loads
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