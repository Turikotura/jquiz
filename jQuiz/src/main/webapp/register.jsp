<%@ page import="models.*" %>
<%@ page import="database.*" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="java.util.*" %>
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
    String message = "";
    if(request.getServletContext().getAttribute("reg-message") != null) message = ((String) request.getServletContext().getAttribute("reg-message"));
%>
<head>
    <title>Register</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/register.css" rel="stylesheet" type="text/css">
</head>
<body>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main class="register-main">
    <h1>Create New Account</h1>
    <form action="Register" method="post" enctype="multipart/form-data">
        <div>
            <label>
                Enter your email:
                <input type="email" name="email">
            </label>
            <p></p>
            <label>
                Username:
                <input type="input" name="user-name">
            </label>
            <p></p>
            <label>
                Password:
                <input type="password" name="password1">
            </label>
            <p></p>
            <label>
                Re-enter your password:
                <input type="password" name="password2">
            </label>
            <p></p>
            <label>
                Upload your profile photo (optional):
                <input type="file" id="photo" name="photo" accept="image/*"><br><br>
            </label>
            <p class="error-message"><%=message%></p>
            <input type="submit" value="Create">
            <span>Already have an account? <a href="login.jsp">Log in</a></span>
        </div>
    </form>
</main>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script> <!-- jQuery for AJAX -->
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>