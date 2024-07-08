package servlets;

import database.Database;
import database.MailDatabase;
import database.UserDatabase;
import models.Mail;
import models.MailTypes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import static listeners.ContextListener.getDatabase;

public class MailPanelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doGet(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        boolean seen = httpServletRequest.getParameter("seen").equals("true");
        if(httpServletRequest.getParameter("mailId") == null){
            return;
        }
        int mailId = Integer.parseInt(httpServletRequest.getParameter("mailId"));
        MailDatabase maildb = getDatabase(Database.MAIL_DB,httpServletRequest);

        try {
            maildb.setFieldById(mailId,MailDatabase.SEEN,true);
            if(!seen){
                Mail mail = maildb.getById(mailId);
                if(mail.getType() == MailTypes.FRIEND_REQUEST){
                    boolean accept = httpServletRequest.getParameter("accept").equals("true");

                    if(accept){
                        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);

                        if(!userdb.checkAreFriends(mail.getSenderId(),mail.getReceiverId())){
                            userdb.addFriend(mail.getSenderId(),mail.getReceiverId());
                        }
                    }

                    maildb.removeById(mailId);

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}