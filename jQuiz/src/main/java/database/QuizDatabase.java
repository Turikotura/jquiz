package database;

import models.Quiz;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizDatabase extends Database<Quiz>{
    static final String ID = "id";
    static final String TITLE = "title";
    static final String AUTHOR_ID = "author_id";
    static final String CREATED_AT = "created_at";
    static final String TIME = "time";
    static final String THUMBNAIL = "thumbnail";
    static final String THUMBNAIL_URL = "thumbnail_url";
    static final String SHOULD_MIX_UP = "should_mix_up";
    static final String SHOW_ALL = "show_all";
    static final String AUTO_CORRECT = "auto_correct";
    static final String ALLOW_PRACTICE = "allow_practice";
    static final String DESCRIPTION = "description";
    static final String CATEGORY = "category";
    static final String TOTAL_PLAY_COUNT = "total_play_count";
    static final String LAST_MONTH_PLAY_COUNT = "last_month_play_count";

    public QuizDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public int add(Quiz quiz) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s ) " +
                        "VALUES ( ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ? );",
                Database.QUIZ_DB, TITLE, AUTHOR_ID, CREATED_AT, TIME, THUMBNAIL, THUMBNAIL_URL, SHOULD_MIX_UP, SHOW_ALL, AUTO_CORRECT, ALLOW_PRACTICE, DESCRIPTION, CATEGORY);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        statement.setString(1, quiz.getTitle());
        statement.setInt(2, quiz.getAuthorId());
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        statement.setInt(4, quiz.getMaxTime());
        statement.setBytes(5, quiz.getThumbnail());
        statement.setString(6, quiz.getThumbnailUrl());
        statement.setBoolean(7, quiz.getShouldMixUp());
        statement.setBoolean(8, quiz.getShowAll());
        statement.setBoolean(9, quiz.getAutoCorrect());
        statement.setBoolean(10, quiz.getAllowPractice());
        statement.setString(11, quiz.getDescription());
        statement.setString(12, quiz.getCategory());

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
    protected Quiz getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        List<Integer> questionIds = new ArrayList<>();
        return new Quiz(
                rs.getInt(ID),
                rs.getString(TITLE),
                rs.getInt(AUTHOR_ID),
                rs.getDate(CREATED_AT),
                rs.getInt(TIME),
                rs.getBytes(THUMBNAIL),
                rs.getString(THUMBNAIL_URL),
                rs.getBoolean(SHOULD_MIX_UP),
                rs.getBoolean(SHOW_ALL),
                rs.getBoolean(AUTO_CORRECT),
                rs.getBoolean(ALLOW_PRACTICE),
                rs.getString(DESCRIPTION),
                rs.getString(CATEGORY),
                questionIds,
                rs.getInt(TOTAL_PLAY_COUNT),
                rs.getInt(LAST_MONTH_PLAY_COUNT)
        );
    }
    public List<Quiz> getQuizzesByAuthorId(int authorId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName, AUTHOR_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,authorId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<Quiz> getPopularQuizzes(int k, String totalOrLastMonth) throws SQLException, ClassNotFoundException {
        String lm = Objects.equals(totalOrLastMonth, "LAST_MONTH") ? LAST_MONTH_PLAY_COUNT : TOTAL_PLAY_COUNT;
        String query = String.format("SELECT * FROM %s ORDER BY " + lm + " DESC LIMIT %d;",
                databaseName,k);
        return queryToList(query, (ps) -> {return ps;});
    }

    public List<Quiz> getRecentlyCreatedQuizzes(int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT %d",
                databaseName, CREATED_AT, k);
        return queryToList(query, (ps) -> {return ps;});
    }
    public Quiz getQuizById(int id) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName,ID);
        return queryToElement(query,(ps) -> {
            try{
                ps.setInt(1,id);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public Quiz getQuizByTitle(String title) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName, TITLE);
        return queryToElement(query, (ps) -> {
            try {
                ps.setString(1,title);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<Quiz> searchRecentQuizzes(int k, String searchString) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s LIKE ? ORDER BY %s DESC LIMIT %d;",
                databaseName, TITLE, CREATED_AT, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, "%" + searchString + "%");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<Quiz> searchPopularQuizzes(int k, String totalOrLastMonth, String searchString) throws SQLException, ClassNotFoundException {
        String lm = Objects.equals(totalOrLastMonth, "LAST_MONTH") ? LAST_MONTH_PLAY_COUNT : TOTAL_PLAY_COUNT;
        String query = String.format("SELECT * FROM %s WHERE %s LIKE ? ORDER BY " + lm + " DESC LIMIT %d;",
                databaseName, TITLE, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, "%" + searchString + "%");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public void removeQuiz(int quizId) throws SQLException, ClassNotFoundException {
        String curStatement = String.format("DELETE FROM %s WHERE %s = ?;",
                QUIZ_DB, ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(curStatement, con);
        ps.setInt(1, quizId);
        ps.execute();
        con.close();
    }
    public List<Quiz> getRecentQuizzesByCategory(int k, String category) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC LIMIT %d;",
                databaseName, CATEGORY, CREATED_AT, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, category);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<Quiz> getPopularQuizzesByCategory(int k, String totalOrLastMonth, String category) throws SQLException, ClassNotFoundException {
        String lm = Objects.equals(totalOrLastMonth, "LAST_MONTH") ? LAST_MONTH_PLAY_COUNT : TOTAL_PLAY_COUNT;
        String query = String.format("SELECT * FROM %s WHERE %s = ? ORDER BY " + lm + " DESC LIMIT %d;",
                databaseName, CATEGORY, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, category);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
}