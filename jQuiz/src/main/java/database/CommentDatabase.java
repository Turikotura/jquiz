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
}