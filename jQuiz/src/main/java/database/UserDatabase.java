package database;

import models.User;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDatabase extends Database<User>{
    public static final String USERNAME_COL = "username";
    public static final String IS_ADMIN_COL = "is_admin";
    public static final String CREATED_AT_COL = "created_at";
    public static final String EMAIL_COL = "email";
    public static final String PASSWORD_COL = "pass";
    public static final String IMAGE_COL = "image";
    public UserDatabase(BasicDataSource dataSource, String databaseName) {
        super(dataSource, databaseName);
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

        User res = new User(
                rs.getInt("id"),
                rs.getString(USERNAME_COL),
                rs.getDate(CREATED_AT_COL),
                rs.getString(EMAIL_COL),
                rs.getString(PASSWORD_COL),
                rs.getString(IMAGE_COL)
        );
        return res;
    }
}
