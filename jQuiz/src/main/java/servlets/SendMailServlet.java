package servlets;

import database.Database;
import database.MailDatabase;
import database.QuizDatabase;
import database.UserDatabase;
import models.Mail;
import models.MailTypes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;

import static listeners.ContextListener.getDatabase;

public class SendMailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doGet(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String receiverName = httpServletRequest.getParameter("receiver");
        MailTypes mailType = MailTypes.values()[Integer.parseInt(httpServletRequest.getParameter("mail-type"))];
        String text = httpServletRequest.getParameter("text");
        String quizName = "";
        if(mailType == MailTypes.CHALLENGE){
            quizName = httpServletRequest.getParameter("quiz-name");
        }

        UserDatabase userdb = getDatabase(Database.USER_DB,httpServletRequest);
        QuizDatabase quizdb = getDatabase(Database.QUIZ_DB,httpServletRequest);
        MailDatabase maildb = getDatabase(Database.MAIL_DB,httpServletRequest);


    }
}