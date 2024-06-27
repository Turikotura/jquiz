<%@ page import="database.*" %>
<%@ page import="models.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.sql.SQLException" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);

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

    // Search variables
    String searchString = request.getParameter("searchString");
    List<Quiz> matchingQuizzes = (List<Quiz>) request.getAttribute("foundQuizzes");
    List<User> matchingUsers = (List<User>) request.getAttribute("foundUsers");
%>
<head>
    <title>Searching: <%= searchString %></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1>Search Results for: "<%= searchString %>"</h1>

    <section>
        <h2>Users</h2>
        <ul>
            <% for (User user : matchingUsers) { %>
            <li><a href="userProfile.jsp?userId=<%= user.getId() %>"><%= user.getUsername() %></a></li>
            <% } %>
            <% if (matchingUsers.isEmpty()) { %>
            <li>No users found matching "<%= searchString %>"</li>
            <% } %>
        </ul>
    </section>

    <section>
        <h2>Quizzes</h2>
        <ul>
            <% for (Quiz quiz : matchingQuizzes) { %>
            <li><a href="quizInfo.jsp?quizId=<%= quiz.getId() %>"><%= quiz.getTitle() %></a></li>
            <% } %>
            <% if (matchingQuizzes.isEmpty()) { %>
            <li>No quizzes found matching "<%= searchString %>"</li>
            <% } %>
        </ul>
    </section>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>
