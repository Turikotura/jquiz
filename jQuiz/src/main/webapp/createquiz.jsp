<%@ page import="models.*" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
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
%>

<html>
<head>
    <title>Create Quiz</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/createQuiz.css" rel="stylesheet" type="text/css">
    <script>
        let questionCount = 1;

        function addQuestion(){
            let questionType = document.getElementById("questionType").value;
            let questionForm = document.getElementById(questionType + "QuestionForm").innerHTML;

            let indexedQuestionForm = questionForm.replace(/name="/g, 'name="' + questionCount + '_');
            indexedQuestionForm = indexedQuestionForm.replace(/answer_container/g, questionCount + '_answer_container');
            indexedQuestionForm = indexedQuestionForm.replace(/addChoice\(\)/g, 'addChoice(' + questionCount + ')');
            indexedQuestionForm = indexedQuestionForm.replace(/addChoiceMulti\(\)/g, 'addChoiceMulti(' + questionCount + ')');
            indexedQuestionForm = indexedQuestionForm.replace(/deleteLastChoice\(\)/g, 'deleteLastChoice(' + questionCount + ')');
            indexedQuestionForm = indexedQuestionForm.replace(/deleteLastChoiceMulti\(\)/g, 'deleteLastChoiceMulti(' + questionCount + ')');

            indexedQuestionForm += `<button type="button" onclick="deleteQuestion(${questionCount})">Delete Question</button><br><br>`;

            let questionDiv = document.createElement('div');
            questionDiv.id = 'question_' + questionCount;
            questionDiv.innerHTML = indexedQuestionForm;

            document.getElementById("questionContainer").appendChild(questionDiv);

            questionCount++;
        }

        function addChoice(questionId){
            let containerId = questionId + '_answer_container';
            let container = document.getElementById(containerId);

            let choiceCount = container.getElementsByTagName('input').length / 2 + 1;

            let answer_box = document.createElement('div');
            answer_box.className = "answer_box";

            let newInput = document.createElement('input');
            newInput.type = 'text';
            newInput.name = questionId + '_answer_' + choiceCount;
            newInput.placeholder = 'Choice ' + choiceCount;
            answer_box.appendChild(newInput);

            let radioInput = document.createElement('input');
            radioInput.type = 'radio';
            radioInput.name = questionId + '_correct';
            radioInput.value = choiceCount;
            answer_box.appendChild(radioInput);

            container.appendChild(answer_box);
        }

        function addChoiceMulti(questionId){
            let containerId = questionId + '_answer_container';
            let container = document.getElementById(containerId);

            let choiceCount = container.getElementsByTagName('input').length / 2 + 1;

            let answer_box = document.createElement('div');
            answer_box.className = "answer_box";

            let newInput = document.createElement('input');
            newInput.type = 'text';
            newInput.name = questionId + '_answer_' + choiceCount;
            newInput.placeholder = 'Choice ' + choiceCount;
            answer_box.appendChild(newInput);

            let newCheckbox = document.createElement('input');
            newCheckbox.type = 'checkbox';
            newCheckbox.name = questionId + '_correct_' + choiceCount;
            answer_box.appendChild(newCheckbox);

            container.appendChild(answer_box);
        }

        function deleteQuestion(questionId){
            let questionDiv = document.getElementById('question_' + questionId);
            questionDiv.parentNode.removeChild(questionDiv);

            questionCount--;
            for(let i = questionId + 1; i <= questionCount; i++) {
                let currentDiv = document.getElementById('question_' + i);
                currentDiv.id = 'question_' + (i - 1);
                let newInnerHTML = currentDiv.innerHTML.replace(new RegExp(i + '_', 'g'), (i - 1) + '_');
                newInnerHTML = newInnerHTML.replace('deleteQuestion(' + i + ')', 'deleteQuestion(' + (i - 1) + ')');
                newInnerHTML = newInnerHTML.replace('addChoice(' + i + ')', 'addChoice(' + (i - 1) + ')');
                newInnerHTML = newInnerHTML.replace('addChoiceMulti(' + i + ')', 'addChoiceMulti(' + (i - 1) + ')');
                newInnerHTML = newInnerHTML.replace('deleteLastChoice(' + i + ')', 'deleteLastChoice(' + (i - 1) + ')');
                newInnerHTML = newInnerHTML.replace('deleteLastChoiceMulti(' + i + ')', 'deleteLastChoiceMulti(' + (i - 1) + ')');
                currentDiv.innerHTML = newInnerHTML;
            }
        }

        function deleteLastChoice(questionId){
            let containerId = questionId + '_answer_container';
            let container = document.getElementById(containerId);

            if(container.getElementsByTagName('div').length > 1) {
                let answer_container = container.lastElementChild;
                let radio = answer_container.lastElementChild;
                let wasChecked = radio.checked
                if(wasChecked){
                    let lastRadio = container.lastElementChild.lastElementChild;
                    lastRadio.checked = true;
                }
                container.removeChild(answer_container);
            }
        }

        function deleteLastChoiceMulti(questionId){
            let containerId = questionId + '_answer_container';
            let container = document.getElementById(containerId);

            if(container.getElementsByTagName('div').length > 1) {
                let answer_container = container.lastElementChild;
                container.removeChild(answer_container);
            }
        }
    </script>
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>
    <h1>Create Quiz</h1>
    <form action="CreateQuiz" method="post" enctype="multipart/form-data">
        <label for="title">Quiz Title:</label><br>
        <input type="text" id="title" name="title"><br><br>

        <label for="description">Quiz Description:</label><br>
        <textarea id="description" name="description"></textarea><br><br>

        <div class="block">
            <div>
                <label for="time">Total Time (in seconds):</label><br>
                <input type="number" id="time" name="time"><br><br>
                <label for="thumbnail">Thumbnail Image:</label><br>
                <input type="file" id="thumbnail" name="thumbnail" accept="image/*"><br><br>
            </div>
            <div>
                <label>Options:</label><br>
                <input type="checkbox" id="shouldMixUp" name="shouldMixUp">
                <label for="shouldMixUp">Mix Up Questions</label><br>
                <input type="checkbox" id="showAll" name="showAll">
                <label for="showAll">Show All Questions on Same Page</label><br>
                <input type="checkbox" id="allowPractice" name="allowPractice">
                <label for="allowPractice">Allow Practice Mode</label><br><br>

            </div>
        </div>

        <label for="questionType">Select Question Type:</label>
        <select id="questionType">
            <option value="response">Response Type</option>
            <option value="fillBlank">Fill in the Blank</option>
            <option value="multipleChoice">Multiple Choice</option>
            <option value="pictureResponse">Picture Response</option>
            <option value="multiChoiceMultiAnswer">Multiple Choice - Multiple Answer</option>
        </select>
        <button type="button" onclick="addQuestion()">Add Question</button><br><br>

        <div id="questionContainer"></div><br><br>

        <input type="submit" value="Create Quiz">
    </form>

    <div id="responseQuestionForm" style="display: none;">
        <input type="hidden" name="questionTypeIdentifier" value="response">
        <label>Question:</label><br>
        <input type="text" name="question"><br><br>

        <label>Answer:</label><br>
        <input type="text" name="answer"><br><br>
    </div>

    <div id="fillBlankQuestionForm" style="display: none;">
        <input type="hidden" name="questionTypeIdentifier" value="fillBlank">
        <label>Question:</label><br>
        <input type="text" name="question"><br><br>

        <label>Answer:</label><br>
        <input type="text" name="answer"><br><br>
    </div>

    <div id="pictureResponseQuestionForm" style="display: none;">
        <input type="hidden" name="questionTypeIdentifier" value="pictureResponse">
        <label>Question:</label><br>
        <input type="text" name="question"><br><br>
        <input type="file" name="picture" accept="image/*"><br><br>

        <label>Answer:</label><br>
        <input type="text" name="answer">
    </div>

    <div id="multipleChoiceQuestionForm" style="display: none;">
        <input type="hidden" name="questionTypeIdentifier" value="multipleChoice">
        <label>Question:</label><br>
        <input type="text" name="question"><br><br>

        <label>Answers:</label><br>
        <div id="answer_container" class="ans_container">
            <div class="answer_box">
                <input type="text" name="answer_1">
                <input type="radio" name="correct" value="1" checked>
            </div>
        </div>
        <br>
        <div class="answer_buttons">
            <button type="button" onclick="addChoice()">Add Choice</button>
            <button type="button" onclick="deleteLastChoice()">Delete Last Answer</button>
        </div>
    </div>

    <div id="multiChoiceMultiAnswerQuestionForm" style="display: none;">
        <input type="hidden" name="questionTypeIdentifier" value="multiChoiceMultiAnswer">
        <label>Question:</label><br>
        <input type="text" name="question"><br><br>

        <label>Answers:</label><br>
        <div id="answer_container" class="ans_container">
            <div class="answer_box">
                <input type="text" name="answer_1">
                <input type="checkbox" name="correct_1">
            </div>
        </div>
        <div class="answer_buttons">
            <button type="button" onclick="addChoiceMulti()">Add Answer</button>
            <button type="button" onclick="deleteLastChoiceMulti()">Delete Last Answer</button>
        </div>
    </div>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>
