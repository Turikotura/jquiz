package servlets;

import database.Database;
import database.MailDatabase;
import database.UserDatabase;
import models.Mail;
import models.MailTypes;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static listeners.ContextListener.getDatabase;
import static listeners.ContextListener.getMailInfo;

public class FriendsPageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");

        UserDatabase userdb = getDatabase(UserDatabase.USER_DB,httpServletRequest);

        List<User> friends = new ArrayList<>();
        try {
            if(curUser != null){
                friends = userdb.getFriendsByUserId(curUser.getId());
            }
        } catch (SQLException | ClassNotFoundException e) {

        }
        httpServletRequest.setAttribute("friends",friends);

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