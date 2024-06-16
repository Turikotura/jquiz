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
    // History Columns
    public static final String USER_ID_COL = "user_id";
    public static final String QUIZ_ID_COL = "quiz_id";
    public static final String GRADE_COL = "grade";
    public static final String COMPLETED_AT_COL = "completed_at";
    public static final String WRITING_TIME_COL = "writing_time";
    public static final String USER_ID = "user_id";
    public static final String ACH_ID = "ach_id";

    AchievementDatabase achievementDB;
    public UserDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
        achievementDB = new AchievementDatabase(dataSource, Database.ACHIEVEMENT_DB);
    }

    @Override
    public boolean add(User toAdd) throws SQLException, ClassNotFoundException {
        String query = String.format("INSERT INTO %s ( %s, %s, %s, %s, %s, %s ) VALUES ('%s', %b, %s, '%s', '%s', '%s')", databaseName,
                USERNAME_COL, IS_ADMIN_COL, CREATED_AT_COL, EMAIL_COL, PASSWORD_COL, IMAGE_COL,
                toAdd.getUsername(), toAdd.isAdmin(), "sysdate()", toAdd.getEmail(), toAdd.getPassword(), toAdd.getImage());
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

    public List<History> getHistoryByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<History> histories = new ArrayList<History>();
        ResultSet rsHistories = getResultSet("SELECT * FROM " + Database.HISTORY_DB + " WHERE user_id = " + userId);
        while (rsHistories.next()) {
            History history = new History(
                    rsHistories.getInt("id"),
                    rsHistories.getInt(USER_ID_COL),
                    rsHistories.getInt(QUIZ_ID_COL),
                    rsHistories.getInt(GRADE_COL),
                    rsHistories.getDate(COMPLETED_AT_COL),
                    rsHistories.getInt(WRITING_TIME_COL)
            );

            histories.add(history);
        }
        return histories;
    }
    public List<Achievement> getAchievementsByUserId(int userId) throws SQLException, ClassNotFoundException {
        List<Achievement> userAchievements = new ArrayList<Achievement>();
        ResultSet rsAchievements = getResultSet("SELECT * FROM " + Database.ACH_TO_USR_DB + " WHERE " + USER_ID + " = " + userId);
        List<Achievement> achievements = achievementDB.getAll();
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

    public User getByUsername(String username) throws SQLException, ClassNotFoundException {
        ResultSet usersFound = getResultSet("SELECT * FROM " + Database.USER_DB + " WHERE " + USERNAME_COL + " = '" + username + "';");
        if(usersFound.next()) return getItemFromResultSet(usersFound);
        return null;
    }
}
