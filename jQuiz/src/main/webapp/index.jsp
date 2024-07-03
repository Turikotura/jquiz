<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.*" %>
<%@ page import="java.util.*" %>
<%@ page import="database.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    User curUser = (User) request.getSession().getAttribute("curUser");
    // Mail variables
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

            // Get history for panel
            histories = historyDB.getHistoryByUserId(curUser.getId());
            for(History history : histories){
                historyQuizNames.put(history.getId(),quizDB.getById(history.getQuizId()).getTitle());
            }
        }
        // Get quiz infos
        recentQuizzes = (List<Quiz>) request.getAttribute("recentQuizzes");
        popularQuizzes = (List<Quiz>) request.getAttribute("popularQuizzes");
        lastMonthPopularQuizzes = (List<Quiz>) request.getAttribute("lastMonthPopularQuizzes");

        if (userDB == null) {
            throw new Exception("User database not found in application.");
        }
        if (recentQuizzes == null || popularQuizzes == null || lastMonthPopularQuizzes == null) {
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
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <div class="announcements">
        <% if(curUser != null && userDB.isUserAdmin(curUser.getId())) { %>
            <form action="Announcement" method="get">
                <input type="hidden" name="authorId" value="<%=curUser.getId()%>">
                <input type="text" placeholder="Title" name="title" id="announcement-title"><br>
                <textarea placeholder="Enter your announcement here!" name="text" id="announcement-body"></textarea><br>
                <input type="submit" value="Publish"><br>
            </form>
        <% } %>
    </div>

    <%
        try{
    %>
    <div class="quiz-list-wrapper">
        <h2>Recently added quizzes</h2>
        <div class="quiz-boxes">
            <%
                for (Quiz quiz : recentQuizzes) {
                    User author = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top" style="background-size: cover; background-image: url('image?type=quiz&quizId=<%=quiz.getId()%>');">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><a href="profile.jsp?username=<%= author.getUsername()%>"><%= author.getUsername()%></a></p>
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
        <% for (Quiz quiz : popularQuizzes) {
                        User author = userDB.getById(quiz.getAuthorId());
                %>
                <div class="quiz-box">
                    <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                        <div class="quiz-box-top" style="background-size: cover; background-image: url('image?type=quiz&quizId=<%=quiz.getId()%>');">
                            <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                        </div>
                        <div class="quiz-box-bot">
                            <p><%= quiz.getTotalPlayCount()%></p>
                            <p><a href="profile.jsp?username=<%= author.getUsername()%>"><%= author.getUsername()%></a></p>
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
                    User author = userDB.getById(quiz.getAuthorId());
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top" style="background-size: cover; background-image: url('image?type=quiz&quizId=<%=quiz.getId()%>');">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><a href="profile.jsp?username=<%= author.getUsername()%>"><%= author.getUsername()%></a></p>
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