package database;

import models.Quiz;
import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.List;

public class UserDatabase extends Database<User>{
    // User Columns
    public static final String ID = "ID";
    public static final String USERNAME = "username";
    public static final String IS_ADMIN = "is_admin";
    public static final String CREATED_AT = "created_at";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "pass";
    public static final String IMAGE = "image";
    public static final String IMAGE_URL = "image_url";
    // Friend Columns
    public static final String USER1_ID = "user1_id";
    public static final String USER2_ID = "user2_id";

    public UserDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public int add(User toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s, %s, %s ) VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                databaseName, USERNAME, IS_ADMIN, CREATED_AT, EMAIL, PASSWORD, IMAGE, IMAGE_URL);
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
        statement.setString(1, toAdd.getUsername());
        statement.setBoolean(2, toAdd.isAdmin());
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        statement.setString(4, toAdd.getEmail());
        statement.setString(5, toAdd.getPassword());
        statement.setBytes(6, toAdd.getImage());
        statement.setString(7, toAdd.getImageUrl());
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
    protected User getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new User(
                rs.getInt(ID),
                rs.getString(USERNAME),
                rs.getDate(CREATED_AT),
                rs.getString(EMAIL),
                rs.getString(PASSWORD),
                rs.getBytes(IMAGE),
                rs.getString(IMAGE_URL)
        );
    }

    public List<User> getFriendsByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                Database.FRIEND_DB, USER1_ID, userId);
        return queryToList(query);
    }

    public List<User> getHighestPerformers(int k, String fromLastDay) throws SQLException, ClassNotFoundException {
        String queryAddition = (fromLastDay.equals("LASTDAY") ? String.format("WHERE h.%s >= DATE_SUB(NOW(), INTERVAL 1 DAY)",HistoryDatabase.COMPLETED_AT) : "");
        String query = String.format(
                "SELECT u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, SUM(h.%s) AS sum_grade, SUM(h.%s) as sum_time FROM %s u LEFT JOIN %s h ON u.%s = h.%s %s GROUP BY u.%s, u.%s, u.%s, u.%s, u.%s, u.%s, u.%s ORDER BY sum_grade DESC, sum_time ASC LIMIT %d;",
                ID, USERNAME, IS_ADMIN, CREATED_AT, EMAIL, PASSWORD, IMAGE,
                HistoryDatabase.GRADE, HistoryDatabase.WRITING_TIME,
                databaseName,
                Database.HISTORY_DB, ID, HistoryDatabase.USER_ID,
                queryAddition,
                ID, USERNAME, IS_ADMIN, CREATED_AT, EMAIL, PASSWORD, IMAGE,
                k);
        return queryToList(query);
    }

    public User getByUsername(String username) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                Database.USER_DB,USERNAME,username);
        return queryToElement(query);
    }

    public User getByEmail(String email) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                databaseName, EMAIL, email);
        return queryToElement(query);
    }

    public List<User> searchUsers(int k, String searchString) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s LIKE '%%%s%%' LIMIT %d",
                databaseName, USERNAME, searchString, k);
        return queryToList(query);
    }

    public boolean addFriend(int from, int to) throws SQLException, ClassNotFoundException {
        String first = String.format("INSERT INTO %s ( %s, %s ) VALUES ( ?, ? )",
                Database.FRIEND_DB, USER1_ID, USER2_ID);
        String second = String.format("INSERT INTO %s ( %s, %s ) VALUES ( ?, ? )",
                Database.FRIEND_DB, USER1_ID, USER2_ID);
        Connection con = getConnection();
        PreparedStatement firstStatement = getStatement(first,con);
        PreparedStatement secondStatement = getStatement(second,con);

        firstStatement.setInt(1,from);
        firstStatement.setInt(2,to);
        secondStatement.setInt(1,to);
        secondStatement.setInt(2,from);

        boolean res = firstStatement.executeUpdate() > 0 && secondStatement.executeUpdate() > 0;
        con.close();
        return res;
    }
    public boolean checkAreFriends(int from, int to) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d",
                Database.FRIEND_DB, USER1_ID, from, USER2_ID, to);
        Connection con = getConnection();
        ResultSet rs = getResultSet(query, con);
        boolean res = rs.next();
        con.close();
        return res;
    }
}
