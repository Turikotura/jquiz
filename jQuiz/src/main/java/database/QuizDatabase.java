package database;

import models.Quiz;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QuizDatabase extends Database<Quiz>{
    // Constants for column names
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

    /**
     * Adds new entry to quizzes table
     * @param quiz Quiz Object describing new row
     * @return id of the new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Assembles Quiz object from ResultSet
     * @param rs ResultSet of quizzes table rows
     * @return Quiz Object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    public List<Quiz> getQuizzesByOffset(int offset, int limit, String match, String category, String tag, String sortBy) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT q.* FROM %s q ", databaseName);
        if(tag!=null && !tag.isEmpty()){
            query+=String.format("LEFT JOIN %s qt ON q.id = qt.quiz_id " +
                                    "LEFT JOIN %s t ON qt.tag_id = t.id ",
                                    Database.TAG_TO_QUIZ_DB, Database.TAG_DB);
        }
        List<String> conditions = new ArrayList<>();
        if(match != null && !match.isEmpty()){
            conditions.add("q.title LIKE ?");
        }
        if(category != null && !category.isEmpty()){
            conditions.add("q.category = ?");
        }
        if(tag != null && !tag.isEmpty()){
            conditions.add("t.name = ?");
        }

        if(!conditions.isEmpty()){
            query += "WHERE " + String.join(" AND ", conditions) + " ";
        }

        switch (sortBy) {
            case "TOTAL":
                query += "ORDER BY q.total_play_count DESC ";
                break;
            case "LAST_MONTH":
                query += "ORDER BY q.last_month_play_count DESC ";
                break;
            case "NEWEST":
                query += "ORDER BY q.created_at DESC ";
                break;
            default:
                query += "ORDER BY q.created_at DESC ";
                break;
        }

        query += "LIMIT ? OFFSET ?";

        return queryToList(query, (ps) -> {
            try {
                int paramIndex = 1;
                if (match != null && !match.isEmpty()) {
                    ps.setString(paramIndex++, "%" + match + "%");
                }
                if (category != null && !category.isEmpty()) {
                    ps.setString(paramIndex++, category);
                }
                if (tag != null && !tag.isEmpty()) {
                    ps.setString(paramIndex++, tag);
                }
                ps.setInt(paramIndex++, limit);
                ps.setInt(paramIndex, offset);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    /**
     * Get amount of quizzes the website hosts
     * @return amount of quizzes the website hosts
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int getTotalQuizCount(String match, String category, String tag) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT COUNT(*) AS total_count FROM %s q ", databaseName);
        if(tag!=null && !tag.isEmpty()){
            query+=String.format("LEFT JOIN %s qt ON q.id = qt.quiz_id " +
                            "LEFT JOIN %s t ON qt.tag_id = t.id ",
                    Database.TAG_TO_QUIZ_DB, Database.TAG_DB);
        }
        List<String> conditions = new ArrayList<>();
        if(match != null && !match.isEmpty()){
            conditions.add("q.title LIKE ?");
        }
        if(category != null && !category.isEmpty()){
            conditions.add("q.category = ?");
        }
        if(tag != null && !tag.isEmpty()){
            conditions.add("t.name = ?");
        }

        if(!conditions.isEmpty()){
            query += "WHERE " + String.join(" AND ", conditions) + " ";
        }
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        int paramIndex = 1;
        if (match != null && !match.isEmpty()) {
            ps.setString(paramIndex++, "%" + match + "%");
        }
        if (category != null && !category.isEmpty()) {
            ps.setString(paramIndex++, category);
        }
        if (tag != null && !tag.isEmpty()) {
            ps.setString(paramIndex, tag);
        }
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getInt("total_count");
    }
    /**
     * Get amount of quizzes the website hosts
     * @return amount of quizzes the website hosts
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int getTotalQuizCount() throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT COUNT(*) AS total_count FROM %s;",Database.QUIZ_DB);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getInt("total_count");
    }
    /**
     * Get quizzes created by user specified
     * @param authorId Id of the author
     * @return List of every quiz created by the user
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get k most popular quizzes of all time or from last month
     * @param k maximum number iq quizzes returned
     * @param totalOrLastMonth equals LAST_MONTH if caller wants popular quizzes from the last month
     * @return List of Quiz objects that are most popular
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Quiz> getPopularQuizzes(int k, String totalOrLastMonth) throws SQLException, ClassNotFoundException {
        String lm = Objects.equals(totalOrLastMonth, "LAST_MONTH") ? LAST_MONTH_PLAY_COUNT : TOTAL_PLAY_COUNT;
        String query = String.format("SELECT * FROM %s ORDER BY " + lm + " DESC LIMIT %d;",
                databaseName,k);
        return queryToList(query, (ps) -> {return ps;});
    }

    /**
     * Get k most recently created quizzes
     * @param k maximum number of quizzes returned
     * @return List of most recently created quizzes with maximum length of k
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Quiz> getRecentlyCreatedQuizzes(int k) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT %d",
                databaseName, CREATED_AT, k);
        return queryToList(query, (ps) -> {return ps;});
    }

    /**
     * Get quiz with the id specified
     * @param id Id of the quiz
     * @return Quiz object with the id passed
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Gets k most recent quizzes, such that it's name contains searchString as substring, ordered from most to least recent
     * @param k Maximum number of quizzes returned
     * @param searchString String we are searching by
     * @return List of Quiz Objects, such that it's name contains searchString as substring, ordered from most to least recent
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Gets k most popular quizzes from last month or all time that contain the search string int the title
     * @param k maximum number of quizzes returned
     * @param totalOrLastMonth Is LAST_MONTH if caller wants quizzes from last month
     * @param searchString String we are searching by
     * @return List of k most popular quizzes that contain search string in the title
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Deletes the quiz specified
     * @param quizId Id of the quiz to be deleted
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void removeQuiz(int quizId) throws SQLException, ClassNotFoundException {
        String curStatement = String.format("DELETE FROM %s WHERE %s = ?;",
                QUIZ_DB, ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(curStatement, con);
        ps.setInt(1, quizId);
        ps.execute();
        con.close();
    }

    /**
     * Get k most recent quizzes from category specified
     * @param k Maximum number of quizzes returned
     * @param category Name of the category
     * @return List of most recent quizzes from category passed
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get k most popular quizzes from last month or all time from category specified
     * @param k Maximum number of quizzes returned
     * @param totalOrLastMonth Is LAST_MONTH if caller wants quizzes from last month
     * @param category name of the category
     * @return List of most popular quizzes of last month or all time from category specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get quizzes with tag specified
     * @param tagName Name of the tag
     * @return List of quizzes with tag specified
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Quiz> getQuizzesByTagName(String tagName) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT q.* FROM %s q " +
                        "JOIN %s qt ON q.id = qt.quiz_id " +
                        "JOIN %s t ON qt.tag_id = t.id " +
                        "WHERE t.name = ?",
                databaseName, Database.TAG_TO_QUIZ_DB, Database.TAG_DB
        );

        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, tagName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
}