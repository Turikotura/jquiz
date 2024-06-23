package database;

import models.Mail;
import models.MailTypes;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailDatabase extends Database<Mail> {
    // Mail Columns
    public static final String  ID = "id";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String SENDER_ID = "sender_id";
    public static final String TYPE = "type";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TEXT = "text";
    public static final String TIME_SENT = "time_sent";
    public MailDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }
    @Override
    public int add(Mail toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s, %s ) VALUES ( %d, %d, %d, %d, '%s', %s)", databaseName,
                RECEIVER_ID, SENDER_ID, TYPE, QUIZ_ID, TEXT, TIME_SENT,
                toAdd.getReceiverId(), toAdd.getSenderId(), toAdd.getType().ordinal(), toAdd.getQuizId(), toAdd.getText(), toAdd.getTimeSent());
        Connection con = getConnection();
        PreparedStatement statement = getStatement(query,con);
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
                rs.getDate(TIME_SENT)
        );
    }
    public List<Mail> getMailsByUserId(int userId, String sendOrReceive) throws SQLException, ClassNotFoundException {
        String query = String.format("SELECT * FROM %s WHERE %s = %d",
                databaseName, (Objects.equals(sendOrReceive, "SEND") ? SENDER_ID : RECEIVER_ID), userId);
        return queryToList(query);
    }
}