import database.Database;
import database.UserDatabase;
import models.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        String userName = request.getParameter("user-name"), password = request.getParameter("password");
        password = Security.getHash(password);
        try {
            User curUser = ((UserDatabase)request.getServletContext().getAttribute(Database.USER_DB)).getByUsername(userName);
            if(curUser == null) {
                request.getServletContext().setAttribute("log-message","User named " + userName + " doesn't exist.");
                response.sendRedirect("login.jsp");
            } else if(password.equals(curUser.getPassword())) {
                response.sendRedirect("index.jsp");
            } else {
                request.getServletContext().setAttribute("log-message","Password incorrect.");
                response.sendRedirect("login.jsp");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
