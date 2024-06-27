<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="database.*" %>
<%@ page import="models.*" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="models.MailTypes" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 25.06.2024
  Time: 20:38
  To change this template use File | Settings | File Templates.
--%>
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

%>

<html>
<head>
    <title>Title</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/sendMail.css" rel="stylesheet" type="text/css">
</head>
<body>
<header>
    <div class="logo">
        <img src="logo.png" alt="Website Logo">
    </div>
    <nav class="main-nav">
        <ul>
            <li><a href="index.jsp">Home</a></li>
            <li><a href="/users.jsp">Users</a></li>
            <li><a href="/achievements.jsp">Achievements</a></li>
            <li><a href="/categories.jsp">Categories</a></li>
            <li><a href="/createquiz.jsp">Create quiz</a></li>
        </ul>
    </nav>
    <nav class="mail-nav">
        <button onclick="togglePanel()">Show Messages</button>
    </nav>
    <nav class="auth-nav">
        <%if(request.getSession().getAttribute("curUser") == null) { %>
        <ul>
            <li><a href="login.jsp">Login</a></li>
            <li><a href="register.jsp">Register</a></li>
        </ul>
        <%} else {
            String loggedInAs = ((User)request.getSession().getAttribute("curUser")).getUsername();%>
        <ul>
            <li><a href="profile.jsp?username=<%=loggedInAs%>"><%=loggedInAs%></a></li>
            <li><form action="Login" method="get">
                <input type="submit" value="Log out">
            </form></li>
        </ul>
        <%}%>
    </nav>
</header>

<%@ include file="header.jsp" %>
<%@ include file="mail.jsp" %>

<main>
    <form id="send-mail" action="SendMail" method="post">
        <label for="receiver">Receiver:</label>
        <input type="text" id="receiver" name="receiver" required><br><br>

        <label>Choose state:</label><br>
        <input type="radio" id="state1" name="mail-type" value="0">
        <label for="state1">Default</label><br>

        <input type="radio" id="state2" name="mail-type" value="1">
        <label for="state2">Friend request</label><br>

        <input type="radio" id="state3" name="mail-type" value="2">
        <label for="state3">Challenge</label><br><br>

        <div id="quiz-name-field" class="hidden">
            <label for="quiz-name">Quiz Name:</label>
            <input type="text" id="quiz-name" name="quiz-name">
        </div>

        <br>

        <label for="text">Text:</label>
        <input type="text" id="text" name="text" required><br><br>

        <button type="submit">Submit</button>
    </form>
</main>
<%
    if(request.getParameter("error-log") != null){
%>
<p id="error-text"><%=request.getParameter("error-log")%></p>
<%
    }
%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script>
    $(document).ready(function() {
        $('input[name="mail-type"]').change(function() {
            if ($(this).attr('id') === 'state3') {
                $('#quiz-name-field').removeClass('hidden');
                $('#quiz-name').attr('required', true);
            } else {
                $('#quiz-name-field').addClass('hidden');
                $('#quiz-name').attr('required', false).val('');
            }
        });
    });
</script>
<script src="script/mailPanel.js"></script>
<script src="script/general.js"></script>

</body>
</html>