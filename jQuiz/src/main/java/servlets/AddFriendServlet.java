package servlets;

import database.Database;
import database.MailDatabase;
import database.UserDatabase;
import models.Mail;
import models.MailTypes;
import models.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class AddFriendServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        MailDatabase mailDB = (MailDatabase) request.getServletContext().getAttribute(Database.MAIL_DB);
        UserDatabase userDB = (UserDatabase) request.getServletContext().getAttribute(Database.USER_DB);
        int from = Integer.parseInt(request.getParameter("from")), to = Integer.parseInt(request.getParameter("to"));
        User remover,removed = null;
        try {
            remover = userDB.getById(from);
            removed = userDB.getById(to);
            Mail friendRequest = new Mail(-1,from,to, MailTypes.DEFAULT,-1,
                    remover.getUsername() + "has removed you from the friend list.",new Date(),false);
            userDB.removeFriends(from,to);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        response.sendRedirect("profile.jsp?username=" + removed.getUsername());
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        MailDatabase mailDB = (MailDatabase) request.getServletContext().getAttribute(Database.MAIL_DB);
        UserDatabase userDB = (UserDatabase) request.getServletContext().getAttribute(Database.USER_DB);
        int from = Integer.parseInt(request.getParameter("from")), to = Integer.parseInt(request.getParameter("to"));
        boolean requestSent = Boolean.parseBoolean(request.getParameter("requestSent"));
        User sender, reciever = null;

        try {
            sender = userDB.getById(from);
            reciever = userDB.getById(to);
            if(requestSent) {
                Mail friendRequest = new Mail(-1,from,to, MailTypes.FRIEND_REQUEST,-1,"",new Date(),false);
                mailDB.add(friendRequest);
            } else {
                Mail removalNote = new Mail(-1,from,to, MailTypes.DEFAULT,-1,
                        sender.getUsername() + "has removed you from the friend list.",new Date(),false);
                mailDB.add(removalNote);
                userDB.removeFriends(from,to);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("profile.jsp?username=" + reciever.getUsername());
    }
}
