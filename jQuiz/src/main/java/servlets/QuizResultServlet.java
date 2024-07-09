package servlets;

import database.*;
import models.History;
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
import java.util.List;

import static listeners.ContextListener.getDatabase;
import static listeners.ContextListener.getMailInfo;

public class QuizResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        UserDatabase userDB = getDatabase(Database.USER_DB,httpServletRequest);
        HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,httpServletRequest);
        QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,httpServletRequest);
        QuestionDatabase questionDB = getDatabase(Database.QUESTION_DB,httpServletRequest);

        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");

        // Mail
        getMailInfo(httpServletRequest);

        // Result info
        int userId = Integer.parseInt(httpServletRequest.getParameter("userId"));
        int quizId = Integer.parseInt(httpServletRequest.getParameter("quizId"));

        // History section
        History lastHistory = null;
        Quiz quiz = null;
        List<Question> questionList = new ArrayList<Question>();
        int totalScore = 0;

        String bestHistoryName = "";
        History bestHistory = null;
        List<History> prevAttempts = new ArrayList<History>();
        List<String> friendNames = new ArrayList<String>();
        List<History> friendHistories = new ArrayList<History>();
        try {
            lastHistory = historyDB.getLastHistoryByUserAndQuizId(userId, quizId);
            quiz = quizDB.getById(lastHistory.getQuizId());
            questionList = questionDB.getQuestionsByQuizId(quiz.getId());
            for (Question question : questionList) {
                totalScore += question.getScore();
            }

            // others results
            bestHistory = historyDB.getBestScoreHistoryByQuizId(quizId);
            bestHistoryName = userDB.getById(bestHistory.getUserId()).getUsername();
            prevAttempts = historyDB.getHistoryByUserAndQuizId(userId, quizId);
            List<User> friends = new ArrayList<User>();
            friends = userDB.getFriendsByUserId(userId);
            for(User friend : friends){
                History frHistory = historyDB.getLastHistoryByUserAndQuizId(friend.getId(),quiz.getId());
                if(frHistory != null){
                    friendNames.add(friend.getUsername());
                    friendHistories.add(historyDB.getLastHistoryByUserAndQuizId(friend.getId(),quiz.getId()));
                }
            }
        }catch (SQLException e){
//        System.out.println("SQL ex");
        }catch (ClassNotFoundException e){
//        System.out.println("Class not found ex");
        }

        httpServletRequest.setAttribute("lastHistory",lastHistory);
        httpServletRequest.setAttribute("totalScore",totalScore);

        httpServletRequest.setAttribute("bestHistoryName",bestHistoryName);
        httpServletRequest.setAttribute("bestHistory",bestHistory);
        httpServletRequest.setAttribute("prevAttempts",prevAttempts);
        httpServletRequest.setAttribute("friendNames",friendNames);
        httpServletRequest.setAttribute("friendHistories",friendHistories);

        httpServletRequest.getRequestDispatcher("quizResult.jsp").forward(httpServletRequest,httpServletResponse);
    }
}