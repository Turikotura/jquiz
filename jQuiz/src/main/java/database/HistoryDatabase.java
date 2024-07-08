package database;

import models.History;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryDatabase extends Database<History> {
    public static final String ID = "id";
    public static final String USER_ID = "user_id";
    public static final String QUIZ_ID = "quiz_id";
    public static final String GRADE = "grade";
    public static final String COMPLETED_AT = "completed_at";
    public static final String WRITING_TIME = "writing_time";
    public static final String IS_PRACTICE = "is_practice";
    public HistoryDatabase(BasicDataSource dataSource, String databaseName){
        super(dataSource,databaseName);
    }
    @Override
    public int add(History toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s, %s, %s, %s ) " +
                        "VALUES ( ?,  ?,  ?,  ?,  ?,  ?  );",
                databaseName, USER_ID, QUIZ_ID, GRADE, COMPLETED_AT, WRITING_TIME, IS_PRACTICE);
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
        statement.setInt(1,toAdd.getUserId());
        statement.setInt(2,toAdd.getQuizId());
        statement.setInt(3,toAdd.getGrade());
        statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
        statement.setInt(5, toAdd.getWritingTime());
        statement.setBoolean(6,toAdd.getIsPractice());

        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Creating row failed");
        }
        try(ResultSet keys = statement.getGeneratedKeys()){
            if(keys.next()){
                int res = keys.getInt(1);
                con.close();
                return res;
            }else{
                throw new SQLException("Creating row failed");
            }
        }
    }

    @Override
    protected History getItemFromResultSet(ResultSet rs) throws SQLException {
        return new History(
                rs.getInt(ID),
                rs.getInt(USER_ID),
                rs.getInt(QUIZ_ID),
                rs.getInt(GRADE),
                rs.getTimestamp(COMPLETED_AT),
                rs.getInt(WRITING_TIME),
                rs.getBoolean(IS_PRACTICE)
        );
    }
    public int getTotalAttemptCount() throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT COUNT(*) AS total_count FROM %s;",Database.HISTORY_DB);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getInt("total_count");
    }
    public List<History> getHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC",
                databaseName, USER_ID, COMPLETED_AT);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public History getLastHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s h WHERE h.%s = ? AND h.%s = (SELECT max(hi.%s) FROM %s hi WHERE hi.%s = ?)",
                databaseName, USER_ID, COMPLETED_AT, COMPLETED_AT, databaseName, USER_ID);
        return queryToElement(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public History getLastHistoryByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s h WHERE h.%s = ? AND h.%s = ? AND h.%s = (SELECT max(hi.%s) FROM %s hi WHERE hi.%s = ? AND hi.%s = ?)",
                databaseName, USER_ID, QUIZ_ID, COMPLETED_AT, COMPLETED_AT, databaseName, USER_ID, QUIZ_ID);
        return queryToElement(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,quizId);
                ps.setInt(3,userId);
                ps.setInt(4,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<History> getHistoryByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? ORDER BY %s DESC",
                databaseName, USER_ID, QUIZ_ID, COMPLETED_AT);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<History> getLatestHistoriesByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? ORDER BY %s DESC;",
                databaseName, USER_ID, QUIZ_ID, COMPLETED_AT);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<History> getBestScoresByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? ORDER BY %s DESC, %s;",
                databaseName, USER_ID, QUIZ_ID, GRADE, WRITING_TIME);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<History> getFastestByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? ORDER BY %s, %s DESC;",
                databaseName, USER_ID, QUIZ_ID, WRITING_TIME, GRADE);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<History> getLatestHistories(int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT %d",
                databaseName, COMPLETED_AT, k);
        return queryToList(query, (ps) -> {return ps;});
    }
    public List<History> getLatestHistoriesByQuizId(int quizId, int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC LIMIT %d",
                databaseName, QUIZ_ID, COMPLETED_AT, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<History> getHistoryByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC",
                databaseName, QUIZ_ID, COMPLETED_AT);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<History> getLatestHistoriesByUserId(int userId, int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC LIMIT %d",
                databaseName, USER_ID, COMPLETED_AT, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public History getBestScoreHistoryByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "Select * from %s h where h.%s = ? " +
                "and h.%s = (Select max(hi.%s) from %s hi where hi.%s = ?) " +
                "order by %s",
                databaseName, QUIZ_ID, GRADE, GRADE, databaseName, QUIZ_ID, WRITING_TIME);
        return queryToElement(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
                ps.setInt(2,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public History getBestHistoryByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ? AND %s = ? ORDER BY %s DESC, %s LIMIT 1;",
                databaseName, USER_ID, QUIZ_ID, GRADE, WRITING_TIME);
        return queryToElement(query, (ps) -> {
            try {
                ps.setInt(1,userId);
                ps.setInt(2,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<History> getTopPerformersByQuizId(int quizId, int k) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC, %s LIMIT %d;",
                databaseName, QUIZ_ID, GRADE, WRITING_TIME, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<History> getTopInLastDayByQuizId(int quizId, int k) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ? AND %s >= NOW() - INTERVAL 1 DAY ORDER BY %s DESC, %s LIMIT %d;",
                databaseName, QUIZ_ID, COMPLETED_AT, GRADE, WRITING_TIME, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public void clearQuizHistory(int quizId) throws SQLException, ClassNotFoundException {
        String curStatement = String.format("DELETE FROM %s WHERE %s = ?;",
                HISTORY_DB,QUIZ_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(curStatement,con);
        ps.setInt(1,quizId);
        ps.execute();
        con.close();
    }
}