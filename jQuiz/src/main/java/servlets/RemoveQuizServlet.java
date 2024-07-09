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
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method is not supported");
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        // Get quiz id and if we are completely deleting it from parameters
        int quizId = Integer.parseInt(request.getParameter("quizId"));
        boolean deleteQuiz = Boolean.parseBoolean(request.getParameter("deleteQuiz"));
        // Databases needed
        HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,request);
        AnswerDatabase answerDB = getDatabase(Database.ANSWER_DB,request);
        QuestionDatabase questionDB = getDatabase(Database.QUESTION_DB,request);
        QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,request);
        TagDatabase tagDB = getDatabase(Database.TAG_DB,request);
        CommentDatabase commentDB = getDatabase(Database.COMMENT_DB,request);

        try {
            // Clear history for both cases
            commentDB.removeCommentsByQuizId(quizId);
            historyDB.clearQuizHistory(quizId);
            if(deleteQuiz) {
                // Delete answers, questions, tag and quiz itself
                answerDB.deleteAnswersByQuizId(quizId);
                questionDB.deleteQuestionsByQuizId(quizId);
                tagDB.clearTagsConnectionByQuizId(quizId);
                quizDB.removeQuiz(quizId);
                response.sendRedirect("");
            } else {
                response.sendRedirect("quizInfo.jsp?quizId="+quizId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
