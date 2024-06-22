package database;

import models.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    @Override
    public int add(Achievement achievement) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO achievements (%s, %s, %s) VALUES ('%s', '%s', '%s')",
                NAME, DESCRIPTION, IMAGE, achievement.getName(), achievement.getDescription(), achievement.getImage());
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
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
    protected Achievement getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Achievement(
                rs.getInt(ID),
                rs.getString(NAME),
                rs.getString(DESCRIPTION),
                rs.getString(IMAGE),
                rs.getDate(ACQUIRE_DATE)
        );
    }
    public List<Achievement> getAchievementsByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT a.%s, a.%s, a.%s, a.%s, au.%s, au.%s FROM %s a JOIN %s au ON a.%s = au.%s WHERE au.%s = %d;",
                ID, NAME, DESCRIPTION, IMAGE, USER_ID, ACQUIRE_DATE, databaseName, Database.ACH_TO_USR_DB, ID, ACH_ID, USER_ID, userId);
        return queryToList(query,getConnection());
    }
}
