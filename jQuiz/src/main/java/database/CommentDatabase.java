package database;

import models.Comment;
import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.List;

public class CommentDatabase extends Database<Comment>{
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String WRITTEN_TIME = "written_time";
    public static final String USER_ID = "user_id";
    public static final String QUIZ_ID = "quiz_id";

    public CommentDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Adds new comment to comments table
     * @param toAdd new Comment object
     * @return id of new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    public int add(Comment toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s, %s ) " +
                        "VALUES ( ?,  ?,  ?,  ?  )",
                databaseName, TEXT, WRITTEN_TIME, USER_ID, QUIZ_ID);
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
        statement.setString(1, toAdd.getText());
        statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        statement.setInt(3, toAdd.getUserId());
        statement.setInt(4, toAdd.getQuizId());

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
     * Assembles Comment object from ResultSet
     * @param rs ResultSet of comments table rows
     * @return Comment object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Comment getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Comment(
                rs.getInt(ID),
                rs.getString(TEXT),
                rs.getTimestamp(WRITTEN_TIME),
                rs.getInt(USER_ID),
                rs.getInt(QUIZ_ID)
        );
    }

    /**
     * Returns cooments of quiz passed
     * @param quizId Id of the quiz
     * @return List of Comments left on the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Comment> getCommentsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ? ORDER BY %s DESC",
                databaseName, QUIZ_ID, WRITTEN_TIME);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public void removeCommentsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ?",
                databaseName, QUIZ_ID
        );
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setInt(1,quizId);
        ps.execute();
        con.close();
    }
}