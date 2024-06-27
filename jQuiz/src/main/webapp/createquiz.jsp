<%@ page import="models.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");
%>

<html>
<head>
    <title>Create Quiz</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
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

            let newInput = document.createElement('input');
            newInput.type = 'text';
            newInput.name = questionId + '_answer_' + choiceCount;
            newInput.placeholder = 'Choice ' + choiceCount;
            container.appendChild(newInput);

            let radioInput = document.createElement('input');
            radioInput.type = 'radio';
            radioInput.name = questionId + '_correct';
            radioInput.value = choiceCount;
            container.appendChild(radioInput);
        }

        function addChoiceMulti(questionId){
            let containerId = questionId + '_answer_container';
            let container = document.getElementById(containerId);

            let choiceCount = container.getElementsByTagName('input').length / 2 + 1;

            let newInput = document.createElement('input');
            newInput.type = 'text';
            newInput.name = questionId + '_answer_' + choiceCount;
            newInput.placeholder = 'Choice ' + choiceCount;
            container.appendChild(newInput);

            let newCheckbox = document.createElement('input');
            newCheckbox.type = 'checkbox';
            newCheckbox.name = questionId + '_correct_' + choiceCount;
            container.appendChild(newCheckbox);
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

            if(container.getElementsByTagName('input').length > 2) {
                let radio = container.lastElementChild;
                let wasChecked = radio.checked
                container.removeChild(radio);
                let text = container.lastElementChild;
                container.removeChild(text);
                if(wasChecked){
                    let lastRadio = container.lastElementChild;
                    lastRadio.checked = true;
                }
            }
        }

        function deleteLastChoiceMulti(questionId){
            let containerId = questionId + '_answer_container';
            let container = document.getElementById(containerId);

            if(container.getElementsByTagName('input').length > 2) {
                let radio = container.lastElementChild;
                container.removeChild(radio);
                let text = container.lastElementChild;
                container.removeChild(text);
            }
        }
    </script>
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
    <nav class="mail-nav">
        <ul>
            <li><a onclick="togglePanel()">Show Messages</a></li>
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
    <h1>Create Quiz</h1>
    <form action="CreateQuiz" method="post" enctype="multipart/form-data">
        <label for="title">Quiz Title:</label><br>
        <input type="text" id="title" name="title"><br><br>

        <label for="description">Quiz Description:</label><br>
        <textarea id="description" name="description"></textarea><br><br>

        <label for="time">Total Time (in seconds):</label><br>
        <input type="number" id="time" name="time"><br><br>

        <label>Options:</label><br>
        <input type="checkbox" id="shouldMixUp" name="shouldMixUp">
        <label for="shouldMixUp">Mix Up Questions</label><br>
        <input type="checkbox" id="showAll" name="showAll">
        <label for="showAll">Show All Questions on Same Page</label><br>
        <input type="checkbox" id="allowPractice" name="allowPractice">
        <label for="allowPractice">Allow Practice Mode</label><br><br>

        <label for="thumbnail">Thumbnail Image:</label><br>
        <input type="file" id="thumbnail" name="thumbnail" accept="image/*"><br><br>

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
        <div id="answer_container">
            <input type="text" name="answer_1">
            <input type="radio" name="correct" value="1" checked>
        </div>
        <button type="button" onclick="addChoice()">Add Choice</button><br><br>
        <button type="button" onclick="deleteLastChoice()">Delete Last Answer</button><br><br>
    </div>

    <div id="multiChoiceMultiAnswerQuestionForm" style="display: none;">
        <input type="hidden" name="questionTypeIdentifier" value="multiChoiceMultiAnswer">
        <label>Question:</label><br>
        <input type="text" name="question"><br><br>

        <label>Answers:</label><br>
        <div id="answer_container">
            <input type="text" name="answer_1">
            <input type="checkbox" name="correct_1">
        </div>
        <button type="button" onclick="addChoiceMulti()">Add Answer</button><br><br>
        <button type="button" onclick="deleteLastChoiceMulti()">Delete Last Answer</button><br><br>
    </div>
</main>

<script src="script/general.js"></script>

</body>
</html>
