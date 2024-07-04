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
    // Banned Columns
    public static final String USER_ID = "user_id";

    public UserDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public int add(User toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s, %s, %s, %s, %s ) " +
                        "VALUES ( ?,  ?,  ?,  ?,  ?,  ?,  ?  )",
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
        String query = String.format("SELECT * FROM %s where %s IN (SELECT %s from %s where %s = ?)",
                Database.USER_DB, ID, USER2_ID, Database.FRIEND_DB, USER1_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
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
        return queryToList(query, (ps) -> {return ps;});
    }

    public User getByUsername(String username) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?;",
                Database.USER_DB,USERNAME);
        return queryToElement(query, (ps) -> {
            try {
                ps.setString(1,username);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public User getByEmail(String email) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?;",
                databaseName, EMAIL);
        return queryToElement(query, (ps) -> {
            try {
                ps.setString(1,email);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    public List<User> searchUsers(int k, String searchString) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s LIKE ? LIMIT %d",
                databaseName, USERNAME, k);
        return queryToList(query, (ps) -> {
            try {
                ps.setString(1, "%" + searchString + "%");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
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
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ?",
                Database.FRIEND_DB, USER1_ID, USER2_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setInt(1,from);
        ps.setInt(2,to);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        boolean res = rs.next();
        con.close();
        return res;
    }
    public boolean removeFriends(int from, int to) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "DELETE FROM %s WHERE (%s = ? AND %s = ?) OR (%s = ? AND %s = ?);",
                Database.FRIEND_DB, USER1_ID, USER2_ID, USER1_ID, USER2_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setInt(1,from);
        ps.setInt(2,to);
        ps.setInt(3,to);
        ps.setInt(4,from);

        boolean res = ps.executeUpdate() > 0;
        con.close();
        return res;
    }
    public boolean isUserAdmin(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = TRUE;",
                USER_DB, ID, IS_ADMIN);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setInt(1,userId);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        boolean res = rs.next();
        con.close();
        return res;
    }

    public void promoteUserToAdmin(int userId) throws SQLException, ClassNotFoundException {
        String statement = String.format("UPDATE %s SET %s = TRUE WHERE %s = ?;",
                USER_DB, IS_ADMIN, ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(statement,con);
        ps.setInt(1,userId);
        ps.execute();
        con.close();
    }

    public List<User> getAllAdmins() throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = TRUE;",
                USER_DB,IS_ADMIN);
        return queryToList(query, (ps) -> {return ps;});
    }

    public void banUser(int userId) throws SQLException, ClassNotFoundException {
        String statement = String.format("INSERT INTO %s (%s) VALUES (?)",
                BANNED_DB, USER_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(statement,con);
        ps.setInt(1,userId);
        ps.execute();
        con.close();
        removeFromFriendsDB(userId);
    }
    public boolean isUsernameBanned(String username) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s b JOIN %s u ON b.%s = u.%s WHERE u.%s = ?;",
                BANNED_DB, USER_DB, USER_ID, ID, USERNAME);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setString(1,username);
        ResultSet rs = ps.executeQuery();
        boolean isBanned = rs.next();
        con.close();
        return isBanned;
    }

    public boolean isEmailBanned(String email) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s b JOIN %s u ON b.%s = u.%s WHERE u.%s = ?;",
                BANNED_DB, USER_DB, USER_ID, ID, EMAIL);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(query,con);
        ps.setString(1,email);
        ResultSet rs = ps.executeQuery();
        boolean isBanned = rs.next();
        con.close();
        return isBanned;
    }

    private void removeFromFriendsDB(int userId) throws SQLException, ClassNotFoundException {
        String statement = String.format("DELETE FROM %s WHERE %s = ? OR %s = ?;",
                FRIEND_DB, USER1_ID, USER2_ID);
        Connection con = getConnection();
        PreparedStatement ps = getStatement(statement,con);
        ps.setInt(1,userId);
        ps.setInt(2,userId);
        ps.execute();
        con.close();
    }
}
