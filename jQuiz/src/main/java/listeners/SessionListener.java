package listeners;

import attempts.QuizAttempt;
import attempts.QuizAttemptsController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
    private static final String QAC = "quiz_attempt_controller";
    public static QuizAttemptsController getQuizAttemptsController(int userId, HttpServletRequest req){
        QuizAttemptsController qac = (QuizAttemptsController) req.getSession().getAttribute(QAC);
        if(qac == null || qac.getUserId() != userId){
            qac = new QuizAttemptsController(userId);
            req.getSession().setAttribute(QAC,qac);
        }
        return qac;
    }
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

    }
}
