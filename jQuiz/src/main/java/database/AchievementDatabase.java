package database;

import models.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.xml.crypto.Data;
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
    public boolean add(Achievement achievement) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO achievements (%s, %s, %s) VALUES ('%s', '%s', '%s')",
                NAME, DESCRIPTION, IMAGE, achievement.getName(), achievement.getDescription(), achievement.getImage());
        PreparedStatement statement = this.getStatement(query);
        int affectedRows = statement.executeUpdate();
        return affectedRows > 0;
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
        // Needs better sql query
        List<Achievement> userAchievements = new ArrayList<Achievement>();
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                Database.ACH_TO_USR_DB, USER_ID, userId);
        ResultSet rsAchievements = getResultSet(query);
        List<Achievement> achievements = getAll();
        while (rsAchievements.next()){
            int achId = rsAchievements.getInt(ACH_ID);
            for(Achievement ach: achievements){
                if(ach.getId() == achId){
                    Achievement achievement = new Achievement(
                            achId,
                            ach.getName(),
                            ach.getDescription(),
                            ach.getImage(),
                            rsAchievements.getDate(ACQUIRE_DATE)
                    );
                    userAchievements.add(achievement);
                    break;
                }
            }
        }
        return userAchievements;
    }
}
