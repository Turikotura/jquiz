package servlets;

import attempts.QuestionAttempt;
import attempts.QuizAttemptsController;
import database.*;
import listeners.*;
import models.Answer;
import models.Question;
import models.Quiz;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static listeners.ContextListener.getDatabase;
import static listeners.SessionListener.getQuizAttemptsController;

public class QuizInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletRequest.getRequestDispatcher("quizInfo.jsp").forward(httpServletRequest,httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String username = ((User) httpServletRequest.getSession().getAttribute("curUser")).getUsername();
        if(username == null){
            httpServletResponse.sendRedirect("login.jsp");
            return;
        }

        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);
        int userId;
        try {
            userId = userdb.getByUsername(username).getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        boolean practice = httpServletRequest.getParameter("practice").equals("true");
        int quizId = Integer.parseInt(httpServletRequest.getParameter("quizId"));

        QuizAttemptsController qac = getQuizAttemptsController(userId,httpServletRequest);

        QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,httpServletRequest);
        QuestionDatabase questiondb = getDatabase(Database.QUESTION_DB,httpServletRequest);
        AnswerDatabase answerdb = getDatabase(Database.ANSWER_DB,httpServletRequest);

        Quiz quiz;
        try {
            quiz = quizdb.getById(quizId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<Question> questionList = null;
        try {
            questionList = questiondb.getQuestionsByQuizId(quiz.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Map<Question,List<Answer>> qToA = new HashMap<Question,List<Answer>>();
        for(Question question : questionList){
            List<Answer> answerList = null;
            try {
                answerList = answerdb.getAnswersByQuestionId(question.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            qToA.put(question,answerList);
        }

        int attemptId = qac.attemptQuiz(quiz,practice,qToA);

        httpServletRequest.setAttribute("attemptId",attemptId);
        httpServletResponse.sendRedirect("/PlayQuiz?attemptId="+attemptId);
    }
}