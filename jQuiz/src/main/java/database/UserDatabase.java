package database;

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

import static database.AchievementDatabase.ACQUIRE_DATE;

public class UserDatabase extends Database<User>{
    public static final String ID = "ID";
    public static final String USERNAME_COL = "username";
    public static final String IS_ADMIN_COL = "is_admin";
    public static final String CREATED_AT_COL = "created_at";
    public static final String EMAIL_COL = "email";
    public static final String PASSWORD_COL = "pass";
    public static final String IMAGE_COL = "image";
    public static final String RECEIVER_ID = "receiver_id";
    public static final String SENDER_ID = "sender_id";
    public static final String TYPE = "type";
    public static final String QUIZ_ID = "quiz_id";
    public static final String TEXT = "text";
    public static final String USER_ID = "user_id";
    public static final String ACH_ID = "ach_id";

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
        int id = rs.getInt("id");

        List<Integer> friendIds = new ArrayList<Integer>();
        ResultSet rsFriends = getResultSet("SELECT * FROM friends WHERE user1_id = " + id);
        while (rsFriends.next()){
            friendIds.add(rsFriends.getInt("user2_id"));
        }

        User res = new User(
                rs.getInt("id"),
                rs.getString(USERNAME_COL),
                rs.getDate(CREATED_AT_COL),
                rs.getString(EMAIL_COL),
                rs.getString(PASSWORD_COL),
                rs.getString(IMAGE_COL),
                null,
                null,
                null
        );
        return res;
    }

    public List<Mail> getMailsByUserId(String userId, String sendOrReceive) throws SQLException, ClassNotFoundException {
        List<Mail> mails = new ArrayList<Mail>();
        ResultSet rsMails = getResultSet("SELECT * FROM " + Database.MAILS_DB + " WHERE " + (sendOrReceive == "SEND" ? SENDER_ID: RECEIVER_ID) + " = " + userId);
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
}
