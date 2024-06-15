package database;

import models.History;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDatabase extends Database<History> {
    public static final String USER_ID_COL = "user_id";
    public static final String QUIZ_ID_COL = "quiz_id";
    public static final String GRADE_COL = "grade";
    public static final String COMPLETED_AT_COL = "completed_at";
    public static final String WRITING_TIME_COL = "writing_time";
    public HistoryDatabase(BasicDataSource dataSource, String databaseName){
        super(dataSource,databaseName);
    }
    @Override
    public boolean add(History toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s ) VALUES ( %d, %d, %d, %t, %d )", databaseName,
                USER_ID_COL, QUIZ_ID_COL, GRADE_COL, COMPLETED_AT_COL, WRITING_TIME_COL,
                toAdd.getUserId(), toAdd.getQuizId(), toAdd.getGrade(), toAdd.getCompletedAt(), toAdd.getWritingTime());
        PreparedStatement statement = getStatement(query);
        return statement.executeUpdate() > 0;
    }

    @Override
    protected History getItemFromResultSet(ResultSet rs) throws SQLException {
        return new History(
                rs.getInt("id"),
                rs.getInt(USER_ID_COL),
                rs.getInt(QUIZ_ID_COL),
                rs.getInt(GRADE_COL),
                rs.getDate(COMPLETED_AT_COL),
                rs.getInt(WRITING_TIME_COL)
        );
    }
    public List<History> getHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<History> histories = new ArrayList<History>();
        ResultSet rsHistories = getResultSet("SELECT * FROM " + Database.HISTORY_DB + " WHERE user_id = " + userId);
        while (rsHistories.next()) {
            histories.add(getItemFromResultSet(rsHistories));
        }
        return histories;
    }
    public History getLastHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        ResultSet rsHistories = getResultSet(
                " SELECT * " +
                        " FROM " + Database.HISTORY_DB +" h" +
                        " WHERE h.user_id = " + userId +
                        " AND h.completed_at = " +
                        " (SELECT max(hi.completed_at) FROM " + Database.HISTORY_DB + " hi WHERE hi.user_id = " + userId + ")");
        return new History(
                rsHistories.getInt("id"),
                rsHistories.getInt(USER_ID_COL),
                rsHistories.getInt(QUIZ_ID_COL),
                rsHistories.getInt(GRADE_COL),
                rsHistories.getDate(COMPLETED_AT_COL),
                rsHistories.getInt(WRITING_TIME_COL)
        );
    }
    public List<History> getHistoryByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        List<History> histories = new ArrayList<>();
        ResultSet rsHistories = getResultSet(
                " SELECT * " +
                        " FROM " + Database.HISTORY_DB +" h" +
                        " WHERE h.user_id = " + userId +
                        " AND h.quiz_id = " + quizId);
        while (rsHistories.next()) {
            History history = new History(
                    rsHistories.getInt("id"),
                    rsHistories.getInt(USER_ID_COL),
                    rsHistories.getInt(QUIZ_ID_COL),
                    rsHistories.getInt(GRADE_COL),
                    rsHistories.getDate(COMPLETED_AT_COL),
                    rsHistories.getInt(WRITING_TIME_COL)
            );

            histories.add(history);
        }
        return histories;
    }
}
