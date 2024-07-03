package servlets;

import database.AnnouncementDatabase;
import database.Database;
import models.Announcement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import static listeners.ContextListener.getDatabase;

public class AnnouncementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        int authorId = Integer.parseInt(request.getParameter("authorId"));
        String title = request.getParameter("title"), text = request.getParameter("text");
        AnnouncementDatabase announcementDB = getDatabase(Database.ANNOUNCEMENT_DB,request);
        try {
            announcementDB.add(new Announcement(-1,authorId,title,text,new Date()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        response.sendRedirect("");
    }
}
