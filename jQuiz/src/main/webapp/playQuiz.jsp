<%@ page import="java.sql.SQLException" %>
<%@ page import="models.Quiz" %>
<%@ page import="database.Database" %>
<%@ page import="database.QuizDatabase" %>
<%@ page import="models.Question" %>
<%@ page import="models.QuestionTypes" %>
<%@ page import="java.util.List" %>
<%@ page import="database.QuestionDatabase" %>
<%@ page import="database.AnswerDatabase" %>
<%@ page import="models.Answer" %><%--
  Created by IntelliJ IDEA.
  User: giorgi
  Date: 6/18/24
  Time: 10:30 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<% int quizId = Integer.parseInt(request.getParameter("quizId"));
    QuizDatabase quizDB = (QuizDatabase) application.getAttribute(Database.QUIZ_DB);
    QuestionDatabase questionDB = (QuestionDatabase) application.getAttribute(Database.QUESTION_DB);
    AnswerDatabase answerDB = (AnswerDatabase) application.getAttribute(Database.ANSWER_DB);
    Quiz curQuiz = null;
    List<Question> quizQuestions = null;
    try {
        curQuiz = quizDB.getQuizById(quizId);
        quizQuestions = questionDB.getQuestionsByQuizId(quizId);
    } catch (SQLException e) {
        throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
    }
%>
<head>
    <title><%=curQuiz.getTitle()%></title>
</head>
<body>
    <h1><%=curQuiz.getTitle()%></h1>
    <form>
        <% for(Question curQ : quizQuestions) {
            if(curQ.getQuestionTypeId() <= QuestionTypes.PIC_RESPONSE.ordinal()) {%>
                <label>
                    <%=curQ.getText()%>
                    <br>
                    <% if(curQ.getQuestionTypeId() == QuestionTypes.PIC_RESPONSE.ordinal()) { %>
                        <img src = "<%=curQ.getImageUrl()%>" alt="Question Pic">
                    <% } %>
                    <input type="input" name="question<%=curQ.getId()%>">
                </label>
                <br>
            <% } else if(curQ.getQuestionTypeId() <= QuestionTypes.MULTI_ANS_MULTI_CHOICE.ordinal()) { %>
                <label>
                    <%=curQ.getText()%>
                    <br>
                    <% List<Answer> options = answerDB.getAnswersByQuestionId(curQ.getId());
                        String inputType = curQ.getQuestionTypeId() == QuestionTypes.MULTI_ANS_MULTI_CHOICE.ordinal() ? "checkbox" : "radio";
                    for(int i = 0; i < options.size(); i++) { %>
                        <input type="<%=inputType%>" id="question<%=curQ.getId()%><%=i%>" name="question<%=curQ.getId()%>" value="<%=options.get(i).getText()%>">
                        <label for="question<%=curQ.getId()%><%=i%>"><%=options.get(i).getText()%></label><br>
                    <% } %>
                </label>
            <% } %>
        <% } %>
        <input type="submit" value="Finish">
    </form>
</body>
</html>
