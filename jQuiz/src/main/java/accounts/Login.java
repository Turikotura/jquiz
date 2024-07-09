package accounts;

import database.UserDatabase;
import models.User;

import java.sql.SQLException;

public class Login {
    // Constants for possible log in results
    public static final int NO_USER = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int USER_BANNED = 2;
    public static final int SUCCESS = 3;

    /**
     * Tries to log in the user to the website
     * @param userName Username of user trying to log in
     * @param password Password the user entered when they tried to log in
     * @param db Database of users of the website
     * @return NO_USER if such username doesn't exist, WRONG_PASSWORD if passwords don't match,
     * USER_BANNED if the user has been banned, SUCCESS otherwise
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static int login(String userName, String password, UserDatabase db) throws SQLException, ClassNotFoundException {
        password = Security.getHash(password);
        User curUser = db.getByUsername(userName);
        if(curUser == null) return NO_USER; // Make sure the username exists
        else if(!password.equals(curUser.getPassword())) return WRONG_PASSWORD; // Make sure the password is correct
        else if(db.isUsernameBanned(userName)) return USER_BANNED; // Make sure the user is not banned
        return SUCCESS;
    }
}
