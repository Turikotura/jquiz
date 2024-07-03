<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="database.*" %>
<%@ page import="models.*" %>
<%@ page import="java.sql.SQLException" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 7/2/24
  Time: 5:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User) request.getSession().getAttribute("curUser");
    AchievementDatabase achievementDB = getDatabase(Database.ACHIEVEMENT_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);

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

    List<Achievement> userAchievements = null;
    List<Achievement> allAchievements = null;
    if(curUser != null) {
        try {
            userAchievements = achievementDB.getAchievementsByUserId(curUser.getId());
            allAchievements = achievementDB.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
%>
<html>
<head>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/achievements.css" rel="stylesheet" type="text/css">
    <title>Achievements</title>
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h2>Achievements</h2>
    <div class="achievement-container">
        <% if(userAchievements == null) { %>
            <p><a href="login.jsp">Log in</a> or <a href="register.jsp">Register</a> to unlock and track your achievements.</p>
        <% } else {
            for(Achievement cur : allAchievements) { %>
                <div class="achievement">
                    <img class="icon" src="<%=cur.getImage()%>" alt="icon">
                    <div class="ach-info">
                    <h4><%=cur.getName()%></h4>
                    <p><%=cur.getDescription()%></p>
                    </div>
                    <% if(!userAchievements.contains(cur)) { %>
                        <img class="lock-img" src="https://i.pinimg.com/1200x/67/9a/8c/679a8ce044245372ca495c4c9c151225.jpg" alt="locked">
                    <% } %>
                </div>
            <% }
       } %>
    </div>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>
</body>
</html>
