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
    public void init() throws ServletException {
        System.out.println("HI");
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/quizDB");
        dataSource.setUsername(DBInfo.USERNAME);
        dataSource.setPassword(DBInfo.PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(50);
        dataSource.setMaxIdle(20);
        dataSource.setMinIdle(5);
        dataSource.setMaxWaitMillis(10000);
        quizDB = new QuizDatabase(dataSource, Database.QUIZ_DB);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
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
