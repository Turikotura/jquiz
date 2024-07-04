package database;

import models.Mail;
import models.MailTypes;
import org.apache.commons.dbcp2.BasicDataSource;
import java.util.Date;
import java.sql.*;
import java.util.List;
import java.util.Objects;

public class  MailDatabase extends Database<Mail> {
    // Mail Columns
    public static final String  ID = "id";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String SENDER_ID = "sender_id";
    public static final String TYPE = "type";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TEXT = "text";
    public static final String TIME_SENT = "time_sent";
    public static final String SEEN = "seen";
    public MailDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }
    @Override
    public int add(Mail toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format(
                "INSERT INTO %s ( %s, %s, %s, %s, %s, %s, %s )" +
                        "VALUES ( ?,  ?,  ?,  ?,  ?,  ?,  ?  )",
                databaseName, RECEIVER_ID, SENDER_ID, TYPE, QUIZ_ID, TEXT, TIME_SENT, SEEN);
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
        statement.setInt(1, toAdd.getReceiverId());
        statement.setInt(2, toAdd.getSenderId());
        statement.setInt(3, toAdd.getType().ordinal());
        statement.setInt(4, toAdd.getQuizId());
        statement.setString(5, toAdd.getText());
        statement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
        statement.setBoolean(7, false);

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
    protected Mail getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Mail(
                rs.getInt(ID),
                rs.getInt(SENDER_ID),
                rs.getInt(RECEIVER_ID),
                MailTypes.values()[rs.getInt(TYPE)],
                rs.getInt(QUIZ_ID),
                rs.getString(TEXT),
                rs.getDate(TIME_SENT),
                rs.getBoolean(SEEN)
        );
    }
    public List<Mail> getMailsByUserId(int userId, String sendOrReceive) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ?",
                databaseName, (Objects.equals(sendOrReceive, "SEND") ? SENDER_ID : RECEIVER_ID));
        return queryToList(query, (ps) -> {
            try {
                ps.setInt(1,userId);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        });
    }
    public boolean friendRequestPending(int from, int to) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = ? AND %s = ? and %s = ?",
                databaseName, SENDER_ID, RECEIVER_ID, TYPE);
        return !queryToList(query, (ps) -> {
            try{
                ps.setInt(1,from);
                ps.setInt(2, to);
                ps.setInt(3, MailTypes.FRIEND_REQUEST.ordinal());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ps;
        }).isEmpty();
    }

    public void sendAchievementMail(int systemId, int userId, String achievementName) throws SQLException, ClassNotFoundException {
        Mail unlockMail = new Mail(-1,systemId,userId,MailTypes.DEFAULT,-1,
                "Congratulations! " + achievementName + " unlocked. Go to achievements page to see more.",new Date(),false);
        add(unlockMail);
    }
}