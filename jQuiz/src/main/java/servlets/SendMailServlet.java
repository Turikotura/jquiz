package servlets;

import database.Database;
import database.MailDatabase;
import database.QuizDatabase;
import database.UserDatabase;
import models.Mail;
import models.MailTypes;
import models.Quiz;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

import static listeners.ContextListener.getDatabase;

public class SendMailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        httpServletRequest.getServletContext().removeAttribute("error-log");
        httpServletResponse.sendRedirect("sendMail.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        // Get parameters
        String senderName = ((User) httpServletRequest.getSession().getAttribute("curUser")).getUsername();
        String receiverName = httpServletRequest.getParameter("receiver");
        MailTypes mailType = MailTypes.values()[Integer.parseInt(httpServletRequest.getParameter("mail-type"))];
        String text = httpServletRequest.getParameter("text");
        String quizName = "";
        if(mailType == MailTypes.CHALLENGE){
            quizName = httpServletRequest.getParameter("quiz-name");
        }

        // Get dbs
        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);
        QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,httpServletRequest);
        MailDatabase maildb = getDatabase(Database.MAIL_DB,httpServletRequest);

        try {
            User sender = userdb.getByUsername(senderName);

            User receiver = userdb.getByUsername(receiverName);
            if(receiver == null){
                // If receiver name is invalid
                String message = "User " + receiverName + " was not found";
                httpServletResponse.sendRedirect("sendMail.jsp?error-log="+ URLEncoder.encode(message,"UTF-8"));
                return;
            }
            Quiz quiz = null;
            if(mailType == MailTypes.CHALLENGE){
                quiz = quizdb.getQuizByTitle(quizName);
                if(quiz == null){
                    // If challenge type and invalid quiz name
                    String message = "Quiz " + quizName + " was not found";
                    httpServletResponse.sendRedirect("sendMail.jsp?error-log="+ URLEncoder.encode(message,"UTF-8"));
                    return;
                }
            }
            // Send mail
            int quizId = (quiz == null) ? -1 : quiz.getId();
            maildb.add(new Mail(-1,sender.getId(),receiver.getId(),mailType,quizId,text,(new Date()),false));
            httpServletResponse.sendRedirect("");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}