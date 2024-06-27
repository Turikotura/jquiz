package servlets;

import database.DBInfo;
import database.Database;
import database.QuizDatabase;
import database.UserDatabase;
import models.Quiz;
import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static listeners.ContextListener.getDatabase;

public class SearchServlet extends HttpServlet {
    private BasicDataSource dataSource;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String searchString = request.getParameter("searchString");

            QuizDatabase quizDB = getDatabase(Database.QUIZ_DB, request);
            UserDatabase userDB = getDatabase(Database.USER_DB, request);

            List<Quiz> foundQuizzes = new ArrayList<>();
            List<User> foundUsers = new ArrayList<>();

            if (searchString != null && !searchString.trim().isEmpty()) {
                foundQuizzes = quizDB.searchQuizzes(searchString);
                foundUsers = userDB.searchUsers(searchString);
            }
            request.setAttribute("foundQuizzes", foundQuizzes);
            request.setAttribute("foundUsers", foundUsers);
            System.out.println(foundUsers + " WOW");
            request.getRequestDispatcher("search.jsp").forward(request, response);

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
