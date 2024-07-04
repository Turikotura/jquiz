package servlets;

import attempts.QuestionAttempt;
import attempts.QuizAttempt;
import attempts.QuizAttemptsController;
import database.*;
import com.google.gson.Gson;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static listeners.SessionListener.getQuizAttemptsController;
import static listeners.ContextListener.getDatabase;

public class PlayQuizServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");

        int attemptId = Integer.parseInt(httpServletRequest.getParameter("attemptId"));

        QuizAttemptsController qac = getQuizAttemptsController(curUser.getId(),httpServletRequest);
        QuizAttempt quizAttempt = qac.getQuizAttemptById(attemptId);
        httpServletRequest.setAttribute("qa",quizAttempt);

        httpServletRequest.getRequestDispatcher("playQuiz.jsp").forward(httpServletRequest,httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        int userId = Integer.parseInt(httpServletRequest.getParameter("userId"));
        int attemptId = Integer.parseInt(httpServletRequest.getParameter("quizAttemptId"));

        QuizAttemptsController qac = getQuizAttemptsController(userId,httpServletRequest);
        QuizAttempt quizAttempt = qac.getQuizAttemptById(attemptId);

        if(httpServletRequest.getParameter("questionInd") != null){
            // Question answer update
            int questionInd = Integer.parseInt(httpServletRequest.getParameter("questionInd"));

            QuestionAttempt questionAttempt = quizAttempt.getQuestions().get(questionInd);
            List<String> answers = new ArrayList<>();

            // Get answers by question type
            if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTIPLE_CHOICE){
                answers.add(httpServletRequest.getParameter(String.format("%d",questionInd)));
            }else if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTI_ANS_MULTI_CHOICE){
                String[] ansPars = httpServletRequest.getParameterValues(String.format("%d",questionInd));
                if(ansPars != null){
                    for(int j = 0; j < ansPars.length; j++){
                        answers.add(ansPars[j]);
                    }
                }
            }else{
                for(int j = 0; j < questionAttempt.getCorrectAnswersAmount(); j++){
                    String answer = httpServletRequest.getParameter(String.format("%d-%d",questionInd,j));
                    answers.add(answer);
                }
            }

            // Update answers
            quizAttempt.getQuestions().get(questionInd).setWrittenAnswers(answers);

            if(httpServletRequest.getParameter("nextQ") != null){
                // Move to next question
                quizAttempt.setOnQuestionIndex(quizAttempt.getOnQuestionIndex()+1);
            }

            if(httpServletRequest.getParameter("eval") != null){
                // Evaluate question
                quizAttempt.getQuestions().get(questionInd).evaluateAnswers();

                Map<String,Integer> respMap = new HashMap<>();
                respMap.put("qInd",questionInd);
                respMap.put("grade",quizAttempt.getQuestions().get(questionInd).evaluateAnswers());
                respMap.put("maxGrade",quizAttempt.getQuestions().get(questionInd).getMaxScore());
                httpServletResponse.setContentType("application/json");
                httpServletResponse.setCharacterEncoding("UTF-8");
                httpServletResponse.getWriter().write(new Gson().toJson(respMap));
            }
        }else{
            // Finish quiz
            History history = qac.finishQuiz(attemptId);
            HistoryDatabase historydb = getDatabase(HistoryDatabase.HISTORY_DB, httpServletRequest);

            try {
                historydb.add(history);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
          
            try {
              List<History> quizzesWritten = historydb.getHistoryByUserId(userId);
              AchievementDatabase achievementDB = getDatabase(AchievementDatabase.ACHIEVEMENT_DB, httpServletRequest);
              MailDatabase mailDB = getDatabase(Database.MAIL_DB,httpServletRequest);
              UserDatabase userDB = getDatabase(Database.USER_DB,httpServletRequest);
              User system = userDB.getByUsername("System");
              if(quizzesWritten.size() == 10) {
                  achievementDB.unlockAchievement(userId,"Quiz Machine");
                  mailDB.sendAchievementMail(system.getId(), userId,"Quiz Machine");
              }
              History bestAttempt = historydb.getBestScoreHistoryByQuizId(history.getQuizId());
              if(bestAttempt.getUserId() == userId && !achievementDB.hasAchievementUnlocked(userId,"I am the Greatest")) {
                  achievementDB.unlockAchievement(userId,"I am the Greatest");
                  mailDB.sendAchievementMail(system.getId(), userId,"I am the Greatest");
              }
              if(history.getIsPractice() && !achievementDB.hasAchievementUnlocked(userId,"Practice Makes Perfect")) {
                  achievementDB.unlockAchievement(userId,"Practice Makes Perfect");
                  mailDB.sendAchievementMail(system.getId(), userId,"Practice Makes Perfect");
              }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            httpServletResponse.sendRedirect("quizResult.jsp?userId="+userId+"&quizId="+quizAttempt.getQuizId());
        }
    }
}