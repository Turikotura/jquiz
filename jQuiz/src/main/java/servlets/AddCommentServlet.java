package servlets;

import com.google.gson.Gson;
import database.CommentDatabase;
import database.Database;
import models.Comment;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static listeners.ContextListener.getDatabase;

public class AddCommentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doGet(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        // Add comment to a quiz
        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");
        int quizId = Integer.parseInt(httpServletRequest.getParameter("quizId"));
        String text = httpServletRequest.getParameter("comment-text");

        CommentDatabase commentdb = getDatabase(Database.COMMENT_DB,httpServletRequest);
        try {
            // Send the comment
            int commentId = commentdb.add(new Comment(-1,text,new Date(),curUser.getId(),quizId));

            // Get send comment info
            Comment comment = commentdb.getById(commentId);

            // Information to display on the page
            Map<String,String> respMap = new HashMap<>();
            respMap.put("username", curUser.getUsername());
            respMap.put("text", comment.getText());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDateString = dateFormat.format(comment.getWrittenTime());
            respMap.put("time", formattedDateString);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.getWriter().write(new Gson().toJson(respMap));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
