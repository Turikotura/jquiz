package accounts;

import database.UserDatabase;
import models.User;

import java.sql.SQLException;
import java.util.Date;

public class Register {
    // Constants ofr possible registration results
    public static final int USERNAME_EXISTS = 0;
    public static final int EMAIL_EXISTS = 1;
    public static final int DIFF_PASSWORDS = 2;
    public static final int WEAK_PASSWORD = 3;
    public static final int EMAIL_BANNED = 4;
    public static final int SUCCESS = 5;
    public static final int NO_EMAIL = 6;

    /**
     * Attempt to create new account and return the outcome
     * @param userName Name new user has chosen
     * @param email Email address of new user
     * @param password1 The password the user wants to use
     * @param password2 Repeated password
     * @param db Database of users
     * @return USERNAME_EXISTS if passed username already exists, EMAIL_EXISTS if an account with that email already exists,
     * DIFF_PASSWORDS if passwords don't match, WEAK_PASSWORD if the password is weak, EMAIL_BANNED if an user with this address
     * has been banned, SUCCESS otherwise
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static int createNew(String userName, String email, String password1,
                                String password2, UserDatabase db) throws SQLException, ClassNotFoundException {
        User curUser = db.getByUsername(userName);
        User curUserByEmail = db.getByEmail(email);
        if(curUser != null) return USERNAME_EXISTS;
        else if(curUserByEmail != null) return EMAIL_EXISTS;
        else if(!password1.equals(password2)) return DIFF_PASSWORDS;
        else if(!Security.isStrong(password1)) return WEAK_PASSWORD;
        else if(db.isEmailBanned(email)) return EMAIL_BANNED;
        else if(email.isEmpty()) return NO_EMAIL;
        return SUCCESS;
    }
}
