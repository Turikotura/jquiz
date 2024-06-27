package servlets;

import attempts.QuestionAttempt;
import attempts.QuizAttempt;
import attempts.QuizAttemptsController;
import database.HistoryDatabase;
import models.History;
import models.Question;
import models.QuestionTypes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static listeners.SessionListener.getQuizAttemptsController;
import static listeners.ContextListener.getDatabase;

public class PlayQuizServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        int userId = Integer.parseInt(httpServletRequest.getParameter("userId"));
        int attemptId = Integer.parseInt(httpServletRequest.getParameter("quizAttemptId"));

        QuizAttemptsController qac = getQuizAttemptsController(userId,httpServletRequest);
        QuizAttempt quizAttempt = qac.getQuizAttemptById(attemptId);

        if(httpServletRequest.getParameter("questionInd") != null){
            int questionInd = Integer.parseInt(httpServletRequest.getParameter("questionInd"));
            boolean eval = httpServletRequest.getParameter("eval").equals("true");

            QuestionAttempt questionAttempt = quizAttempt.getQuestions().get(questionInd);
            List<String> answers = new ArrayList<>();
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
            quizAttempt.getQuestions().get(questionInd).setWrittenAnswers(answers);
            if(eval){
                System.out.println(quizAttempt.getQuestions().get(questionInd).evaluateAnswers());
            }

            return;
        }else{

        }

        for(int i = 0; i < quizAttempt.getQuestions().size(); i++){
            QuestionAttempt questionAttempt = quizAttempt.getQuestions().get(i);
            List<String> answers = new ArrayList<>();
            if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTIPLE_CHOICE){
                answers.add(httpServletRequest.getParameter(String.format("%d",i)));
            }else if(questionAttempt.getQuestion().getQuestionType() == QuestionTypes.MULTI_ANS_MULTI_CHOICE){
                String[] ansPars = httpServletRequest.getParameterValues(String.format("%d",i));
                if(ansPars != null){
                    for(int j = 0; j < ansPars.length; j++){
                        answers.add(ansPars[j]);
                    }
                }
            }else{
                for(int j = 0; j < questionAttempt.getCorrectAnswersAmount(); j++){
                    String answer = httpServletRequest.getParameter(String.format("%d-%d",i,j));
                    answers.add(answer);
                }
            }
            quizAttempt.getQuestions().get(i).setWrittenAnswers(answers);
        }

        History history = qac.finishQuiz(attemptId);

        HistoryDatabase historydb = getDatabase(HistoryDatabase.HISTORY_DB, httpServletRequest);

        try {
            historydb.add(history);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        //httpServletResponse.sendRedirect("quizInfo.jsp?quizId="+quizAttempt.getQuizId());
        httpServletResponse.sendRedirect("quizResult.jsp?userId="+userId+"&quizId="+quizAttempt.getQuizId());
    }
}