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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        int newAdminId = Integer.parseInt(request.getParameter("userToPromote"));
        UserDatabase userDB = getDatabase(Database.USER_DB,request);
        MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
        User newAdmin = null, system = null, curUser = (User) request.getSession().getAttribute("curUser");
        try {
            userDB.promoteUserToAdmin(newAdminId);
            newAdmin = userDB.getById(newAdminId);
            system = userDB.getByUsername("System");
            Mail promotionMail = new Mail(-1,system.getId(),newAdminId, MailTypes.DEFAULT,-1,
                    "Congratulations! You have been promoted to admin by " + curUser.getUsername() + ". Keep up the good work!",new Date(),false);
            mailDB.add(promotionMail);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("profile.jsp?username="+newAdmin.getUsername());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        int punishedId = Integer.parseInt(request.getParameter("userToRemove"));
        UserDatabase userDB = getDatabase(Database.USER_DB,request);
        User punishedUser = null;
        try {
            userDB.banUser(punishedId);
            punishedUser = userDB.getById(punishedId);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("profile.jsp?username="+punishedUser.getUsername());
    }
}
