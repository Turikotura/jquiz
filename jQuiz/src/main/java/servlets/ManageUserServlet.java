package servlets;

import database.*;
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

import static listeners.ContextListener.getDatabase;

public class ManageUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        // Get user and whether he is being promoted or banned from the parameters
        int managedUserId = Integer.parseInt(request.getParameter("managedUserId"));
        boolean userPromoted = Boolean.parseBoolean(request.getParameter("userPromoted"));
        // Databases needed
        UserDatabase userDB = getDatabase(Database.USER_DB,request);
        MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
        User managedUser = null, system = null, admin = (User) request.getSession().getAttribute("curUser");
        try {
            managedUser = userDB.getById(managedUserId);
            system = userDB.getByUsername("System");
            if(userPromoted) {
                userDB.promoteUserToAdmin(managedUserId);
                Mail promotionMail = new Mail(-1,system.getId(),managedUserId, MailTypes.DEFAULT,-1,
                        "Congratulations! You have been promoted to admin by " + admin.getUsername() + ". Keep up the good work!",new Date(),false);
                mailDB.add(promotionMail);
            } else {
                userDB.banUser(managedUserId);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("profile.jsp?username="+managedUser.getUsername());
    }
}
