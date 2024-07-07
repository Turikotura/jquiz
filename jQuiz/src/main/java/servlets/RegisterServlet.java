package servlets;

import accounts.Register;
import accounts.Security;
import database.*;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

@MultipartConfig(maxFileSize = 16177215)
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html");
        String userName = request.getParameter("user-name"),
                email = request.getParameter("email"),
                password1 = request.getParameter("password1"),
                password2 = request.getParameter("password2");

        byte[] image = null;
        Part filePart = request.getPart("photo");
        if (filePart != null) {
            try (InputStream inputStream = filePart.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                image = outputStream.toByteArray();
            }
        }

        try {
            int result = Register.createNew(userName,email,password1,password2, (UserDatabase)request.getServletContext().getAttribute(Database.USER_DB));
            if(result == Register.USERNAME_EXISTS) request.getServletContext().setAttribute("reg-message","User named " + userName + " already exists.");
            else if(result == Register.EMAIL_EXISTS) request.getServletContext().setAttribute("reg-message","User with email " + email + " already exists.");
            else if(result == Register.DIFF_PASSWORDS) request.getServletContext().setAttribute("reg-message","Passwords don't match.");
            else if(result == Register.WEAK_PASSWORD) request.getServletContext().setAttribute("reg-message","Password is not strong enough.\n" +
                                                                                                                    "Password contain minimum of 8 symbols.\n" +
                                                                                                                    "Password must contain at least one uppercase letter, one lowercase letter and one digit.");
            else if(result == Register.EMAIL_BANNED) request.getServletContext().setAttribute("reg-message","You have been banned for violating our policies.");
            else {
                User newUser = new User(User.NO_ID,userName,new Date(),email, Security.getHash(password1),image,null);
                ((UserDatabase)request.getServletContext().getAttribute(Database.USER_DB)).add(newUser);
                newUser = ((UserDatabase)request.getServletContext().getAttribute(Database.USER_DB)).getByUsername(newUser.getUsername());
                request.getSession().setAttribute("curUser",newUser);
            }

            if(result == Register.SUCCESS) response.sendRedirect("");
            else response.sendRedirect("register.jsp");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}