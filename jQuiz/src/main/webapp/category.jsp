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

    String categoryName = request.getParameter("category");
    if (categoryName == null || categoryName.isEmpty()) {
        throw new IllegalArgumentException("Category name is required");
    }

    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB, request);
    List<Quiz> recentQuizzes;
    List<Quiz> popularQuizzes;


    try {
        recentQuizzes = quizDB.getRecentQuizzesByCategory(5,categoryName);
        popularQuizzes = quizDB.getPopularQuizzesByCategory(5, "TOTAL", categoryName);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quizzes in <%= categoryName %></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/category.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1>Quizzes in <%= categoryName %></h1>

    <div class="quiz-list-wrapper">
        <h2>Most Recent Quizzes</h2>
        <a href="quizzes?sortBy=LAST_MONTH&category=<%= categoryName%>">See all</a>
        <div class="quiz-boxes">
            <% for (Quiz quiz : recentQuizzes) {
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

    <div class="quiz-list-wrapper">
        <h2>Most Popular Quizzes</h2>
        <a href="quizzes?sortBy=TOTAL&category=<%= categoryName%>">See all</a>
        <div class="quiz-boxes">
            <% for (Quiz quiz : popularQuizzes) {
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
