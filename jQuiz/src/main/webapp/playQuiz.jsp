<%@ page import="attempts.QuizAttempt" %>
<%@ page import="attempts.QuestionAttempt" %>
<%@ page import="models.*" %>
<%@ page import="database.MailDatabase" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="database.Database" %>
<%@ page import="database.HistoryDatabase" %>
<%@ page import="java.util.*" %><%--
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
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>

    <%--Quiz info--%>
    <img src="image?type=quiz&quizId=<%=quizAttempt.getQuizId()%>">
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

        <%--Practice result--%>
        <%
            if(quizAttempt.getIsPractice()){
        %>
        <div id="practice-result-panel">
            <h3 id="practice-result-text">No Previous question</h3>
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
            <img src="image?type=question&questionId=<%=questionAttempt.getQuestion().getId()%>">
            <%
                }
            %>

                <%
                    if(questionAttempt.getQuestion().getQuestionType() != QuestionTypes.FILL_BLANK){
                %>
            <p style="float: left;"><%=questionAttempt.getQuestion().getText()%></p>
                <%
                    }
                %>
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
                else if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.FILL_BLANK){
                    String text = " " + questionAttempt.getQuestion().getText() + " ";
                    String[] split = text.split("\\{}");
            %>
                <p>
                    <%
                        for(int j = 0; j < split.length-1; j++){
                    %>
                    <%=split[j]%>
                    <input <%=disabled%> name="<%=i%>-<%=j%>" type="text">
                    <%
                        }
                    %>
                    <%=split[split.length-1]%>
                </p>
                <%
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
<script src="script/general.js"></script>
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

    function usePracticeResponse(resp){
        if(resp.correct == '1'){
            $("#practice-result-text").text('Correct');
            $("#practice-result-panel").removeClass('incorrect');
            $("#practice-result-panel").addClass('correct');
        }else{
            $("#practice-result-text").text('Incorrect');
            $("#practice-result-panel").removeClass('correct');
            $("#practice-result-panel").addClass('incorrect');
        }
        moveToNext(resp.onQuestion-1);
    }



    // --- AJAX calls ---
    $(document).ready(function() {
        // Logic for question submit buttons
        $('.send-question').click(function(e) {

            <%
            if(!quizAttempt.getShowAll() && !quizAttempt.getIsPractice()){
            %>
            moveToNext(this.parentNode.parentNode.id);
            <%
            }
            %>

            e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            <%
            if(quizAttempt.getIsPractice()){
            %>
            // Find all input, textarea, and select elements within the form and clear their values
            form.find('input, textarea, select').each(function() {
                var elementType = $(this).attr('type'); // Get the type attribute of the element

                // Clear the value based on element type
                switch (elementType) {
                    case 'text':
                    case 'password':
                    case 'textarea':
                    case 'email':
                    case 'number':
                        $(this).val('');
                        break;
                    case 'checkbox':
                    case 'radio':
                        $(this).prop('checked', false);
                        break;
                    case 'select-one':
                        $(this).prop('selectedIndex', 0);
                        break;
                    // Add more cases as needed for different types of inputs
                }
            });
            <%
            }
            %>

            // If question submits need to be auto-corrected
            <%
            if(quizAttempt.getAutoCorrect() || quizAttempt.getIsPractice()){
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
                    <%
                    if(quizAttempt.getIsPractice()){
                    %>
                    usePracticeResponse(response);
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
    <%
    if(!quizAttempt.getIsPractice()){
    %>
    startTimer(timerDuration, display);
    <%
    }
    %>
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