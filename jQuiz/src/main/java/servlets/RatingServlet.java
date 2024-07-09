package servlets;

import database.Database;
import database.RatingDatabase;
import models.Rating;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.sql.SQLException;

import static listeners.ContextListener.getDatabase;

public class RatingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        super.doGet(httpServletRequest, httpServletResponse);
    }

    @Override
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        int rating = Integer.parseInt(httpServletRequest.getParameter("rating"));
        int quizId = Integer.parseInt(httpServletRequest.getParameter("quizId"));
        int userId = ((User) httpServletRequest.getSession().getAttribute("curUser")).getId();

        RatingDatabase ratingdb = getDatabase(Database.RATING_DB,httpServletRequest);
        try {
            Rating r = ratingdb.getRatingByQuizAndUserId(quizId,userId);
            if(r != null){
                // If has rating by user, update
                ratingdb.setFieldById(r.getId(),RatingDatabase.RATING,rating);
            }else{
                // If doesn't have rating by user, add
                ratingdb.add(new Rating(-1,rating,quizId,userId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}