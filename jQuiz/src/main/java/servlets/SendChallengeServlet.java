package servlets;

import com.google.gson.Gson;
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
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static listeners.ContextListener.getDatabase;

public class SendChallengeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        if(httpServletRequest.getSession().getAttribute("curUser") == null){
            return;
        }

        Map<String,String> respMap = new HashMap<>();

        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");
        int quizId = Integer.parseInt(httpServletRequest.getParameter("quizId"));
        String toUsername = httpServletRequest.getParameter("toUsername");
        String message = httpServletRequest.getParameter("message");

        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);
        MailDatabase maildb = getDatabase(Database.MAIL_DB,httpServletRequest);


        try {
            User toUser = userdb.getByUsername(toUsername);

            // Challenge error checking
            if(Objects.equals(curUser.getUsername(), toUsername)){
                respMap.put("errorText","Can't have your own name");
            }else if(toUser == null){
                respMap.put("errorText","Can't find user to send to");
            }else if(!userdb.checkAreFriends(curUser.getId(),toUser.getId())){
                respMap.put("errorText","Can't challenge a non friend");
            }else{
                // If passed all, send
                maildb.add(new Mail(-1,curUser.getId(),toUser.getId(), MailTypes.CHALLENGE,quizId,message,new Date(),false));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Return response
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.getWriter().write(new Gson().toJson(respMap));
    }
}
