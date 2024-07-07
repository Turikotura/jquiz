package servlets;

import accounts.Security;
import accounts.Login;
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
        try {
            int result = Login.login(userName,password,(UserDatabase) request.getServletContext().getAttribute(Database.USER_DB));
            if(result == Login.NO_USER) request.getServletContext().setAttribute("log-message","User named " + userName + " doesn't exist.");
            else if(result == Login.WRONG_PASSWORD) request.getServletContext().setAttribute("log-message","Password incorrect.");
            else if (result == Login.USER_BANNED) request.getServletContext().setAttribute("log-message","You have been banned for violating our policies.");
            else request.getSession().setAttribute("curUser",((UserDatabase) request.getServletContext().getAttribute(Database.USER_DB)).getByUsername(userName));

            if(result == Login.SUCCESS) response.sendRedirect("");
            else response.sendRedirect("login.jsp");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        request.getSession().removeAttribute("curUser");
        response.sendRedirect("");
    }
}
