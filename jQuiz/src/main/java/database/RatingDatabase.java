package database;

import models.Rating;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.List;

public class RatingDatabase extends Database<Rating> {
    // Constants for column names
    public static final String ID = "id";
    public static final String RATING = "rating";
    public static final String QUIZ_ID = "quiz_id";
    public static final String USER_ID = "user_id";
    public RatingDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Adds new entry to ratings table
     * @param toAdd Rating Object describing new row
     * @return id of the new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Assembles Rating object from ResultSet
     * @param rs ResultSet of ratings table rows
     * @return rating Object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Rating getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Rating(
                rs.getInt(ID),
                rs.getInt(RATING),
                rs.getInt(QUIZ_ID),
                rs.getInt(USER_ID)
        );
    }

    /**
     * Get rating user left on the quiz specified
     * @param quizId Id of the quiz
     * @param userId Id of the user
     * @return Rating user left on the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
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

    /**
     * Get every rating left on the quiz
     * @param quizId Id of the quiz
     * @return List of Rating objects left on the quiz
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Rating> getRatingsByQuizId(int quizId) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                databaseName, QUIZ_ID
        );
        return queryToList(query, (ps) -> {
           try{
               ps.setInt(1,quizId);
           }catch (SQLException e){
               throw new RuntimeException(e);
           }
           return ps;
        });
    }
}
