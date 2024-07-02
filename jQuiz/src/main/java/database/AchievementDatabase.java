package database;

import models.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;

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
    public static final String IS_UNLOCKED = "is_unlocked";

    public AchievementDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

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

    public void initAchievements(int userId) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        List<Achievement> achievements = getAll();
        for(Achievement cur : achievements) {
            String query = String.format("INSERT INTO achToUser (%s, %s, %s, %s) VALUES (%d, %d, SYSDATE(), FALSE);",
                    USER_ID, ACH_ID, ACQUIRE_DATE, IS_UNLOCKED, userId, cur.getId());
            PreparedStatement statement = this.getStatement(query,con);
            int affectedRows = statement.executeUpdate();
            if(affectedRows == 0){
                throw new SQLException("Creating row failed");
            }
        }
        con.close();
    }

    @Override
    protected Achievement getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Achievement(
                rs.getInt(ID),
                rs.getString(NAME),
                rs.getString(DESCRIPTION),
                rs.getString(IMAGE),
                rs.getDate(ACQUIRE_DATE),
                rs.getBoolean(IS_UNLOCKED)
        );
    }
    public List<Achievement> getAll() throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM achievements;");
        List<Achievement> achievements = new ArrayList<>();
        while(rs.next()) {
            achievements.add(new Achievement(rs.getInt(ID), rs.getString(NAME),rs.getString(DESCRIPTION),rs.getString(IMAGE),new Date(),false));
        }
        con.close();
        return achievements;
    }
    public List<Achievement> getAchievementsByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT a.%s, a.%s, a.%s, a.%s, au.%s, au.%s, au.%s FROM %s a JOIN %s au ON a.%s = au.%s WHERE au.%s = ?;",
                ID, NAME, DESCRIPTION, IMAGE, USER_ID, ACQUIRE_DATE, IS_UNLOCKED, databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, USER_ID);
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public List<Achievement> getUnlockedAchievementsByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT a.%s, a.%s, a.%s, a.%s, au.%s, au.%s, au.%s FROM %s a JOIN %s au ON a.%s = au.%s WHERE au.%s = ? AND au.%s = TRUE;",
                ID, NAME, DESCRIPTION, IMAGE, USER_ID, ACQUIRE_DATE, IS_UNLOCKED, databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, USER_ID, IS_UNLOCKED);
        return queryToList(query, (ps) -> {
            try{
                ps.setInt(1, userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public void unlockAchievement(int userId, String achievementName) throws SQLException, ClassNotFoundException {
        Connection con = getConnection();
        String query = String.format("UPDATE %s a JOIN %s au ON a.%s = au.%s SET au.%s = TRUE WHERE au.%s = %d AND a.%s = '%s';",
                databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, IS_UNLOCKED, USER_ID, userId, NAME, achievementName);
        PreparedStatement statement = this.getStatement(query,con);
        int affectedRows = statement.executeUpdate();
        if(affectedRows == 0){
            throw new SQLException("Unlocking achievement failed.");
        }
        con.close();
    }
    public boolean hasAchievementUnlocked(int userId, String achievementName) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s a JOIN %s au ON a.%s = au.%s WHERE au.%s = %d AND a.%s = '%s' AND au.%s = TRUE;",
                databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, USER_ID, userId, NAME, achievementName, IS_UNLOCKED);
        Connection con = getConnection();
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(query);
        boolean hasUnlocked = rs.next();
        con.close();
        return hasUnlocked;
    }
}
