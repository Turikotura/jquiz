package listeners;

import database.*;
import models.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;

public class ContextListener implements ServletContextListener {
    public static <T> T getDatabase(String databaseName, HttpServletRequest req){
        return (T)req.getServletContext().getAttribute(databaseName);
    }
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://" + DBInfo.SERVER + "/" + DBInfo.NAME);
        basicDataSource.setUsername(DBInfo.USERNAME);
        basicDataSource.setPassword(DBInfo.PASSWORD);
        basicDataSource.setInitialSize(10);
        basicDataSource.setMaxTotal(50);
        basicDataSource.setMaxIdle(20);
        basicDataSource.setMinIdle(5);
        basicDataSource.setMaxWaitMillis(10000);

        UserDatabase userdb = new UserDatabase(basicDataSource, Database.USER_DB);
        QuizDatabase quizdb = new QuizDatabase(basicDataSource, Database.QUIZ_VIEW);
        QuestionDatabase questiondb = new QuestionDatabase(basicDataSource, Database.QUESTION_DB);
        AnswerDatabase answerdb = new AnswerDatabase(basicDataSource, Database.ANSWER_DB);
        AchievementDatabase achievementdb = new AchievementDatabase(basicDataSource, Database.ACHIEVEMENT_DB);
        HistoryDatabase historydb = new HistoryDatabase(basicDataSource, Database.HISTORY_DB);
        MailDatabase maildb = new MailDatabase(basicDataSource, Database.MAIL_DB);

        servletContextEvent.getServletContext().setAttribute(Database.USER_DB, userdb);
        servletContextEvent.getServletContext().setAttribute(Database.QUIZ_DB, quizdb);
        servletContextEvent.getServletContext().setAttribute(Database.QUESTION_DB, questiondb);
        servletContextEvent.getServletContext().setAttribute(Database.ANSWER_DB, answerdb);
        servletContextEvent.getServletContext().setAttribute(Database.ACHIEVEMENT_DB, achievementdb);
        servletContextEvent.getServletContext().setAttribute(Database.HISTORY_DB, historydb);
        servletContextEvent.getServletContext().setAttribute(Database.MAIL_DB, maildb);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
