package servlets;

import database.DBInfo;
import database.Database;
import database.QuizDatabase;
import models.Quiz;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FrontPageServlet extends HttpServlet {
    private BasicDataSource dataSource;
    private QuizDatabase quizDB;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            QuizDatabase quizDB = (QuizDatabase) getServletContext().getAttribute(Database.QUIZ_DB);
            List<Quiz> recentQuizzes = quizDB.getRecentlyCreatedQuizzes(5);
            request.setAttribute("recentQuizzes", recentQuizzes);
            List<Quiz> popularQuizzes = quizDB.getPopularQuizzes(5, "TOTAL");
            request.setAttribute("popularQuizzes", popularQuizzes);
            List<Quiz> lastMonthPopularQuizzes = quizDB.getPopularQuizzes(5, "LAST_MONTH");
            request.setAttribute("lastMonthPopularQuizzes", lastMonthPopularQuizzes);
            request.getRequestDispatcher("index.jsp").forward(request, response);

        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
