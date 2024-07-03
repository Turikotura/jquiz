package accounts;

import database.UserDatabase;
import models.User;

import java.sql.SQLException;
import java.util.Date;

public class Register {
    public static final int USERNAME_EXISTS = 0;
    public static final int EMAIL_EXISTS = 1;
    public static final int DIFF_PASSWORDS = 2;
    public static final int WEAK_PASSWORD = 3;
    public static final int SUCCESS = 4;


    public static int createNew(String userName, String email, String password1,
                                String password2, byte[] imageLink, UserDatabase db) throws SQLException, ClassNotFoundException {
        User curUser = db.getByUsername(userName);
        User curUserByEmail = db.getByEmail(email);
        if(curUser != null) return USERNAME_EXISTS;
        else if(curUserByEmail != null) return EMAIL_EXISTS;
        else if(!password1.equals(password2)) return DIFF_PASSWORDS;
        else if(!Security.isStrong(password1)) return WEAK_PASSWORD;
        return SUCCESS;
    }
}
