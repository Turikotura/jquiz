package servlets;

import database.Database;
import database.HistoryDatabase;
import database.QuizDatabase;
import models.History;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static listeners.ContextListener.getDatabase;
import static listeners.ContextListener.getMailInfo;

public class HistorySummaryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        User curUser = (User) httpServletRequest.getSession().getAttribute("curUser");

        HistoryDatabase historyDB = getDatabase(Database.HISTORY_DB,httpServletRequest);
        QuizDatabase quizDB = getDatabase(Database.QUIZ_DB,httpServletRequest);

        getMailInfo(httpServletRequest);

        List<History> histories = new ArrayList<History>();
        Map<Integer,String> historyQuizNames = new HashMap<Integer,String>();
        if(curUser != null){
            try {
                histories = historyDB.getHistoryByUserId(curUser.getId());
                for(History history : histories){
                    historyQuizNames.put(history.getId(),quizDB.getById(history.getQuizId()).getTitle());
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        httpServletRequest.setAttribute("histories",histories);
        httpServletRequest.setAttribute("historyQuizNames",historyQuizNames);

        httpServletRequest.getRequestDispatcher("historySummary.jsp").forward(httpServletRequest,httpServletResponse);
    }
}