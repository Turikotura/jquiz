package servlets;

import database.Database;
import database.UserDatabase;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static listeners.ContextListener.getDatabase;

public class MailPanelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doGet(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        int toId = Integer.parseInt(httpServletRequest.getParameter("to"));
        int fromId = Integer.parseInt(httpServletRequest.getParameter("from"));

        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);

        try {
            if(!userdb.checkAreFriends(fromId,toId)){
                userdb.addFriend(fromId,toId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}
