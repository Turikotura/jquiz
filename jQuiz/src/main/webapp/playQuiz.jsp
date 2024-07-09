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

    // Mail variables
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();

    if(curUser != null){
        mails = (List<Mail>) request.getAttribute("mails");
        senderNames = (List<String>) request.getAttribute("senderNames");
        maxGrades = (Map<Integer, Integer>) request.getAttribute("maxGrades");
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
    <img id="quiz-thumbnail" src="image?type=quiz&quizId=<%=quizAttempt.getQuizId()%>">
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
        <p id="q-res-<%=i%>"><%=i%> : <%=toWrite%></p>
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
                <p>&#8470; <%=i%></p>
                <%
                    if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.PIC_RESPONSE){
                %>
                <img src="image?type=question&questionId=<%=questionAttempt.getQuestion().getId()%>"><br><br>
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
                <%--Question point--%>
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
                <input class="answer-inp mc-inp" <%=disabled%> <%=checked%> name="<%=i%>" id="<%=i%>-<%=j%>" type="radio" value="<%=questionAttempt.getAnswers().get(j).getText()%>">
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
                <input class="answer-inp mamc-inp" <%=disabled%> <%=checked%> name="<%=i%>" id="<%=i%>-<%=j%>" type="checkbox" value="<%=questionAttempt.getAnswers().get(j).getText()%>">
                <label for="<%=i%>-<%=j%>"> <%=questionAttempt.getAnswers().get(j).getText()%></label><br>
                <%
                        }
                    }
                    else if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.FILL_BLANK){
                        String text = " " + questionAttempt.getQuestion().getText() + " ";
                        String[] split = text.split("\\{}");
                %>
                <%--Fill in the blank question--%>
                <p>
                    <%
                        for(int j = 0; j < split.length-1; j++){
                            String val = "";
                            if(!questionAttempt.getWrittenAnswers().isEmpty()){
                                val = questionAttempt.getWrittenAnswers().get(j);
                            }
                    %>
                    <%=split[j]%>
                    <input class="answer-inp" <%=disabled%> name="<%=i%>-<%=j%>" type="text" value="<%=val%>">
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
                <input class="answer-inp" <%=disabled%> name="<%=i%>-<%=j%>" type="text" value="<%=val%>"><br>
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
        // Update question result text
        $('#q-res-'+resp.qInd).text(resp.qInd + ' : ' +  resp.grade + ' / ' + resp.maxGrade);

        // Disable inputs
        var form = $('#'+resp.qInd).find('form');
        if (form.length > 0) {
            form.find('input').prop('disabled',true);
        }
    }

    // Scroll to question
    function scrollToDiv(sectionId) {
        var element = $('#'+sectionId);
        if (element.length) {
            $('html, body').animate({
                scrollTop: element.offset().top-100
            }, 800);
        }
    }

    // Move to next question on single question per page
    function moveToNext(sectionId){
        if(sectionId == <%=quizAttempt.getQuestions().size()%>){
            // At the end of the quiz, finish quiz
            submitQuiz();
            return;
        }

        // Hide every question
        $('.question-box').removeClass('active');

        // Show only one question
        $('#'+Number(sectionId)).addClass('active');
    }

    // Finish quiz
    function submitQuiz(){
        $('.send-question').each(function () {
           $(this).click();
        });

        $('#finish-form').submit();
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
        moveToNext(resp.onQuestion);
    }



    // --- AJAX calls ---
    $(document).ready(function() {
        // Send answer for question
        $('.send-question').click(function(e) {
            e.preventDefault();

            var form = $(this).closest('form');
            var formData = form.serializeArray();

            <%
            if(!quizAttempt.getShowAll() && !quizAttempt.getIsPractice()){
            %>
            // Display next question after answering
            moveToNext(Number(this.parentNode.parentNode.id)+1);
            <%
            }
            %>

            <%
            if(quizAttempt.getIsPractice()){
            %>
            // Clear input answers when practice question is answered
            form.find('input, textarea, select').each(function() {
                var elementType = $(this).attr('type');

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
                }
            });
            <%
            }
            %>

            <%
            if(quizAttempt.getAutoCorrect() || quizAttempt.getIsPractice()){
            %>
            // If question submits need to be auto-corrected
            formData.push({ name: 'eval', value: 'true' });
            <%
            }
            %>

            <%
            if(!quizAttempt.getShowAll()){
            %>
            // If quiz displays one question at a time
            formData.push({ name: 'nextQ', value: 'true'});
            <%
            }
            %>

            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: formData,
                success: function(response) {
                    console.log('Question successfully submitted');
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
                    console.log('Question did not submit');
                    console.log(error);
                    alert('question did not submit');
                }
            });
        });

        // Logic to save answers automatically
        $('.question-box').on('input', function(e) {
            e.preventDefault();

            var formElem = $(this).find('form');
            var form = $(formElem).closest('form');
            var formData = form.serializeArray();

            $.ajax({
                url: form.attr('action'),
                type: form.attr('method'),
                data: formData,
                success: function(response) {
                    console.log('Answer saved');
                },
                error: function(xhr, status, error) {
                    console.log('Could not save answer');
                    console.log(error);
                    alert('answer could not save');
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