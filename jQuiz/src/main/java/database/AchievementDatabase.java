package database;

import models.Achievement;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AchievementDatabase extends Database<Achievement> {
    static final String ID = "id";
    static final String NAME = "name";
    static final String DESCRIPTION = "description";
    static final String IMAGE = "image";
    static final String ACQUIRE_DATE = "unlock_date";

    public AchievementDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    @Override
    public boolean add(Achievement achievement) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO achievements (%s, %s, %s, %s) VALUES ('%s', '%s', '%s', %s)",
                NAME, DESCRIPTION, IMAGE, ACQUIRE_DATE, achievement.getName(), achievement.getDescription(), achievement.getImage(), "sysdate()");
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
}
