package servlets;

import database.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static listeners.ContextListener.getDatabase;

public class RemoveQuizServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        int quizId = Integer.parseInt(request.getParameter("quizId"));
        HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
        AnswerDatabase answerDB = getDatabase(Database.ANSWER_DB,request);
        QuestionDatabase questionDB = getDatabase(Database.QUESTION_DB,request);
        QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
        TagDatabase tagDB = getDatabase(Database.TAG_DB,request);
        CommentDatabase commentDB = getDatabase(Database.COMMENT_DB,request);
        try {
            commentDB.removeCommentsByQuizId(quizId);
            tagDB.clearTagsConnectionByQuizId(quizId);
            historyDB.clearQuizHistory(quizId);
            answerDB.deleteAnswersByQuizId(quizId);
            questionDB.deleteQuestionsByQuizId(quizId);
            quizDB.removeQuiz(quizId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        int quizId = Integer.parseInt(request.getParameter("quizId"));
        HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
        try {
            historyDB.clearQuizHistory(quizId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("quizInfo.jsp?quizId="+quizId);
    }
}
