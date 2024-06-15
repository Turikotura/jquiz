package database;

import models.History;
import models.Achievement;
import models.Mail;
import models.MailTypes;
import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static database.AchievementDatabase.ACQUIRE_DATE;

public class UserDatabase extends Database<User>{
    // User Columns
    public static final String ID = "ID";
    public static final String USERNAME_COL = "username";
    public static final String IS_ADMIN_COL = "is_admin";
    public static final String CREATED_AT_COL = "created_at";
    public static final String EMAIL_COL = "email";
    public static final String PASSWORD_COL = "pass";
    public static final String IMAGE_COL = "image";
    // Mail Columns
    public static final String RECEIVER_ID = "receiver_id";
    public static final String SENDER_ID = "sender_id";
    public static final String TYPE = "type";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TEXT = "text";

    AchievementDatabase achievementDB;
    public UserDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
        achievementDB = new AchievementDatabase(dataSource, Database.ACHIEVEMENT_DB);
    }

    @Override
    public boolean add(User toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s, %s ) VALUES ( %s, %b, %s, %s, %s, %s)", databaseName,
                USERNAME_COL, IS_ADMIN_COL, CREATED_AT_COL, EMAIL_COL, PASSWORD_COL, IMAGE_COL,
                toAdd.getUsername(), toAdd.isAdmin(), toAdd.getCreated_at(), toAdd.getEmail(), toAdd.getPassword(), toAdd.getImage());
        PreparedStatement statement = getStatement(query);
        return statement.executeUpdate() > 0;
    }

    @Override
    protected User getItemFromResultSet(ResultSet rs) throws SQLException, ClassNotFoundException {
        return new User(
                rs.getInt("id"),
                rs.getString(USERNAME_COL),
                rs.getDate(CREATED_AT_COL),
                rs.getString(EMAIL_COL),
                rs.getString(PASSWORD_COL),
                rs.getString(IMAGE_COL)
        );
    }

    public List<Mail> getMailsByUserId(int userId, String sendOrReceive) throws SQLException, ClassNotFoundException {
        List<Mail> mails = new ArrayList<Mail>();
        ResultSet rsMails = getResultSet("SELECT * FROM " + Database.MAIL_DB + " WHERE " + (Objects.equals(sendOrReceive, "SEND") ? SENDER_ID: RECEIVER_ID) + " = " + userId);
        while (rsMails.next()){
            Mail mail = new Mail(
                    rsMails.getInt(ID),
                    rsMails.getInt(SENDER_ID),
                    rsMails.getInt(RECEIVER_ID),
                    MailTypes.values()[rsMails.getInt(TYPE)],
                    rsMails.getInt(QUIZ_ID),
                    rsMails.getString(TEXT)
                    );

            mails.add(mail);
        }
        return mails;
    }

    public List<User> getFriendsByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<User> users = new ArrayList<User>();
        ResultSet rsUsers = getResultSet("SELECT * FROM " + Database.FRIEND_DB + " WHERE user1_id = " + userId);
        while (rsUsers.next()){
            User user = new User(
                    rsUsers.getInt("id"),
                    rsUsers.getString(USERNAME_COL),
                    rsUsers.getDate(CREATED_AT_COL),
                    rsUsers.getString(EMAIL_COL),
                    rsUsers.getString(PASSWORD_COL),
                    rsUsers.getString(IMAGE_COL)
            );

            users.add(user);
        }
        return users;
    }

    public List<User> getHighestPerformers(int k, String fromLastDay) throws SQLException, ClassNotFoundException {
        List<User> users = new ArrayList<>();
        ResultSet rsUsers = getResultSet(
                " SELECT u.id, u.username, u.is_admin, u.created_at, u.email, u.pass, u.image," +
                " SUM(h.grade) AS sum_grade, SUM(h.writing_time) as sum_time" +
                " FROM " + Database.USER_DB +" u" +
                " LEFT JOIN " + Database.HISTORY_DB + " h ON u.id = h.user_id" +
                        (fromLastDay.equals("lastday") ? " WHERE h.completed_at >= DATE_SUB(NOW(), INTERVAL 1 DAY)" : "") +
                " GROUP BY u.id, u.username, u.is_admin, u.created_at, u.email, u.pass, u.image" +
                " ORDER BY sum_grade DESC, sum_time ASC" +
                " LIMIT " + k + ";");
        while(rsUsers.next()){
            users.add(getItemFromResultSet(rsUsers));
        }
        return users;
    }
}