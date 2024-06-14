<%@ page import="models.Quiz" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="org.apache.commons.dbcp2.BasicDataSource" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="database.Database" %>
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