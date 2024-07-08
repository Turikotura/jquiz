package servlets;

import models.*;
import database.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class QuizzesServlet extends HttpServlet {
    private static final int QUIZZES_PER_PAGE = 20;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page = 1;
        String match = null;
        String category = null;
        String tag = null;
        String sortBy = "";
        if(request.getParameter("page") != null){
            page = Integer.parseInt(request.getParameter("page"));
        }
        if(request.getParameter("match") != null && !request.getParameter("match").isEmpty()){
            match = request.getParameter("match");
        }
        if(request.getParameter("category") != null && !request.getParameter("category").isEmpty()){
            category = request.getParameter("category");
        }
        if(request.getParameter("tag") != null && !request.getParameter("tag").isEmpty()){
            tag = request.getParameter("tag");
        }
        if(request.getParameter("sortBy") != null && !request.getParameter("sortBy").isEmpty()){
            sortBy = request.getParameter("sortBy");
        }

        QuizDatabase quizDB = (QuizDatabase) getServletContext().getAttribute(Database.QUIZ_DB);
        List<Quiz> quizzes = null;
        int totalQuizzesCount;
        try {
            quizzes = quizDB.getQuizzesByOffset((page - 1) * QUIZZES_PER_PAGE, QUIZZES_PER_PAGE, match, category, tag, sortBy);
            totalQuizzesCount = quizDB.getTotalQuizCount(match, category, tag);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int totalPages = (int) Math.ceil(totalQuizzesCount / (double) QUIZZES_PER_PAGE);

        request.setAttribute("quizzes", quizzes);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/quizzes.jsp").forward(request, response);
    }
}
