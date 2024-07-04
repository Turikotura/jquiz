<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="database.*" %>
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

    String tagName = request.getParameter("name");
    if (tagName == null || tagName.isEmpty()) {
        throw new IllegalArgumentException("Category name is required");
    }

    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB, request);
    List<Quiz> quizzes;

    try {
        quizzes = quizDB.getQuizzesByTagName(tagName);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quizzes with tag "<%= tagName %>"</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/tag.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1>Quizzes with tag "<%= tagName %>"</h1>

    <div class="quiz-list-wrapper">
        <div class="quiz-boxes">
            <% for (Quiz quiz : quizzes) {
                User author = userDB.getById(quiz.getAuthorId());%>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%= quiz.getId() %>">
                    <div class="quiz-box-top" style="background-size: cover; background-image: url('image?type=quiz&quizId=<%= quiz.getId() %>');">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount() %> plays</p>
                        <p><a href="profile.jsp?username=<%= author.getUsername()%>"><%= author.getUsername()%></a></p>
                    </div>
                </a>
            </div>
            <% } %>
        </div>
    </div>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>
