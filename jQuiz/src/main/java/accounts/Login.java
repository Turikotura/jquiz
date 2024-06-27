package accounts;

import database.UserDatabase;
import models.User;

import java.sql.SQLException;

public class Login {
    public static final int NO_USER = 0;
    public static final int WRONG_PASSWORD = 1;
    public static final int SUCCESS = 2;
    public static int login(String userName, String password, UserDatabase db) throws SQLException, ClassNotFoundException {
        password = Security.getHash(password);
        User curUser = db.getByUsername(userName);
        if(curUser == null) return NO_USER;
        else if(!password.equals(curUser.getPassword())) return WRONG_PASSWORD;
        return SUCCESS;
    }
}
