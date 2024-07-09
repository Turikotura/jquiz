package database;

import models.Achievement;
import models.Quiz;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AchievementDatabase extends Database<Achievement> {
    // Achievement Columns
    static final String ID = "id";
    static final String NAME = "name";
    static final String DESCRIPTION = "description";
    static final String IMAGE = "image";
    // Ach_To_User Columns
    public static final String USER_ID = "user_id";
    public static final String ACH_ID = "ach_id";
    static final String ACQUIRE_DATE = "acquire_date";

    public AchievementDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Adds new achievement to achievements table
     * @param achievement new Achievement object
     * @return id of new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    public int add(Achievement achievement) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s )" +
                        "VALUES ( ?,  ?,  ?  )",
                databaseName, NAME, DESCRIPTION, IMAGE);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        statement.setString(1,achievement.getName());
        statement.setString(2,achievement.getDescription());
        statement.setString(3,achievement.getImage());

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
     * Assembles Achievement object from ResultSet
     * @param rs ResultSet of achievements table rows
     * @return Achievement object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Achievement getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Achievement(
                rs.getInt(ID),
                rs.getString(NAME),
                rs.getString(DESCRIPTION),
                rs.getString(IMAGE),
                new Date()
        );
    }

    /**
     * Returns Achievement which has the name passed
     * @param name Name of the achievement
     * @return Achievement object of passed name
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public Achievement getAchievementByName(String name) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                Database.ACHIEVEMENT_DB, NAME);
        return queryToElement(query, (ps) -> {
            try {
                ps.setString(1, name);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    /**
     * Get every achievement user has unlocked
     * @param userId Id of the user
     * @return List of user's achievements
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Achievement> getAchievementsByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT a.%s, a.%s, a.%s, a.%s, au.%s, au.%s FROM %s a JOIN %s au ON a.%s = au.%s WHERE au.%s = ?;",
                ID, NAME, DESCRIPTION, IMAGE, USER_ID, ACQUIRE_DATE, databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, USER_ID);
        return queryToList(query, (ps) -> {
            try{
                ps.setInt(1, userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }

    /**
     * Unlocks achievement for the user
     * @param userId Id of the user
     * @param achievementName Name of the achievement
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void unlockAchievement(int userId, String achievementName) throws SQLException, ClassNotFoundException {
        Achievement achievement = getAchievementByName(achievementName);
        String query = String.format("INSERT INTO %s ( %s, %s, %s) VALUES(?, ?, ?);",
                Database.ACH_TO_USR_DB, USER_ID, ACH_ID, ACQUIRE_DATE);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        statement.setInt(1, userId);
        statement.setInt(2, achievement.getId());
        statement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Unlocking achievement failed.");
        }
        con.close();
    }

    /**
     * Checks if the user has achievement unlocked
     * @param userId Id of the user
     * @param achievementName Name of the achievement
     * @return true if the user has achievement unlocked, false otherwise
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean hasAchievementUnlocked(int userId, String achievementName) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s a JOIN %s au ON a.%s = au.%s WHERE au.%s = ? AND a.%s = ?;",
                databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, USER_ID, NAME);
        List<Achievement> achievements = queryToList(query, (ps) -> {
            try {
                ps.setInt(1, userId);
                ps.setString(2, achievementName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
        return achievements.size() > 0;
    }

    public List<Achievement> getLatestFriendAchsByUserId(int userId, int k) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s IN (SELECT %s FROM %s WHERE %s = ?) ORDER BY %s DESC LIMIT ?",
                databaseName, USER_ID, UserDatabase.USER2_ID, FRIEND_DB, UserDatabase.USER1_ID, ACQUIRE_DATE
        );
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1, userId);
                ps.setInt(2,k);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
}
