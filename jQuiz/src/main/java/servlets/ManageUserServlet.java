package servlets;

import database.*;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

import static listeners.ContextListener.getDatabase;

public class ManageUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        int newAdminId = Integer.parseInt(request.getParameter("userToPromote"));
        UserDatabase userDB = getDatabase(Database.USER_DB,request);
        User newAdmin = null;
        try {
            userDB.promoteUserToAdmin(newAdminId);
            newAdmin = userDB.getById(newAdminId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("profile.jsp?username="+punishedUser.getUsername());
    }
}
