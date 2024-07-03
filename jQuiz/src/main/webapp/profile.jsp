<%@ page import="models.*" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/27/24
  Time: 8:19 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    AchievementDatabase achievementDB = getDatabase(Database.ACHIEVEMENT_DB,request);

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
    String profileName = request.getParameter("username");
    User profileOf = userDB.getByUsername(profileName);
    List<Quiz> quizzesByUser = quizDB.getQuizzesByAuthorId(profileOf.getId());
    List<History> recentActivity = historyDB.getLatestHistoriesByUserId(profileOf.getId(),10);
    List<Achievement> unlockedAchievements = achievementDB.getAchievementsByUserId(profileOf.getId());
%>
<head>
    <title><%=profileName%></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/profile.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <div class="profile-info">
    <img class="profile-pic" src="image?type=user&userId=<%=profileOf.getId()%>" alt="profile-pic">
    <h2><%=profileOf.getUsername()%></h2>
    <h3><%="Member since: " + profileOf.getCreated_at().toString()%></h3>
        <% if(curUser == null) { %>
        <p><a href="login.jsp">Log in</a> or <a href="register.jsp">register</a> to add friends and compete against them!</p>
        <% } else if(curUser.getId() == profileOf.getId()) { %>
        <p>This is you.</p>
        <% } else if(mailDB.friendRequestPending(curUser.getId(),profileOf.getId())) { %>
        <p>Friend request sent.</p>
        <% } else if(mailDB.friendRequestPending(profileOf.getId(),curUser.getId())) { %>
        <p><%=profileOf.getUsername() + " has already sent you a friend request. Check your mail!"%></p>
        <% } else if(userDB.checkAreFriends(curUser.getId(),profileOf.getId())) { %>
            <p>You are friends.</p>
            <form action="AddFriend" method="get">
                <input type="hidden" name="from" value="<%=curUser.getId()%>">
                <input type="hidden" name="to" value="<%=profileOf.getId()%>">
                <input type="submit" value="Remove friend">
            </form>
        <% } else { %>
            <form action="AddFriend" method="post">
                <input type="hidden" name="from" value="<%=curUser.getId()%>">
                <input type="hidden" name="to" value="<%=profileOf.getId()%>">
                <input type="submit" value="Send friend request">
            </form>
        <% } %>
    </div>

    <h2>Quizzes created by <%=profileName%>:</h2>
    <% if(quizzesByUser.isEmpty()) { %>
        <h3>No quizzes created yet.</h3>
    <% } else { %>
        <h3><%="In total: " + quizzesByUser.size()%></h3>
        <div class="quiz-boxes">
            <% for(Quiz curQuiz : quizzesByUser) {%>
                <div class="quiz-box">
                    <a href="quizInfo.jsp?quizId=<%=curQuiz.getId()%>">
                        <div class="quiz-box-top">
                            <p class="quiz-box-name"><%= curQuiz.getTitle() %></p>
                        </div>
                        <div class="quiz-box-bot">
                            <p><%= curQuiz.getTotalPlayCount()%></p>
                        </div>
                    </a>
                </div>
            <% } %>
        </div>
    <% } %>

    <div class="activity">
        <h2>Recent Activity:</h2>
        <% if(recentActivity.isEmpty()) { %>
            <h3>Wow, such empty</h3>
        <% } else { %>
            <ul>
                <% for(History cur : recentActivity) {
                    Quiz curQuiz = quizDB.getQuizById(cur.getQuizId());%>
                <li><a href="quizInfo.jsp?<%=cur.getQuizId()%>"><%=curQuiz.getTitle()%></a>, Score: <%=cur.getGrade()%></li>
                <% } %>
            </ul>
        <% } %>
    </div>

    <h2>Achievements</h2>
    <div class="achievement-container">
        <% if(unlockedAchievements.isEmpty()) { %>
            <h3>Nothing yet.</h3>
        <% } else {
            for(Achievement cur : unlockedAchievements) { %>
            <div class="achievement">
                <img class="icon" src="<%=cur.getImage()%>" alt="icon">
                <div class="ach-info">
                    <h4><%=cur.getName()%></h4>
                    <p><%=cur.getDescription()%></p>
                </div>
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
