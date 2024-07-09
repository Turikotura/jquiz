package servlets;

import database.*;
import models.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static listeners.ContextListener.getDatabase;
import static listeners.ContextListener.getMailInfo;

public class FriendsPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");

        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);
        QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,httpServletRequest);
        HistoryDatabase historydb = getDatabase(Database.HISTORY_DB,httpServletRequest);
        AchievementDatabase achdb = getDatabase(Database.ACHIEVEMENT_DB,httpServletRequest);

        List<User> friends = new ArrayList<>();

        List<Quiz> rfq = new ArrayList<>();
        List<History> rfh = new ArrayList<>();
        List<Achievement> rfa = new ArrayList<>();

        List<Activity> activities = new ArrayList<>();
        try {
            if(curUser != null){
                friends = userdb.getFriendsByUserId(curUser.getId());

                rfq = quizdb.getLatestFriendQuizzesByUserId(curUser.getId(),10);
                rfh = historydb.getLatestFriendHistoriesByUserId(curUser.getId(),10);

                for(Quiz quiz : rfq){
                    String authorName = userdb.getById(quiz.getAuthorId()).getUsername();
                    activities.add(new Activity(quiz.getCreatedAt(),quiz.getAuthorId(),authorName,"Created quiz",ActivityTypes.QUIZ_CREATE,quiz.getId(),quiz.getTitle()));
                }
                for(History history : rfh){
                    String writerName = userdb.getById(history.getUserId()).getUsername();
                    String quizTitle = quizdb.getById(history.getQuizId()).getTitle();
                    activities.add(new Activity(history.getCompletedAt(),history.getUserId(),writerName,"Written quiz : " + history.getGrade() + " pts",ActivityTypes.QUIZ_WRITE,history.getQuizId(),quizTitle));
                }

                activities.sort(Collections.reverseOrder());
            }
        } catch (SQLException | ClassNotFoundException e) {

        }
        httpServletRequest.setAttribute("friends",friends);

        httpServletRequest.setAttribute("activities",activities);

        getMailInfo(httpServletRequest);

        httpServletRequest.getRequestDispatcher("friends.jsp").forward(httpServletRequest,httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");

        if(httpServletRequest.getParameter("receiverId") != null){
            // Send mail
            int receiverId = Integer.parseInt(httpServletRequest.getParameter("receiverId"));
            String text = httpServletRequest.getParameter("message");

            MailDatabase maildb = getDatabase(Database.MAIL_DB,httpServletRequest);

            try {
                maildb.add(new Mail(-1,curUser.getId(),receiverId,MailTypes.DEFAULT,-1,text,new Date(),false));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else if(httpServletRequest.getParameter("toRemoveId") != null){
            // Remove friend
            int toRemoveId = Integer.parseInt(httpServletRequest.getParameter("toRemoveId"));

            UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);

            try {
                userdb.removeFriends(curUser.getId(), toRemoveId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }
}