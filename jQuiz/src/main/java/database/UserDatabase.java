package database;

import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    // Friend Columns
    public static final String USER1_ID = "user1_id";
    public static final String USER2_ID = "user2_id";

    public UserDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public int add(User toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s, %s ) VALUES ( ?, ?, ?, ?, ?, ?)",
                databaseName, USERNAME, IS_ADMIN, CREATED_AT, EMAIL, PASSWORD, IMAGE);
        PreparedStatement statement = getStatement(query);
        statement.setString(1, toAdd.getUsername());
        statement.setBoolean(2, toAdd.isAdmin());
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        statement.setString(4, toAdd.getEmail());
        statement.setString(5, toAdd.getPassword());
        statement.setString(6, toAdd.getImage());
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Creating row failed");
        }
        try(ResultSet keys = statement.getGeneratedKeys()){
            if(keys.next()){
                return keys.getInt(1);
            }else{
                throw new SQLException("Creating row failed");
            }
        }
    }

    @Override
    protected User getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        System.out.println(rs.getString(USERNAME));
        return new User(
                rs.getInt(ID),
                rs.getString(USERNAME),
                rs.getDate(CREATED_AT),
                rs.getString(EMAIL),
                rs.getString(PASSWORD),
                rs.getString(IMAGE)
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
        ResultSet usersFound = getResultSet("SELECT * FROM " + Database.USER_DB + " WHERE " + USERNAME + " = '" + username + "';");
        if(usersFound.next()) return getItemFromResultSet(usersFound);
        return null;
    }

    public User getByEmail(String email) throws SQLException, ClassNotFoundException {
        ResultSet emailsFound = getResultSet("SELECT * FROM " + Database.USER_DB + " WHERE " + EMAIL + " = '" + email + "';");
        if(emailsFound.next()) return getItemFromResultSet(emailsFound);
        return null;
    }
}
