<%@ page import="models.Quiz" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="org.apache.commons.dbcp2.BasicDataSource" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="database.Database" %>
<%@ page import="database.UserDatabase" %>
<%@ page import="models.User" %>
<%@ page import="java.util.List" %>
<%@ page import="database.HistoryDatabase" %>
<%@ page import="models.History" %>
<html>
<body>
<h2>Hello World!</h2>
</body>
</html>
<%
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl("jdbc:mysql://localhost:3306/quizDB");
    dataSource.setUsername("root");
    dataSource.setPassword("");
    Connection conn = dataSource.getConnection();
    QuizDatabase quizDB = new QuizDatabase(dataSource, Database.QUIZ_DB);
    ArrayList<Quiz> quizzes = (ArrayList<Quiz>) quizDB.getAll();
%>

<%
    UserDatabase userdb = new UserDatabase(dataSource, Database.USER_DB);
    List<User> topUsers = userdb.getHighestPerformers(3, "");
    for(int i = 0; i < topUsers.size(); i++){
%>
    <p><%=topUsers.get(i).getUsername()%></p>
<%
    }
%>

<%
    HistoryDatabase historydb = new HistoryDatabase(dataSource, Database.HISTORY_DB);
    History newestHistory = historydb.getLastHistoryByUserId(3);
%>
<p><%=newestHistory.getWritingTime()%></p>