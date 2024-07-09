<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="models.History" %>
<%@ page import="models.MailTypes" %>
<%@ page import="models.Mail" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="database.*" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 7/5/24
  Time: 5:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% User curUser = (User) request.getSession().getAttribute("curUser");
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);

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
    int totalUserCount = userDB.getTotalUserCount(), bannedUserCount = userDB.getBannedUserCount();
    List<User> admins = userDB.getAllAdmins();
    int totalQuizCount = quizDB.getTotalQuizCount(), totalAttempts = historyDB.getTotalAttemptCount();
%>
<html>
<head>
    <title>Statistics</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
</head>
<body>
    <%@ include file="header.jsp" %>
    <%@ include file="mail.jsp" %>
<main>
    <h2>Website Statistics:</h2>
    <p>Website has <%=totalUserCount%> total users, <%=bannedUserCount%> of them have been suspended.</p>
    <p>Website has <%=admins.size()%> admins:</p>
    <uL>
        <% for(User curAdmin : admins) { %>
            <li><%=curAdmin.getUsername() + ": " + curAdmin.getEmail() + ", since " + curAdmin.getCreated_at().toString()%></li>
        <% } %>
    </uL>
    <p>This website offers <%=totalQuizCount%> different quizzes. In total, they have been attempted <%=totalAttempts%> times.</p>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>
