<%@ page import="java.sql.SQLException" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<%@ page import="models.Mail" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
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
</head>
<body>
<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>
<main>
    <h1><%=curQuiz.getTitle()%></h1>
    <h3><%="Author: " + author.getUsername()%></h3>
    <h3><%="Created at: " + curQuiz.getCreatedAt().toString()%></h3>
    <h3><%="Time Limit: " + curQuiz.getMaxTime() + " minutes"%></h3>
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
            %>
            <div class="quiz-box">
                <a href="quizInfo.jsp?quizId=<%=quiz.getId()%>">
                    <div class="quiz-box-top">
                        <p class="quiz-box-name"><%= quiz.getTitle() %></p>
                    </div>
                    <div class="quiz-box-bot">
                        <p><%= quiz.getTotalPlayCount()%></p>
                        <p><%= curAuthor.getUsername()%></p>
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

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>
