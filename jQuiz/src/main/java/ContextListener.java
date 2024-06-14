import database.*;
import models.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.crypto.Data;

public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://" + DBInfo.SERVER + "/" + DBInfo.NAME);
        basicDataSource.setUsername(DBInfo.USERNAME);
        basicDataSource.setPassword(DBInfo.PASSWORD);

        UserDatabase userdb = new UserDatabase(basicDataSource, Database.USER_DB);
        QuizDatabase quizdb = new QuizDatabase(basicDataSource, Database.QUIZ_DB);
        QuestionDatabase questiondb = new QuestionDatabase(basicDataSource, Database.QUESTION_DB);
        AnswerDatabase answerdb = new AnswerDatabase(basicDataSource, Database.ANSWER_DB);
        AchievementDatabase achievementdb = new AchievementDatabase(basicDataSource, Database.ACHIEVEMENT_DB);

        servletContextEvent.getServletContext().setAttribute(Database.USER_DB, userdb);
        servletContextEvent.getServletContext().setAttribute(Database.QUIZ_DB, quizdb);
        servletContextEvent.getServletContext().setAttribute(Database.QUESTION_DB, questiondb);
        servletContextEvent.getServletContext().setAttribute(Database.ANSWER_DB, answerdb);
        servletContextEvent.getServletContext().setAttribute(Database.ACHIEVEMENT_DB, achievementdb);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
