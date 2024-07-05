package database;

import models.Rating;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;

public class RatingDatabase extends Database<Rating> {
    public static final String ID = "id";
    public static final String RATING = "rating";
    public static final String QUIZ_ID = "quiz_id";
    public static final String USER_ID = "user_id";
    public RatingDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }
    @Override
    public int add(Rating toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s ) " +
                        "VALUES ( ?,  ?,  ?  )",
                databaseName, RATING, QUIZ_ID, USER_ID);
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
        statement.setInt(1,toAdd.getRating());
        statement.setInt(2,toAdd.getQuizId());
        statement.setInt(3,toAdd.getUserId());

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
    protected Rating getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Rating(
                rs.getInt(ID),
                rs.getInt(RATING),
                rs.getInt(QUIZ_ID),
                rs.getInt(USER_ID)
        );
    }

    public Rating getRatingByQuizAndUserId(int quizId, int userId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ? AND %s = ?",
                databaseName, QUIZ_ID, USER_ID
        );
        return queryToElement(query, (ps) -> {
            try {
                ps.setInt(1,quizId);
                ps.setInt(2,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
}
