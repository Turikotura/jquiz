package listeners;

import database.*;
import models.*;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextListener implements ServletContextListener {
    public static <T> T getDatabase(String databaseName, HttpServletRequest req){
        return (T)req.getServletContext().getAttribute(databaseName);
    }
    public static void getMailInfo(HttpServletRequest req){
        MailDatabase mailDB = getDatabase(Database.MAIL_DB,req);
        UserDatabase userDB = getDatabase(Database.USER_DB,req);
        HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,req);
        QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,req);

        User curUser = (User) req.getSession().getAttribute("curUser");
        // Mail variables
        List<Mail> mails = new ArrayList<Mail>();
        List<String> senderNames = new ArrayList<String>();
        Map<Integer,Integer> maxGrades = new HashMap<Integer,Integer>();

        try {
            if(curUser != null){
                // Get mails received by user
                mails = mailDB.getMailsByUserId(curUser.getId(),"RECEIVE");
                for(Mail mail : mails){
                    // Get names of senders
                    senderNames.add(userDB.getById(mail.getSenderId()).getUsername());
                    if(mail.getType() == MailTypes.CHALLENGE){
                        // Get max grade of senders for challenges
                        History history = historyDB.getBestHistoryByUserAndQuizId(mail.getSenderId(),mail.getQuizId());
                        int grade = (history == null) ? 0 : history.getGrade();
                        maxGrades.put(mail.getId(),grade);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error getting mail info");
        }

        req.setAttribute("mails",mails);
        req.setAttribute("senderNames",senderNames);
        req.setAttribute("maxGrades",maxGrades);
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
        TagDatabase tagdb = new TagDatabase(basicDataSource, Database.TAG_DB);

        servletContextEvent.getServletContext().setAttribute(Database.USER_DB, userdb);
        servletContextEvent.getServletContext().setAttribute(Database.QUIZ_DB, quizdb);
        servletContextEvent.getServletContext().setAttribute(Database.QUESTION_DB, questiondb);
        servletContextEvent.getServletContext().setAttribute(Database.ANSWER_DB, answerdb);
        servletContextEvent.getServletContext().setAttribute(Database.ACHIEVEMENT_DB, achievementdb);
        servletContextEvent.getServletContext().setAttribute(Database.HISTORY_DB, historydb);
        servletContextEvent.getServletContext().setAttribute(Database.MAIL_DB, maildb);
        servletContextEvent.getServletContext().setAttribute(Database.TAG_DB, tagdb);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
