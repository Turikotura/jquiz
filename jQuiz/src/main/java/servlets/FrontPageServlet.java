package servlets;

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
        System.out.println("HAIII");
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/quizDB");
        dataSource.setUsername("root");
        dataSource.setPassword("");
        quizDB = new QuizDatabase(dataSource, Database.QUIZ_DB);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            System.out.println("NUGGA");
            List<Quiz> quizzes = quizDB.getAll();
            System.out.println(quizzes.size() + " NIGGA");
            request.setAttribute("quizzes", quizzes);
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
