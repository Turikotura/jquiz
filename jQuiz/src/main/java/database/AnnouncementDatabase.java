package database;

import models.Announcement;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.List;

public class AnnouncementDatabase extends Database<Announcement> {
    static final String ID = "id";
    static final String TITLE = "title";
    static final String TEXT = "text";
    static final String AUTHOR_ID = "author_id";
    static final String CREATED_AT = "created_at";
    public AnnouncementDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }

    /**
     * Add new announcement to announcements table
     * @param toAdd new Announcement object
     * @return id of new row
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    public int add(Announcement toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s (%s, %s, %s, %s) VALUES (?,?,?,?);",
                Database.ANNOUNCEMENT_DB, AUTHOR_ID, TITLE, TEXT, CREATED_AT);
        Connection con = getConnection();
        PreparedStatement statement = this.getStatement(query,con);
        statement.setInt(1,toAdd.getAuthorId());
        statement.setString(2, toAdd.getTitle());
        statement.setString(3, toAdd.getText());
        statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
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
     * Assembles Announcement object from ResultSet
     * @param rs ResultSet of announcements table rows
     * @return Announcement Object
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    @Override
    protected Announcement getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Announcement(
                rs.getInt(ID),
                rs.getInt(AUTHOR_ID),
                rs.getString(TITLE),
                rs.getString(TEXT),
                rs.getDate(CREATED_AT)
        );
    }

    /**
     * Get three latest announcements published
     * @return List of three latest announcements
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Announcement> getThreeAnnouncements() throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 3;", Database.ANNOUNCEMENT_DB, CREATED_AT);
        return queryToList(query, (ps) -> {return ps;});
    }
}
