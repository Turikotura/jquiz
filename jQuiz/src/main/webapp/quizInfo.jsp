<%@ page import="java.sql.SQLException" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<%@ page import="models.Mail" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="database.*" %>
<%@ page import="models.History" %>
<%@ page import="database.HistoryDatabase" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<%
    User curUser = (User) request.getSession().getAttribute("curUser");
    System.out.println(curUser);

    MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
    UserDatabase userDB = getDatabase(Database.USER_DB,request);
    HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
    List<History> prevAttempts, bestAttempts, fastestAttempts = new ArrayList<History>();

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

    int quizId = Integer.parseInt(request.getParameter("quizId"));
    Quiz curQuiz = null;
    User author = null;

    try {
        curQuiz = quizDB.getById(quizId);
        author = userDB.getById(curQuiz.getAuthorId());
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/quizResult.css" rel="stylesheet" type="text/css">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const ids = ['recent','fastest','best'];
            document.getElementById('fastest').style.display = 'none';
            document.getElementById('best').style.display = 'none';

            const sortSelect = document.getElementById('sort');

            sortSelect.addEventListener('change', function() {
                const selectedValue = sortSelect.value;
                for (const id of ids) {
                    if(id === selectedValue) {
                        document.getElementById(id).style.display = 'block';
                    } else {
                        document.getElementById(id).style.display = 'none';
                    }
                }

                console.log('Selected sort option:', selectedValue);
            });
        });
    </script>
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1><%=curQuiz.getTitle()%></h1>
    <h3><%="Author: " + author.getUsername()%></h3>
    <h3><%="Created at: " + curQuiz.getCreatedAt().toString()%></h3>
    <h3><%="Time Limit: " + curQuiz.getMaxTime() + " seconds"%></h3>
    <p><%=curQuiz.getDescription()%></p>
    <% if(curQuiz.getAllowPractice()) { %>
    <form action="QuizInfo" method="post">
        <input name="practice" type="hidden" value="true">
        <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
        <input type="submit" value="Practice">
    </form>
    <% } %>
    <form action="QuizInfo" method="post">
        <input name="practice" type="hidden" value="false">
        <input name="quizId" type="hidden" value="<%=curQuiz.getId()%>">
        <input type="submit" value="Start Quiz">
    </form>

    <h3>Your previous attempts</h3>
    <hr>
    <% if(request.getSession().getAttribute("curUser") == null) { %>
        <p><a href="login.jsp">Log in</a> to track your attempts.</p>
    <% } else { %>
        <label for="sort">Sort by:</label>
        <select id="sort">
            <option value="recent">Most Recent</option>
            <option value="fastest">Fastest Completion</option>
            <option value="best">Highest Score</option>
        </select>
        <div class="scroll-container" id="recent">
            <% int userId = ((User) request.getSession().getAttribute("curUser")).getId();
                prevAttempts = historyDB.getLatestHistoriesByUserAndQuizId(userId, quizId);
                for(int i = 0; i < prevAttempts.size(); i++){ %>
            <div class="box">
                <h3><%=prevAttempts.get(i).getGrade()%></h3>
                <h3><%=prevAttempts.get(i).getWritingTime()%> Seconds</h3>
                <h4><%=prevAttempts.get(i).getCompletedAt()%></h4>
            </div>
            <%}%>
        </div>

        <div class="scroll-container" id="fastest">
            <% fastestAttempts = historyDB.getFastestByUserAndQuizId(userId, quizId);
                for(int i = 0; i < fastestAttempts.size(); i++){ %>
            <div class="box">
                <h3><%=fastestAttempts.get(i).getGrade()%></h3>
                <h3><%=fastestAttempts.get(i).getWritingTime()%> Seconds</h3>
                <h4><%=fastestAttempts.get(i).getCompletedAt()%></h4>
            </div>
            <%}%>
        </div>

        <div class="scroll-container" id="best">
            <% bestAttempts = historyDB.getBestScoresByUserAndQuizId(userId, quizId);
                for(int i = 0; i < bestAttempts.size(); i++){ %>
            <div class="box">
                <h3><%=bestAttempts.get(i).getGrade()%></h3>
                <h3><%=bestAttempts.get(i).getWritingTime()%> Seconds</h3>
                <h4><%=bestAttempts.get(i).getCompletedAt()%></h4>
            </div>
            <%}%>
        </div>
    <%}%>

    <h3>Hall of fame</h3>
    <% try {
        List<History> topOfAllTime = historyDB.getTopPerformersByQuizId(curQuiz.getId(),10);
        if(topOfAllTime.isEmpty()) { %>
            <p>No one has taken this quiz yet. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : topOfAllTime) {
                    User topScorer = userDB.getById(curTry.getUserId());
                    String scorerName = topScorer.getUsername();%>
                <li><a href="profile.jsp?username=<%=scorerName%>"><%=scorerName%></a> <%=": " + curTry.getGrade() + " in " + curTry.getWritingTime() + "sec"%></li>
                <% } %>
            </ol>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>


    <h3>Top performers of the last day</h3>
    <% try {
        List<History> topOfLastDay = historyDB.getTopInLastDayByQuizId(curQuiz.getId(),10);
        if(topOfLastDay.isEmpty()) { %>
            <p>No one has taken this quiz in the last day. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : topOfLastDay) {
                    User topScorer = userDB.getById(curTry.getUserId());
                    String scorerName = topScorer.getUsername();%>
                <li><a href="profile.jsp?username=<%=scorerName%>"><%=scorerName%></a><%=": " + curTry.getGrade() + " in " + curTry.getWritingTime() + "sec"%></li>
                <% } %>
            </ol>
        <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>

    <h3>Recent attempts</h3>
    <% try {
        List<History> recentAttempts = historyDB.getLatestHistoriesByQuizId(curQuiz.getId(),10);
        if(recentAttempts.isEmpty()) { %>
            <p>No one has taken this quiz yet. Be the first!</p>
        <% } else { %>
            <ol>
                <% for(History curTry : recentAttempts) {
                    User topScorer = userDB.getById(curTry.getUserId());
                    String scorerName = topScorer.getUsername();%>
                <li><a href="profile.jsp?username=<%=scorerName%>"><%=scorerName%></a> <%=": " + curTry.getGrade() + " at " + curTry.getCompletedAt().toString()%></li>
                <% } %>
            </ol>
    <% }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    } %>

    <div class="quiz-list-wrapper">
        <h2>Quizzes by the same author:</h2>
        <div class="quiz-boxes">
            <% try {
                    List<Quiz> quizzesBySameAuthor = quizDB.getQuizzesByAuthorId(author.getId());
                    if (quizzesBySameAuthor == null) {
                        throw new Exception("Quizzes not found in request.");
                    }

                    for (Quiz quiz : quizzesBySameAuthor) {
                        User curAuthor = userDB.getById(quiz.getAuthorId());
                        if(quiz.getId() == curQuiz.getId()) continue;
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
            <% }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<p>Error loading quizzes: " + e.getMessage() + "</p>");
            } %>
        </div>
    </div>
    </main>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>
</body>
</html>
