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
            User curUser = ((UserDatabase)request.getServletContext().getAttribute(Database.USER_DB)).getByUsername(userName);
            if(curUser != null) {
                request.getServletContext().setAttribute("reg-message","User named " + userName + " already exists.");
                response.sendRedirect("register.jsp");
            } else if(!password1.equals(password2)) {
                request.getServletContext().setAttribute("reg-message","Passwords don't match.");
                response.sendRedirect("register.jsp");
            } else if(!Security.isStrong(password1)) {
                request.getServletContext().setAttribute("reg-message","Password is not strong enough.\n" +
                        "Password contain minimum of 8 symbols.\n" +
                        "Password must contain at least one uppercase letter, one lowercase letter and one digit.");
                response.sendRedirect("register.jsp");
            } else {
                User newUser = new User(User.NO_ID,userName,new Date(),email,Security.getHash(password1),imageLink);
                ((UserDatabase)request.getServletContext().getAttribute(Database.USER_DB)).add(newUser);
                request.getSession().setAttribute("curUser",userName);
                response.sendRedirect("");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
}