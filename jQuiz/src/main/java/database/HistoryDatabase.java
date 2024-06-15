package database;

import models.History;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDatabase extends Database<History> {
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String QUIZ_ID = "quiz_id";
    public static final String GRADE = "grade";
    public static final String COMPLETED_AT = "completed_at";
    public static final String WRITING_TIME = "writing_time";
    public HistoryDatabase(BasicDataSource dataSource, String databaseName){
        super(dataSource,databaseName);
    }
    @Override
    public boolean add(History toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s ) VALUES ( %d, %d, %d, %s, %d )", databaseName,
                USER_ID, QUIZ_ID, GRADE, COMPLETED_AT, WRITING_TIME,
                toAdd.getUserId(), toAdd.getQuizId(), toAdd.getGrade(), toAdd.getCompletedAt(), toAdd.getWritingTime());
        PreparedStatement statement = getStatement(query);
        return statement.executeUpdate() > 0;
    }

    @Override
    protected History getItemFromResultSet(ResultSet rs) throws SQLException {
        return new History(
                rs.getInt(ID),
                rs.getInt(USER_ID),
                rs.getInt(QUIZ_ID),
                rs.getInt(GRADE),
                rs.getDate(COMPLETED_AT),
                rs.getInt(WRITING_TIME)
        );
    }
    public List<History> getHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                databaseName, USER_ID, userId);
        return queryToList(query);
    }
    public History getLastHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s h WHERE h.%s = %d AND h.%s = (SELECT max(hi.%s) FROM %s hi WHERE hi.%s = %d)",
                databaseName, USER_ID, userId, COMPLETED_AT, COMPLETED_AT, databaseName, USER_ID, userId);
        ResultSet rsHistories = getResultSet(query);
        rsHistories.next();
        return getItemFromResultSet(rsHistories);
    }
    public List<History> getHistoryByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d",
                databaseName, USER_ID, userId, QUIZ_ID, quizId);
        return queryToList(query);
    }
    public List<History> getLatestHistories(int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT %d",
                databaseName, COMPLETED_AT, k);
        return queryToList(query);
    }
    public List<History> getHistoryByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                databaseName, QUIZ_ID, quizId);
        return queryToList(query);
    }
}
