package servlets;

import database.Database;
import database.MailDatabase;
import database.UserDatabase;
import models.Mail;
import models.MailTypes;
import models.User;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static listeners.ContextListener.getDatabase;

public class ReportServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        UserDatabase userDB = getDatabase(Database.USER_DB,request);
        MailDatabase mailDB = getDatabase(Database.MAIL_DB,request);
        int quizId = Integer.parseInt(request.getParameter("quizId"));
        String reportText = request.getParameter("report-text");
        User reportAuthor = (User) request.getSession().getAttribute("curUser");

        try {
            List<User> admins = userDB.getAllAdmins();
            for(User curAdmin : admins) {
                Mail newMessage = new Mail(-1,reportAuthor.getId(),curAdmin.getId(), MailTypes.QUIZ_REPORT,quizId,
                        String.format("Report by %s:\n%s", reportAuthor.getUsername(), reportText),new Date(),false);
                mailDB.add(newMessage);
            }

            User system = userDB.getByUsername("System");
            mailDB.add(new Mail(-1,system.getId(),reportAuthor.getId(),MailTypes.DEFAULT,-1,
                    "Thanks for writing the report! We are currently looking into it.",new Date(),false));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        response.sendRedirect("quizInfo.jsp?quizId="+quizId);
    }
}
