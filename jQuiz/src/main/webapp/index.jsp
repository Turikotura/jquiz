<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="database.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    MailDatabase maildb = getDatabase(Database.MAIL_DB,request);
    UserDatabase userdb = getDatabase(Database.USER_DB,request);
    HistoryDatabase historydb = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,request);

    // Mail variables
    User curUser = (User)request.getSession().getAttribute("curUser");
    List<Mail> mails = new ArrayList<Mail>();
    List<String> senderNames = new ArrayList<String>();
    Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();
    // History variables
    List<History> histories = new ArrayList<History>();
    Map<Integer,String> historyQuizNames = new HashMap<Integer,String>();

    // Quiz display
    List<Quiz> recentQuizzes = new ArrayList<Quiz>();
    List<Quiz> popularQuizzes = new ArrayList<Quiz>();
    List<Quiz> lastMonthPopularQuizzes = new ArrayList<Quiz>();

    try {
        if(curUser != null){
            // Get mails received by user
            mails = maildb.getMailsByUserId(curUser.getId(),"RECEIVE");
            for(Mail mail : mails){
                // Get names of senders
                senderNames.add(userdb.getById(mail.getSenderId()).getUsername());
                if(mail.getType() == MailTypes.CHALLENGE){
                    // Get max grade of senders for challenges
                    History history = historydb.getBestHistoryByUserAndQuizId(mail.getSenderId(),mail.getQuizId());
                    int grade = (history == null) ? 0 : history.getGrade();
                    maxGrades.put(mail.getId(),grade);
                }
            }

            // Get history for panel
            histories = historydb.getHistoryByUserId(curUser.getId());
            for(History history : histories){
                historyQuizNames.put(history.getId(),quizdb.getById(history.getQuizId()).getTitle());
            }
        }
        // Get quiz infos
        recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
        popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
        lastMonthPopularQuizzes = (List<Quiz>) request.getAttribute("lastMonthPopularQuizzes");

        if (userdb == null) {
            throw new Exception("User database not found in application.");
        }
        if (recentQuizzes == null || popularQuizzes == null) {
            throw new Exception("Quizzes not found in request.");
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz List</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/index.css" rel="stylesheet" type="text/css">
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

    <%
        try{
    %>

<%--MAIL--%>
    <div id="mail-panel">
        <%
            if(curUser == null){
            // loads if there is no login
        %>
        <h3 id="log-in-first">Log in first</h3>
        <%
            }else{
        %>
        <%--button to send mails--%>
        <a id="send-mail" href="sendMail.jsp">Send Mail</a>
        <hr>
        <%
            }
        %>
        <%
            for(int i = 0; i < mails.size(); i++){
                Mail mail = mails.get(i);
                String senderName = senderNames.get(i);

                String active = "";
                if(!mail.getSeen()){
                    active = "active-message";
                }
        %>
        <%--mail info--%>
        <form id="mail-form-<%=mail.getId()%>" method="post" action="MailPanel">
            <input type="hidden" name="mailId" value="<%=mail.getId()%>">
            <div class="message-box <%=active%>">
                <p class="message-date"><%=mail.getTimeSent()%></p>
                <h3 class="message-text"><%=mail.getText()%></h3>
                <h4 class="message-author">From: <%=senderName%></h4>

            <%
                if(mail.getType() == MailTypes.CHALLENGE){
                // Display challenge visual
            %>
                <p>Best Score: <%=maxGrades.get(mail.getId())%> pts</p>
                <a href="quizInfo.jsp?quizId=<%=mail.getQuizId()%>">Accept</a>
            <%
                }else if(mail.getType() == MailTypes.FRIEND_REQUEST){
                // Display friend request visual
            %>
                <input type="submit" value="accept" class="friend-acpt-submit">
                <input type="submit" value="reject" class="friend-rjct-submit">
            <%
                }
            %>
            </div>
        </form>
        <%
            }
        %>
    </div>
<%----%>

    <div class="quiz-list-wrapper">
        <h2>Recently added quizzes</h2>
        <div class="quiz-boxes">
            <%
                for (Quiz quiz : recentQuizzes) {
                    User author = userdb.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                }
            %>
        </div>
    </div>

    <div class="quiz-list-wrapper">
        <h2>All-time popular quizzes</h2>
        <div class="quiz-boxes">
            <%
                for (Quiz quiz : popularQuizzes) {
                    User author = userdb.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                }
            %>
        </div>
    </div>

    <div class="quiz-list-wrapper">
        <h2>Popular quizzes in the last month</h2>
        <div class="quiz-boxes">
            <%
                for (Quiz quiz : lastMonthPopularQuizzes) {
                    User author = userdb.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= author.getUsername()%></p>
                    </div>
                </a>
            </div>
            <%
                }
            %>
        </div>
    </div>

    <%
        if(curUser != null){
    %>
    <section id="history-panel">
        <%
            for(History history : histories){
        %>
        <div class="history-card">
            <h4 class="history-grade"><%=history.getGrade()%> Pts</h4>
            <a class="history-quiz" href="quizInfo.jsp?quizId=<%=history.getQuizId()%>"><%=historyQuizNames.get(history.getId())%></a>
        </div>
        <%
            }
        %>
    </section>
    <%
        }
    %>

    <%
        }catch (Exception ex){
    %>
    <p>An exception occurred</p>
    <%
            ex.printStackTrace();
        }
    %>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>