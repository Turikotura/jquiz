package servlets;

import accounts.Register;
import accounts.Security;
import database.*;
import models.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String userName = request.getParameter("user-name"),
                email = request.getParameter("email"),
                password1 = request.getParameter("password1"),
                password2 = request.getParameter("password2"),
                imageLink = request.getParameter("profile-pic");

        try {
            int result = Register.createNew(userName,email,password1,password2,imageLink,(UserDatabase)request.getServletContext().getAttribute(Database.USER_DB));
            if(result == Register.USERNAME_EXISTS) {
                request.getServletContext().setAttribute("reg-message","User named " + userName + " already exists.");
                response.sendRedirect("register.jsp");
            } else if(result == Register.EMAIL_EXISTS) {
                request.getServletContext().setAttribute("reg-message","User with email " + email + " already exists.");
                response.sendRedirect("register.jsp");
            } else if(result == Register.DIFF_PASSWORDS) {
                request.getServletContext().setAttribute("reg-message","Passwords don't match.");
                response.sendRedirect("register.jsp");
            } else if(result == Register.WEAK_PASSWORD) {
                request.getServletContext().setAttribute("reg-message","Password is not strong enough.\n" +
                        "Password contain minimum of 8 symbols.\n" +
                        "Password must contain at least one uppercase letter, one lowercase letter and one digit.");
                response.sendRedirect("register.jsp");
            } else {
                request.getSession().setAttribute("curUser",new User(User.NO_ID,userName,new Date(),email, Security.getHash(password1),imageLink));
                response.sendRedirect("");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}