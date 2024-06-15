package database;

import models.Mail;
import models.MailTypes;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MailDatabase extends Database<Mail> {
    // Mail Columns
    public static final String RECEIVER_ID = "receiver_id";
    public static final String SENDER_ID = "sender_id";
    public static final String TYPE = "type";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TEXT = "text";
    public MailDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
    }
    @Override
    public boolean add(Mail toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s ) VALUES ( %d, %d, %d, %d, %s )", databaseName,
                RECEIVER_ID, SENDER_ID, TYPE, QUIZ_ID, TEXT,
                toAdd.getReceiverId(), toAdd.getSenderId(), toAdd.getType().ordinal(), toAdd.getQuizId(), toAdd.getText());
        PreparedStatement statement = getStatement(query);
        return statement.executeUpdate() > 0;
    }

    @Override
    protected Mail getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new Mail(
                rs.getInt("id"),
                rs.getInt(SENDER_ID),
                rs.getInt(RECEIVER_ID),
                MailTypes.values()[rs.getInt(TYPE)],
                rs.getInt(QUIZ_ID),
                rs.getString(TEXT)
        );
    }
    public List<Mail> getMailsByUserId(int userId, String sendOrReceive) throws SQLException, ClassNotFoundException {
        List<Mail> mails = new ArrayList<Mail>();
        ResultSet rsMails = getResultSet("SELECT * FROM " + Database.MAIL_DB + " WHERE " + (Objects.equals(sendOrReceive, "SEND") ? SENDER_ID: RECEIVER_ID) + " = " + userId);
        while (rsMails.next()){
            mails.add(getItemFromResultSet(rsMails));
        }
        return mails;
    }
}