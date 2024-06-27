<%@ page import="database.HistoryDatabase" %>
<%@ page import="javax.xml.crypto.Data" %>
<%@ page import="database.Database" %>
<%@ page import="static listeners.ContextListener.getDatabase" %>
<%@ page import="models.User" %>
<%@ page import="java.util.List" %>
<%@ page import="models.History" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: Dachi
  Date: 26.06.2024
  Time: 12:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    User curUser = (User)request.getSession().getAttribute("curUser");
    HistoryDatabase historydb = getDatabase(Database.HISTORY_DB,request);
    QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,request);

    List<History> histories = new ArrayList<History>();
    Map<Integer,String> quizNames = new HashMap<Integer,String>();
    try {
        if(curUser != null){
            histories = historydb.getHistoryByUserId(curUser.getId());
            for(History history : histories){
                quizNames.put(history.getId(),quizdb.getById(history.getQuizId()).getTitle());
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>

<html>
<head>
    <title>Title</title>
    <link href="style/general.css" rel="stylesheet" type="text/css">
    <link href="style/historySummary.css" rel="stylesheet" type="text/css">
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
        if(curUser != null){
            for(History history : histories){
                String isPractice = "";
                if(history.getIsPractice()){
                    isPractice = "practice";
                }
    %>
    <div class="history-box <%=isPractice%>">
        <h3 class="history-grade"><%=history.getGrade()%> Pts</h3>
        <h3 class="history-writing-time"><%=(double)history.getWritingTime()/1000%> Seconds</h3>
        <h4 class="history-completed-at"><%=history.getCompletedAt()%></h4>
        <a class="history-quiz" href="quizInfo.jsp?quizId=<%=history.getQuizId()%>"><%=quizNames.get(history.getId())%></a>
    </div>
    <%
            }
        }else{
    %>
    <p>Log in first</p>
    <%
        }
    %>
</main>

<script src="script/general.js"></script>

</body>
</html>