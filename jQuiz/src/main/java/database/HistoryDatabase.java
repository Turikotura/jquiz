package database;

import models.History;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryDatabase extends Database<History> {
    // Constants for column names
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

    /**
     * Adds new entry to history table
     * @param toAdd History Object describing new row
     * @return id of the new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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
        statement.setTimestamp(4, new java.sql.Timestamp(toAdd.getCompletedAt().getTime()));
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

    /**
     * Assembles History object from ResultSet
     * @param rs ResultSet of history table rows
     * @return History object
     * @throws SQLException
     */
    @Override
    protected History getItemFromResultSet(ResultSet rs) throws SQLException {
        return new History(
                rs.getInt(ID),
                rs.getInt(USER_ID),
                rs.getInt(QUIZ_ID),
                rs.getInt(GRADE),
                rs.getDate(COMPLETED_AT),
                rs.getInt(WRITING_TIME),
                rs.getBoolean(IS_PRACTICE)
        );
    }

    /**
     * Get entire history of user passed
     * @param userId Id of the user
     * @return List of History objects which belong to the user
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<History> getHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName, USER_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    /**
     * Get the last time user wrote the quiz specified
     * @param userId Id of the user
     * @param quizId Id of the quiz
     * @return History object of last instance user wrote the quiz specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get every instance of user taking the quiz specified
     * @param userId Id of the user
     * @param quizId Id of the quiz
     * @return List of every History object where user took the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<History> getHistoryByUserAndQuizId(int userId, int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
                databaseName, USER_ID, QUIZ_ID);
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

    /**
     * Get every instance of user taking the quiz specified ordered from most to least recent
     * @param userId Id of the user
     * @param quizId Id of the quiz
     * @return List of every History object where user took the quiz ordered from most to least recent
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get user's attempts of quiz ordered from best to worst
     * @param userId Id of the user
     * @param quizId Id of the quiz
     * @return List of every History object where user took the quiz ordered from best to worst
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get user's attempts of quiz ordered from fastest to slowest
     * @param userId Id of the user
     * @param quizId Id of the quiz
     * @return List of every History object where user took the quiz ordered from fastest to slowest
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get last k times someone took the quiz specified ordered from most to least recent
     * @param quizId Id of the quiz
     * @param k Amount of rows returned
     * @return List of History objects with max length of k, of most recent times someone took the quiz specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get the last k times the user took a quiz from most to least recent
     * @param userId Id of the user
     * @param k Maximum number of rows returned
     * @return List of History objects with max length of k, of most recent times user took a quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get entry for the best attempt for the quiz specified
     * @param quizId Id of the quiz
     * @return History object of the best attempt for the quiz specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get best attempt user has had on a quiz
     * @param userId Id of the user
     * @param quizId Id of the quiz
     * @return History object for the best attempt user has had on the quiz specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get the best k attempts of all time for the quiz specified
     * @param quizId Id of the quiz
     * @param k Maximum number of entries
     * @return List of History objects for best k attempts on a quiz, ordered from best to worst
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get best k attempts from the last day for the quiz specified, ordered from best to worst
     * @param quizId Id of the quiz
     * @param k Maximum number of entries
     * @return List of History objects for best k attempts on a quiz from the last day, ordered from best to worst
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Clear all history entries for the quiz specified
     * @param quizId Id of the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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